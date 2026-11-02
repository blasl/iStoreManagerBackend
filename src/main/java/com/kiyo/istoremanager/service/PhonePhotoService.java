package com.kiyo.istoremanager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kiyo.istoremanager.dto.PhotoResponse;
import com.kiyo.istoremanager.model.Phone;
import com.kiyo.istoremanager.model.PhonePhoto;
import com.kiyo.istoremanager.model.PhoneStatus;
import com.kiyo.istoremanager.model.User;
import com.kiyo.istoremanager.repository.PhonePhotoRepository;
import com.kiyo.istoremanager.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PhonePhotoService {

    public static final int MAX_PHOTOS = 5;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Logger log = LoggerFactory.getLogger(PhonePhotoService.class);

    private final PhonePhotoRepository photoRepository;
    private final PhoneRepository phoneRepository;
    private final Cloudinary cloudinary;

    public PhonePhotoService(PhonePhotoRepository photoRepository,
                             PhoneRepository phoneRepository,
                             Cloudinary cloudinary) {
        this.photoRepository = photoRepository;
        this.phoneRepository = phoneRepository;
        this.cloudinary = cloudinary;
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> list(Long phoneId, User user) {
        findOwnedPhone(phoneId, user);
        return photoRepository.findByPhoneIdOrderByPositionAscIdAsc(phoneId).stream()
                .map(PhotoResponse::from).toList();
    }

    @Transactional
    public PhotoResponse upload(Long phoneId, MultipartFile file, User user) {
        Phone phone = phoneRepository.findByIdAndUserIdForUpdate(phoneId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));
        requireNotDeleted(phone);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se recibió ninguna imagen");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido. Usa JPG, PNG o WEBP");
        }

        long count = photoRepository.countByPhoneId(phoneId);
        if (count >= MAX_PHOTOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Máximo " + MAX_PHOTOS + " fotos por teléfono");
        }

        Map<?, ?> result;
        try {
            result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "istoremanager/user_" + user.getId(),
                    "resource_type", "image",
                    "allowed_formats", "jpg,jpeg,png,webp"));
        } catch (IOException | RuntimeException e) {
            log.error("Cloudinary upload failed for phone {}", phoneId, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo subir la imagen. Inténtalo de nuevo");
        }

        String publicId = (String) result.get("public_id");
        PhonePhoto photo = new PhonePhoto();
        photo.setPhone(phone);
        photo.setUrl((String) result.get("secure_url"));
        photo.setPublicId(publicId);
        photo.setPosition((int) count);

        try {
            return PhotoResponse.from(photoRepository.saveAndFlush(photo));
        } catch (RuntimeException e) {
            destroyQuietly(publicId);
            throw e;
        }
    }

    @Transactional
    public void delete(Long phoneId, Long photoId, User user) {
        findOwnedPhone(phoneId, user);
        PhonePhoto photo = photoRepository.findByIdAndPhoneId(photoId, phoneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada"));

        photoRepository.delete(photo);

        List<PhonePhoto> rest = photoRepository.findByPhoneIdOrderByPositionAscIdAsc(phoneId);
        for (int i = 0; i < rest.size(); i++) {
            rest.get(i).setPosition(i);
        }

        destroyQuietly(photo.getPublicId());
    }

    @Transactional
    public List<PhotoResponse> reorder(Long phoneId, List<Long> photoIds, User user) {
        findOwnedPhone(phoneId, user);
        List<PhonePhoto> photos = photoRepository.findByPhoneIdOrderByPositionAscIdAsc(phoneId);

        Set<Long> current = photos.stream().map(PhonePhoto::getId).collect(Collectors.toSet());
        if (photoIds == null || photoIds.size() != photos.size() || !current.equals(new HashSet<>(photoIds))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El orden enviado no coincide con las fotos del teléfono");
        }

        Map<Long, PhonePhoto> byId = photos.stream()
                .collect(Collectors.toMap(PhonePhoto::getId, Function.identity()));
        for (int i = 0; i < photoIds.size(); i++) {
            byId.get(photoIds.get(i)).setPosition(i);
        }
        return photoIds.stream().map(byId::get).map(PhotoResponse::from).toList();
    }

    private Phone findOwnedPhone(Long phoneId, User user) {
        Phone phone = phoneRepository.findByIdAndUserId(phoneId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado"));
        requireNotDeleted(phone);
        return phone;
    }

    private void requireNotDeleted(Phone phone) {
        if (phone.getStatus() == PhoneStatus.ELIMINADO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teléfono no encontrado");
        }
    }

    private void destroyQuietly(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image", "invalidate", true));
        } catch (IOException | RuntimeException e) {
            log.warn("Could not delete Cloudinary asset {}", publicId, e);
        }
    }
}