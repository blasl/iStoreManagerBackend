package com.kiyo.istoremanager.dto;

import com.kiyo.istoremanager.model.PhonePhoto;
import com.kiyo.istoremanager.service.PhotoUrls;

public record PhotoResponse(Long id, String url, String thumbUrl, int position) {
    public static PhotoResponse from(PhonePhoto p) {
        return new PhotoResponse(p.getId(), PhotoUrls.full(p.getUrl()), PhotoUrls.thumb(p.getUrl()), p.getPosition());
    }
}