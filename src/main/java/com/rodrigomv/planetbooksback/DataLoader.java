package com.rodrigomv.planetbooksback;

import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.model.enums.Role;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeder que crea un usuario administrador inicial si no existe.
 * Se ejecuta automáticamente al iniciar la aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@planetbooks.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_NAME = "Administrador";

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("Usuario admin ya existe, omitiendo creación");
            return;
        }

        User admin = User.builder()
                .name(ADMIN_NAME)
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.info("Usuario admin creado: {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
    }
}