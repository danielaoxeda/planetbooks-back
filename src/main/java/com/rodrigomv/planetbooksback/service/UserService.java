package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.UserDTO;
import com.rodrigomv.planetbooksback.model.dto.UserRegistrationDTO;
import com.rodrigomv.planetbooksback.model.enums.Role;
import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de usuarios.
 * Ejemplo de implementación de lógica de negocio.
 *
 * Funcionalidades:
 * - Registro de usuarios
 * - Búsqueda de usuarios
 * - Actualización de usuarios
 * - Eliminación de usuarios (soft delete)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario.
     *
     * @param registrationDTO datos de registro
     * @return DTO del usuario creado
     * @throws IllegalArgumentException si el email ya existe
     */
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Intentando registrar usuario con email: {}", registrationDTO.getEmail());

        // Validar que el email no exista
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            log.warn("Email ya existe: {}", registrationDTO.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = User.builder()
            .name(registrationDTO.getName())
            .email(registrationDTO.getEmail())
            .password(passwordEncoder.encode(registrationDTO.getPassword()))
            .role(Role.USER)
            .enabled(true)
            .build();

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    /**
     * Obtiene un usuario por ID.
     *
     * @param id ID del usuario
     * @return DTO del usuario
     */
    public UserDTO getUserById(Long id) {
        log.debug("Buscando usuario con ID: {}", id);

        return userRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado: {}", id);
                return new IllegalArgumentException("Usuario no encontrado");
            });
    }

    /**
     * Obtiene un usuario por email.
     *
     * @param email email del usuario
     * @return Optional con el DTO del usuario
     */
    public Optional<UserDTO> getUserByEmail(String email) {
        log.debug("Buscando usuario con email: {}", email);

        return userRepository.findByEmail(email)
            .map(this::convertToDTO);
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return Lista de DTOs de usuarios
     */
    public List<UserDTO> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");

        return userRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id ID del usuario
     * @param updateDTO datos para actualizar
     * @return DTO del usuario actualizado
     */
    public UserDTO updateUser(Long id, UserRegistrationDTO updateDTO) {
        log.info("Actualizando usuario: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar email si cambió
        if (!user.getEmail().equals(updateDTO.getEmail()) &&
            userRepository.existsByEmail(updateDTO.getEmail())) {
            log.warn("Email ya está registrado: {}", updateDTO.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        user.setName(updateDTO.getName());
        user.setEmail(updateDTO.getEmail());
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado: {}", id);

        return convertToDTO(updatedUser);
    }

    /**
     * Habilita un usuario.
     *
     * @param id ID del usuario
     */
    public void enableUser(Long id) {
        log.info("Habilitando usuario: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Deshabilita un usuario.
     *
     * @param id ID del usuario
     */
    public void disableUser(Long id) {
        log.info("Deshabilitando usuario: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Promueve un usuario a ADMIN.
     *
     * @param id ID del usuario
     */
    public void promoteToAdmin(Long id) {
        log.info("Promoviendo usuario a ADMIN: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    /**
     * Elimina un usuario (hard delete).
     *
     * @param id ID del usuario
     */
    public void deleteUser(Long id) {
        log.info("Eliminando usuario: {}", id);

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        userRepository.deleteById(id);
        log.info("Usuario eliminado: {}", id);
    }

    /**
     * Convierte una entidad User a DTO.
     *
     * @param user entidad usuario
     * @return DTO usuario (sin contraseña)
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}

