-- ============================================
-- Schema para planetbooks-back
-- Ejecutar en MySQL
-- ============================================

-- product_items (faltaba según el error)
CREATE TABLE IF NOT EXISTS product_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    item_key VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    image VARCHAR(255),
    description TEXT,
    pages VARCHAR(255),
    format VARCHAR(255),
    is_default BIT NOT NULL DEFAULT 0,
    stock INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_product_item_key UNIQUE (product_id, item_key),
    CONSTRAINT fk_product_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- product_categories (ElementCollection de Product.categories)
CREATE TABLE IF NOT EXISTS product_categories (
    product_id BIGINT NOT NULL,
    category VARCHAR(255),
    CONSTRAINT fk_product_categories_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- product_gallery (ElementCollection de Product.gallery)
CREATE TABLE IF NOT EXISTS product_gallery (
    product_id BIGINT NOT NULL,
    gallery_url VARCHAR(255),
    CONSTRAINT fk_product_gallery_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- carts
CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(255),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- cart_items
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_item_id BIGINT,
    product_id BIGINT NOT NULL,
    product_title VARCHAR(255) NOT NULL,
    product_image VARCHAR(255),
    item_key VARCHAR(255) NOT NULL,
    item_title VARCHAR(255) NOT NULL,
    item_description TEXT,
    quantity INT NOT NULL,
    item_price DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product_item FOREIGN KEY (product_item_id) REFERENCES product_items(id) ON DELETE SET NULL
);

-- orders
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- order_items
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_title VARCHAR(255) NOT NULL,
    product_image VARCHAR(255),
    item_key VARCHAR(255) NOT NULL,
    item_title VARCHAR(255) NOT NULL,
    item_description TEXT,
    item_price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- ============================================
-- Índices adicionales
-- ============================================
CREATE INDEX IF NOT EXISTS idx_order_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_user_id ON carts(user_id);
CREATE INDEX IF NOT EXISTS idx_product_tag ON products(tag);
CREATE INDEX IF NOT EXISTS idx_product_level ON products(level);
