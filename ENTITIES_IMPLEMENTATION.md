# PlanetBooks - Entidades JPA - Documentación de Implementación

## ✅ Resumen de lo implementado

Se han creado todas las entidades JPA según la especificación del documento de diseño. El proyecto compila sin errores.

### Entidades Creadas (Entities)

#### Enums
- **Role**: ADMIN, USER
- **OrderStatus**: PENDING, PAID, CANCELLED, SHIPPED, COMPLETED

#### Entidades Principales
1. **User** - Tabla `users`
   - Almacena usuarios con contraseña hashed
   - Email es único
   - Campos: id, name, email, password, role, enabled, createdAt, updatedAt

2. **Product** - Tabla `products`
   - Catálogo de productos (libros)
   - Soporta categorías y galería como colecciones
   - Relación 1:N con ProductItem
   - Índices sobre: tag, level

3. **ProductItem** - Tabla `product_items`
   - Variaciones de producto con diferentes precios
   - Constraint UNIQUE(product_id, item_key)
   - Precio con precision=12, scale=2
   - Relación N:1 con Product

4. **Cart** - Tabla `carts`
   - Carrito de compras (usuario o sesión)
   - Nullable user_id para carritos anónimos
   - Relación 1:N con CartItem
   - Índice sobre: user_id

5. **CartItem** - Tabla `cart_items`
   - Items en el carrito con snapshot de datos
   - Almacena: productId, productTitle, itemKey, itemPrice, etc.
   - Mantiene histórico incluso si el producto cambia
   - Relación N:1 con Cart

6. **Order** - Tabla `orders`
   - Orden de compra de un usuario
   - Estados: PENDING, PAID, CANCELLED, SHIPPED, COMPLETED
   - Totales pre-calculados
   - Relación 1:N con OrderItem
   - Índice sobre: user_id

7. **OrderItem** - Tabla `order_items`
   - Items de la orden (snapshot completo)
   - Almacena: productId, productTitle, itemKey, itemPrice, quantity
   - Mantiene registro histórico

### DTOs Creados (Data Transfer Objects)

Para evitar exponer campos sensibles y serializar solo lo necesario:

- **UserDTO** - Respuesta de usuario (sin password)
- **ProductDTO** - Respuesta de producto con items
- **ProductItemDTO** - Respuesta de item de producto
- **CartDTO** - Respuesta de carrito con items
- **CartItemDTO** - Respuesta de item en carrito
- **OrderDTO** - Respuesta de orden con items
- **OrderItemDTO** - Respuesta de item en orden
- **UserRegistrationDTO** - Request para registro
- **LoginRequestDTO** - Request para login
- **AddToCartRequestDTO** - Request para añadir al carrito
- **UpdateCartQuantityRequestDTO** - Request para actualizar cantidad

### Repositorios Creados (JPA Repositories)

- **UserRepository**
  - findByEmail(String email)
  - existsByEmail(String email)

- **ProductRepository**
  - findByTag(String tag)
  - findByLevel(String level)
  - findByTitleContainingIgnoreCase(String title, Pageable pageable)
  - Con soporte para paginación

- **ProductItemRepository**
  - findByProductIdAndKey(Long productId, String key)

- **CartRepository**
  - findByUserId(Long userId)
  - findBySessionId(String sessionId)

- **CartItemRepository**
  - deleteByCartId(Long cartId)

- **OrderRepository**
  - findByUserId(Long userId, Pageable pageable)
  - findByUserId(Long userId)
  - findByStatus(OrderStatus status)
  - findByUserIdAndStatus(Long userId, OrderStatus status)

- **OrderItemRepository**
  - deleteByOrderId(Long orderId)

## 🔧 Decisiones de Diseño Implementadas

### 1. **Snapshot Pattern para CartItem y OrderItem**
- Se almacenan datos históricos del producto en CartItem/OrderItem
- Evita inconsistencias si el ProductItem cambia después de añadirse al carrito
- Mantiene registro completo de precios y descripciones en el momento de compra

### 2. **Enums para Role y OrderStatus**
- Validación a nivel de tipo
- Mejor seguridad de tipos que strings

