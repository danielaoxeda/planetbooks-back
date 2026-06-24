package com.rodrigomv.planetbooksback;

import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.model.enums.Role;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Seeder que crea datos iniciales si no existen.
 * Se ejecuta automáticamente al iniciar la aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@planetbooks.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_NAME = "Administrador";

    @Override
    @Transactional
    public void run(String... args) {
        loadAdminUser();
        loadProducts();
    }

    private void loadAdminUser() {
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

    private void loadProducts() {
        if (productRepository.count() > 0) {
            log.info("Ya existen productos, omitiendo seeder");
            return;
        }

        List<Product> books = Arrays.asList(
                Product.builder()
                        .title("Cambridge English Authentic Examination Papers for Starters, Movers, Flyers")
                        .description("Cambridge English Authentic Examination Papers for Starters, Movers, Flyers: PDF, Audio, Answer Booklet")
                        .tag("YLE")
                        .categories(Arrays.asList("YLE", "STARTERS", "MOVERS", "FLYERS"))
                        .level("Beginner")
                        .image("/books/Cambridge-AEP.png")
                        .build(),
                Product.builder()
                        .title("Cambridge Mini Trainer: Starters (Pre-A1), Movers (A1), Flyers (A2)")
                        .description("Cambridge Mini Trainer: Starters (Pre-A1), Movers (A1), Flyers (A2) PDF, Audio, Answer keys")
                        .tag("YLE")
                        .categories(Arrays.asList("YLE", "STARTERS", "MOVERS", "FLYERS"))
                        .level("Beginner")
                        .image("/books/Cambridge-MiniTrainer.png")
                        .build(),
                Product.builder()
                        .title("A2 KEY for Schools Trainer 2")
                        .description("Six practice tests + Teacher's Notes: PDF + MP3 + SB + TB + AK")
                        .tag("A2 Key")
                        .categories(Arrays.asList("KET-A2"))
                        .level("Beginner")
                        .image("/books/ket-cambridge-2024.jpeg")
                        .build(),
                Product.builder()
                        .title("A2 Key for Schools 3 (2025)")
                        .description("PDF, audio answer key A2 Key for Schools 3 (2025)")
                        .tag("A2 Key")
                        .categories(Arrays.asList("KET-A2"))
                        .level("Beginner")
                        .image("/books/A2-KeyForSchools-3.png")
                        .build(),
                Product.builder()
                        .title("B1 Preliminary for Schools 3")
                        .description("B1 Preliminary for Schools 3 PDF, Answer key, Audio Script, resources bank")
                        .tag("B1 Preliminary")
                        .categories(Arrays.asList("PET-B1"))
                        .level("Intermediate")
                        .image("/books/B1-PreliminarySchools-3.jpg")
                        .build(),
                Product.builder()
                        .title("B2 First Trainer 3 (2025)")
                        .description("B2 First Trainer 3 (2025): PDF, Audio, Test, Answer key, teacher note.")
                        .tag("B2 First")
                        .categories(Arrays.asList("FCE-B2"))
                        .level("Intermediate")
                        .image("/books/B2-FirstTrainer-3.png")
                        .build(),
                Product.builder()
                        .title("C1 Advanced 5 Student's Book with Answers with Digital Pack")
                        .description("C1 Advanced 5 Student's Book with Answers with Digital Pack PDF + Audio")
                        .tag("C1 Advanced")
                        .categories(Arrays.asList("CA1-C1"))
                        .level("Advanced")
                        .image("/books/C1-Advanced-5.png")
                        .build(),
                Product.builder()
                        .title("The Official Guide to the TOEFL iBT Test 7th edition")
                        .description("The Official Guide to the TOEFL iBT Test 7th edition PDF + Audio + Practice Test")
                        .tag("TOEFL")
                        .categories(Arrays.asList("TOEFL"))
                        .level("Advanced")
                        .image("/books/TOEFLiBT-OfficialGuide-7th.png")
                        .build(),
                Product.builder()
                        .title("Cambridge IELTS 20 Practice Tests Academic")
                        .description("Cambridge IELTS 20 Practice Tests Academic: PDF, audio, answer key, transcript")
                        .tag("IELTS")
                        .categories(Arrays.asList("IELTS"))
                        .level("Advanced")
                        .image("/books/IELTS-Academic-20.png")
                        .build()
        );

        productRepository.saveAll(books);
        log.info("{} productos insertados", books.size());
    }
}