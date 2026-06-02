-- PlanetBooks DDL Schema
-- Compatible con MySQL 8.0+ y PostgreSQL 12+
-- Nota: Para PostgreSQL cambiar BIGINT AUTO_INCREMENT por BIGSERIAL y NUMERIC por DECIMAL

-- ============================================
-- USUARIOS
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
);

-- ============================================
-- PRODUCTOS
-- ============================================
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT,
    tag VARCHAR(100),
    level VARCHAR(100),
    image VARCHAR(500),
    pages VARCHAR(100),
    format VARCHAR(100),
    publisher VARCHAR(255),
    language VARCHAR(100),
    INDEX idx_product_tag (tag),
    INDEX idx_product_level (level),
    INDEX idx_product_title (title)
);

-- Colecciones de producto (categories)
CREATE TABLE product_categories (
    product_id BIGINT NOT NULL,
    category VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_categories_product_id (product_id)
);

-- Galería de producto
CREATE TABLE product_gallery (
    product_id BIGINT NOT NULL,
    gallery_url VARCHAR(500),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_gallery_product_id (product_id)
);

-- ============================================
-- ITEMS DE PRODUCTO
-- ============================================
CREATE TABLE product_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    item_key VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    image VARCHAR(500),
    description LONGTEXT,
    pages VARCHAR(100),
    format VARCHAR(100),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_item_key (product_id, item_key),
    INDEX idx_product_items_product_id (product_id)
);

-- ============================================
-- CARRITOS
-- ============================================
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_cart_user_id (user_id),
    INDEX idx_cart_session_id (session_id)
);

-- ============================================
-- ITEMS EN CARRITO
-- ============================================
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_item_id BIGINT,
    product_id BIGINT NOT NULL,
    product_title VARCHAR(255),
    product_image VARCHAR(500),
    item_key VARCHAR(100) NOT NULL,
    item_title VARCHAR(255),
    item_description LONGTEXT,
    quantity INT NOT NULL DEFAULT 1,
    item_price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_item_id) REFERENCES product_items(id) ON DELETE SET NULL,
    INDEX idx_cart_items_cart_id (cart_id),
    INDEX idx_cart_items_product_item_id (product_item_id)
);

-- ============================================
-- ÓRDENES
-- ============================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_order_user_id (user_id),
    INDEX idx_order_status (status)
);

-- ============================================
-- ITEMS EN ORDEN
-- ============================================
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_title VARCHAR(255),
    product_image VARCHAR(500),
    item_key VARCHAR(100) NOT NULL,
    item_title VARCHAR(255),
    item_description LONGTEXT,
    item_price DECIMAL(12, 2),
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_items_order_id (order_id)
);

-- ============================================
-- VISTAS ÚTILES (OPCIONAL)
-- ============================================

-- Vista: Resumen de carrito por usuario
CREATE VIEW v_cart_summary AS
SELECT
    c.id AS cart_id,
    c.user_id,
    COUNT(ci.id) AS item_count,
    SUM(ci.quantity) AS total_items,
    SUM(ci.quantity * ci.item_price) AS total_amount,
    c.created_at
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
GROUP BY c.id, c.user_id, c.created_at;

-- Vista: Resumen de órdenes por usuario
CREATE VIEW v_order_summary AS
SELECT
    o.id AS order_id,
    o.user_id,
    COUNT(oi.id) AS item_count,
    SUM(oi.quantity) AS total_items,
    o.total_amount,
    o.status,
    o.created_at
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.user_id, o.total_amount, o.status, o.created_at;

-- ============================================
-- DATOS INICIALES DE EJEMPLO (OPCIONAL)
-- ============================================

-- Insertar usuario de ejemplo
INSERT INTO users (name, email, password, role, enabled)
VALUES
    ('Admin User', 'admin@planetbooks.com', '$2a$10$abcdefg', 'ADMIN', TRUE),
    ('Test User', 'user@planetbooks.com', '$2a$10$hijklmn', 'USER', TRUE);