### 3. **ElementCollection para Categories y Gallery**
- Flexibilidad para listas de strings sin tabla adicional
- Simplifica consultas cuando no requieres normalización completa

### 4. **BigDecimal para Precios**
- Precision=12, scale=2 para evitar problemas de redondeo
- Estándar para aplicaciones financieras

### 5. **Cascada y Orphan Removal**
- CartItems se eliminan al borrar Cart
- OrderItems se eliminan al borrar Order
- ProductItems se eliminan al borrar Product

### 6. **Índices para Performance**
- users.email (UNIQUE)
- products.tag, products.level
- carts.user_id, orders.user_id
- product_items.product_id + item_key (UNIQUE)

### 7. **FetchType.LAZY para Relaciones**
- Optimiza consultas evitando cargas innecesarias
- Las colecciones (@OneToMany) están en LAZY

### 8. **Timestamps Automáticos**
- @CreationTimestamp para createdAt (solo lectura)
- @UpdateTimestamp para updatedAt (automático)
- Usa org.hibernate.annotations

## 📋 Validaciones Implementadas

```java
// User
@Email - Email válido
@NotBlank - Campo no vacío

// ProductItem, CartItem, OrderItem
@DecimalMin("0.00") - Precios no negativos
@Min(1) - Cantidad mínima 1

// CartItem, OrderItem
Quantity con @Min(1)
itemPrice con @DecimalMin("0.00")
```

## 🚀 Próximos Pasos Recomendados

### 1. Crear Servicios (Service Layer)
```java
UserService - Gestión de usuarios (registro, login, etc.)
ProductService - Búsqueda y filtrado de productos
CartService - Gestión del carrito
OrderService - Creación y gestión de órdenes
```

### 2. Crear Controladores REST
```
POST /api/auth/register
POST /api/auth/login
GET /api/products
GET /api/products/{id}
POST /api/cart/items
GET /api/cart
PUT /api/cart/items/{id}
DELETE /api/cart/items/{id}
POST /api/orders
GET /api/orders
GET /api/orders/{id}
```

### 3. Configurar Seguridad (Spring Security)
- JWT tokens para autenticación
- Roles para autorización
- BCryptPasswordEncoder para contraseñas

### 4. Mapeos Entity ↔ DTO
- Usar ModelMapper o MapStruct
- O crear convertidores manuales

### 5. Excepciones Personalizadas
```
ResourceNotFoundException
InvalidCredentialsException
DuplicateEmailException
CartNotFoundException
OrderNotFoundException
```

### 6. Configuración de Persistencia
Asegurar en `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.datasource.url=jdbc:mysql://localhost:3306/planetbooks
spring.datasource.username=root
spring.datasource.password=...
```

## 📁 Estructura de Carpetas

```
src/main/java/com/rodrigomv/planetbooksback/
├── model/
│   ├── entity/          ✅ Entidades JPA implementadas
│   │   ├── Role.java
│   │   ├── OrderStatus.java
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── ProductItem.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   └── dto/             ✅ DTOs implementados
│       ├── UserDTO.java
│       ├── ProductDTO.java
│       ├── CartDTO.java
│       ├── OrderDTO.java
│       └── ...
├── repository/          ✅ Repositorios JPA implementados
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   └── ...
├── service/             ⏳ Pendiente
├── controller/          ⏳ Pendiente
└── config/              ⏳ Pendiente
```

## ✅ Estado del Proyecto

- ✅ Entidades JPA compiladas
- ✅ DTOs para APIs
- ✅ Repositorios con consultas base
- ✅ Validaciones en entidades
- ✅ Índices para performance
- ✅ Relaciones correctas (1:N, N:1, @ElementCollection)
- ⏳ Servicios (siguiente fase)
- ⏳ Controladores REST (siguiente fase)
- ⏳ Seguridad Spring Security (siguiente fase)

## 📝 Notas

1. Las entidades usan Lombok para reducir boilerplate (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
2. Todas las precisiones de BigDecimal están configuradas a (12, 2) como se especificó
3. Las relaciones usan LAZY loading para optimizar consultas
4. Se incluyen índices en las columnas más usadas para búsquedas
5. Los DTOs omiten campos sensibles como contraseñas

