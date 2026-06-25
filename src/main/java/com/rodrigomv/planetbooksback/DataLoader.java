package com.rodrigomv.planetbooksback;

import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
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

import java.math.BigDecimal;
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
        // Si ya hay productos, verificar si tienen items. Si no, se insertan los items faltantes.
        if (productRepository.count() > 0) {
            log.info("Ya existen productos, omitiendo seeder");
            return;
        }

        Product p1 = Product.builder()
                .title("Cambridge English Authentic Examination Papers for Starters, Movers, Flyers")
                .description("Cambridge English Authentic Examination Papers for Starters, Movers, Flyers: PDF, Audio, Answer Booklet")
                .tag("YLE")
                .categories(Arrays.asList("YLE", "STARTERS", "MOVERS", "FLYERS"))
                .level("Beginner")
                .image("/books/Cambridge-AEP.png")
                .build();

        ProductItem p1i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("25.00")).stock(100).isDefault(true).pages(null).format(null).product(p1).build();
        ProductItem p1i2 = ProductItem.builder().key("pdf-audio").title("PDF + Audio").price(new BigDecimal("40.00")).stock(100).isDefault(false).pages(null).format(null).product(p1).build();
        p1.setItems(Arrays.asList(p1i1, p1i2));

        Product p2 = Product.builder()
                .title("Cambridge Mini Trainer: Starters (Pre-A1), Movers (A1), Flyers (A2)")
                .description("Cambridge Mini Trainer: Starters (Pre-A1), Movers (A1), Flyers (A2) PDF, Audio, Answer keys")
                .tag("YLE")
                .categories(Arrays.asList("YLE", "STARTERS", "MOVERS", "FLYERS"))
                .level("Beginner")
                .image("/books/Cambridge-MiniTrainer.png")
                .build();

        ProductItem p2i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("22.00")).stock(100).isDefault(true).pages(null).format(null).product(p2).build();
        ProductItem p2i2 = ProductItem.builder().key("pdf-audio").title("PDF + Audio").price(new BigDecimal("35.00")).stock(100).isDefault(false).pages(null).format(null).product(p2).build();
        p2.setItems(Arrays.asList(p2i1, p2i2));

        Product p3 = Product.builder()
                .title("A2 KEY for Schools Trainer 2")
                .description("Six practice tests + Teacher's Notes: PDF + MP3 + SB + TB + AK")
                .tag("A2 Key")
                .categories(Arrays.asList("KET-A2"))
                .level("Beginner")
                .image("/books/ket-cambridge-2024.jpeg")
                .build();

        ProductItem p3i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("28.00")).stock(100).isDefault(true).pages(null).format(null).product(p3).build();
        ProductItem p3i2 = ProductItem.builder().key("full").title("PDF + Audio + Extras").price(new BigDecimal("45.00")).stock(100).isDefault(false).pages(null).format(null).product(p3).build();
        p3.setItems(Arrays.asList(p3i1, p3i2));

        Product p4 = Product.builder()
                .title("A2 Key for Schools 3 (2025)")
                .description("PDF, audio answer key A2 Key for Schools 3 (2025)")
                .tag("A2 Key")
                .categories(Arrays.asList("KET-A2"))
                .level("Beginner")
                .image("/books/A2-KeyForSchools-3.png")
                .build();

        ProductItem p4i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("30.00")).stock(100).isDefault(true).pages(null).format(null).product(p4).build();
        ProductItem p4i2 = ProductItem.builder().key("pdf-audio").title("PDF + Audio").price(new BigDecimal("48.00")).stock(100).isDefault(false).pages(null).format(null).product(p4).build();
        p4.setItems(Arrays.asList(p4i1, p4i2));

        Product p5 = Product.builder()
                .title("B1 Preliminary for Schools 3")
                .description("B1 Preliminary for Schools 3 PDF, Answer key, Audio Script, resources bank")
                .tag("B1 Preliminary")
                .categories(Arrays.asList("PET-B1"))
                .level("Intermediate")
                .image("/books/B1-PreliminarySchools-3.jpg")
                .build();

        ProductItem p5i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("32.00")).stock(100).isDefault(true).pages(null).format(null).product(p5).build();
        ProductItem p5i2 = ProductItem.builder().key("full").title("PDF + Audio + Resources").price(new BigDecimal("50.00")).stock(100).isDefault(false).pages(null).format(null).product(p5).build();
        p5.setItems(Arrays.asList(p5i1, p5i2));

        Product p6 = Product.builder()
                .title("B2 First Trainer 3 (2025)")
                .description("B2 First Trainer 3 (2025): PDF, Audio, Test, Answer key, teacher note.")
                .tag("B2 First")
                .categories(Arrays.asList("FCE-B2"))
                .level("Intermediate")
                .image("/books/B2-FirstTrainer-3.png")
                .build();

        ProductItem p6i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("35.00")).stock(100).isDefault(true).pages(null).format(null).product(p6).build();
        ProductItem p6i2 = ProductItem.builder().key("full").title("PDF + Audio + Teacher Notes").price(new BigDecimal("55.00")).stock(100).isDefault(false).pages(null).format(null).product(p6).build();
        p6.setItems(Arrays.asList(p6i1, p6i2));

        Product p7 = Product.builder()
                .title("C1 Advanced 5 Student's Book with Answers with Digital Pack")
                .description("C1 Advanced 5 Student's Book with Answers with Digital Pack PDF + Audio")
                .tag("C1 Advanced")
                .categories(Arrays.asList("CA1-C1"))
                .level("Advanced")
                .image("/books/C1-Advanced-5.png")
                .build();

        ProductItem p7i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("40.00")).stock(100).isDefault(true).pages(null).format(null).product(p7).build();
        ProductItem p7i2 = ProductItem.builder().key("digital-pack").title("PDF + Digital Pack").price(new BigDecimal("65.00")).stock(100).isDefault(false).pages(null).format(null).product(p7).build();
        p7.setItems(Arrays.asList(p7i1, p7i2));

        Product p8 = Product.builder()
                .title("The Official Guide to the TOEFL iBT Test 7th edition")
                .description("The Official Guide to the TOEFL iBT Test 7th edition PDF + Audio + Practice Test")
                .tag("TOEFL")
                .categories(Arrays.asList("TOEFL"))
                .level("Advanced")
                .image("/books/TOEFLiBT-OfficialGuide-7th.png")
                .build();

        ProductItem p8i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("38.00")).stock(100).isDefault(true).pages(null).format(null).product(p8).build();
        ProductItem p8i2 = ProductItem.builder().key("pdf-audio").title("PDF + Audio").price(new BigDecimal("55.00")).stock(100).isDefault(false).pages(null).format(null).product(p8).build();
        p8.setItems(Arrays.asList(p8i1, p8i2));

        Product p9 = Product.builder()
                .title("Cambridge IELTS 20 Practice Tests Academic")
                .description("Cambridge IELTS 20 Practice Tests Academic: PDF, audio, answer key, transcript")
                .tag("IELTS")
                .categories(Arrays.asList("IELTS"))
                .level("Advanced")
                .image("/books/IELTS-Academic-20.png")
                .build();

        ProductItem p9i1 = ProductItem.builder().key("pdf").title("PDF").price(new BigDecimal("35.00")).stock(100).isDefault(true).pages(null).format(null).product(p9).build();
        ProductItem p9i2 = ProductItem.builder().key("pdf-audio").title("PDF + Audio").price(new BigDecimal("55.00")).stock(100).isDefault(false).pages(null).format(null).product(p9).build();
        p9.setItems(Arrays.asList(p9i1, p9i2));

        List<Product> books = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9);

        productRepository.saveAll(books);
        log.info("{} productos insertados", books.size());
    }
}