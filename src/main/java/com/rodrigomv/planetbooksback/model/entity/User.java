package com.rodrigomv.planetbooksback.model.entity;

import com.rodrigomv.planetbooksback.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Entidad que representa un usuario del sistema.
 * - Almacena contraseña en hash (BCrypt)
 * - Cada usuario tiene un rol (ADMIN o USER)
 * - Email es único en el sistema
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar en blanco")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar en blanco")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

