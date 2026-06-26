package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.ChangePasswordDTO;
import com.rodrigomv.planetbooksback.model.dto.UpdateUserDTO;
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
     * @param updateDTO datos para actualizar (solo name y email)
     * @return DTO del usuario actualizado
     */
    public UserDTO updateUser(Long id, UpdateUserDTO updateDTO) {
        log.info("Actualizando usuario: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(updateDTO.getEmail())) {
            log.warn("Email ya está registrado: {}", updateDTO.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
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
     * Degrada un usuario a USER (le quita el rol ADMIN).
     *
     * @param id ID del usuario
     */
    public void demoteToUser(Long id) {
        log.info("Degradando usuario a USER: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setRole(Role.USER);
        userRepository.save(user);
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param id ID del usuario
     * @param changePasswordDTO DTO con contraseña actual y nueva
     */
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) {
        log.info("Cambiando contraseña para usuario: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            log.warn("Contraseña actual incorrecta para usuario: {}", id);
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Validar que nueva contraseña y confirmación coincidan
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            log.warn("Las contraseñas no coinciden para usuario: {}", id);
            throw new IllegalArgumentException("La nueva contraseña y la confirmación no coinciden");
        }

        // Validar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            log.warn("La nueva contraseña es igual a la actual para usuario: {}", id);
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
        log.info("Contraseña cambiada exitosamente para usuario: {}", id);
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