-- Insertar producto de ejemplo
INSERT INTO products (title, description, tag, level, image, pages, publisher, language)
VALUES
    (
        'Cambridge English Authentic Examination Papers',
        'Authentic examination papers for Cambridge English exams',
        'YLE',
        'Beginner',
        '/books/Cambridge-AEP.png',
        '200',
        'Cambridge University Press',
        'English'
    );

-- Insertar categorías de producto
INSERT INTO product_categories (product_id, category)
VALUES
    (1, 'YLE'),
    (1, 'STARTERS');

-- Insertar items de producto
INSERT INTO product_items (product_id, item_key, title, price, is_default)
VALUES
    (1, 'starters', 'Starters 1,2,3', 12.00, TRUE),
    (1, 'movers', 'Movers 1,2,3', 14.00, FALSE),
    (1, 'flyers', 'Flyers 1,2,3', 16.00, FALSE);

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS (OPCIONAL)
-- ============================================

-- Crear orden desde carrito
DELIMITER //
CREATE PROCEDURE sp_create_order_from_cart(
    IN p_user_id BIGINT,
    OUT p_order_id BIGINT,
    OUT p_success BOOLEAN
)
BEGIN
    DECLARE v_cart_id BIGINT;
    DECLARE v_total DECIMAL(12,2);

    START TRANSACTION;

    -- Obtener carrito del usuario
    SELECT id INTO v_cart_id FROM carts WHERE user_id = p_user_id LIMIT 1;

    IF v_cart_id IS NULL THEN
        SET p_success = FALSE;
        ROLLBACK;
    ELSE
        -- Calcular total del carrito
        SELECT SUM(quantity * item_price) INTO v_total
        FROM cart_items
        WHERE cart_id = v_cart_id;

        -- Crear orden
        INSERT INTO orders (user_id, total_amount, status)
        VALUES (p_user_id, COALESCE(v_total, 0), 'PENDING');

        SET p_order_id = LAST_INSERT_ID();

        -- Copiar items del carrito a la orden
        INSERT INTO order_items (order_id, product_id, product_title, product_image, item_key, item_title, item_description, item_price, quantity)
        SELECT p_order_id, product_id, product_title, product_image, item_key, item_title, item_description, item_price, quantity
        FROM cart_items
        WHERE cart_id = v_cart_id;

        -- Limpiar carrito
        DELETE FROM cart_items WHERE cart_id = v_cart_id;

        SET p_success = TRUE;
        COMMIT;
    END IF;
END //
DELIMITER ;

-- ============================================
-- ÍNDICES ADICIONALES PARA PERFORMANCE
-- ============================================

-- Para búsquedas complejas
CREATE INDEX idx_products_tag_level ON products(tag, level);
CREATE INDEX idx_product_items_is_default ON product_items(is_default);
CREATE INDEX idx_carts_created_at ON carts(created_at);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_user_id_status ON orders(user_id, status);

-- ============================================
-- CONSTRAINTS ADICIONALES (VALIDACIÓN DB)
-- ============================================

-- La validación principal se hace en las anotaciones JPA
-- Pero se pueden agregar constraints a nivel DB:

ALTER TABLE product_items ADD CONSTRAINT chk_positive_price CHECK (price >= 0);
ALTER TABLE cart_items ADD CONSTRAINT chk_positive_quantity CHECK (quantity >= 1);
ALTER TABLE cart_items ADD CONSTRAINT chk_positive_item_price CHECK (item_price >= 0);
ALTER TABLE order_items ADD CONSTRAINT chk_positive_order_quantity CHECK (quantity >= 1);
ALTER TABLE order_items ADD CONSTRAINT chk_positive_order_price CHECK (item_price >= 0);
ALTER TABLE orders ADD CONSTRAINT chk_valid_order_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED', 'SHIPPED', 'COMPLETED'));
ALTER TABLE users ADD CONSTRAINT chk_valid_role CHECK (role IN ('ADMIN', 'USER'));

