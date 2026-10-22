package com.kiyo.istoremanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Por ahora sin roles complejos
    }

    @Override
    @NonNull
    public String getUsername() {
        return email; // Usamos el email como identificador de usuario
    }

}
