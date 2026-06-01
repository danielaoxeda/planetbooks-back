# PlanetBooks Backend - Estructura de Implementación (Diagrama)

## 📊 Árbol de Archivos Generados

```
planetbooks-back/
│
├── 📁 src/main/java/com/rodrigomv/planetbooksback/
│   │
│   ├── 📁 model/
│   │   │
│   │   ├── 📁 entity/ (9 archivos) ✅
│   │   │   ├── Role.java                    [Enum]
│   │   │   ├── OrderStatus.java             [Enum]
│   │   │   ├── User.java                    [@Entity]
│   │   │   ├── Product.java                 [@Entity]
│   │   │   ├── ProductItem.java             [@Entity]
│   │   │   ├── Cart.java                    [@Entity]
│   │   │   ├── CartItem.java                [@Entity]
│   │   │   ├── Order.java                   [@Entity]
│   │   │   └── OrderItem.java               [@Entity]
│   │   │
│   │   └── 📁 dto/ (11 archivos) ✅
│   │       ├── UserDTO.java
│   │       ├── UserRegistrationDTO.java
│   │       ├── LoginRequestDTO.java
│   │       ├── ProductDTO.java
│   │       ├── ProductItemDTO.java
│   │       ├── CartDTO.java
│   │       ├── CartItemDTO.java
│   │       ├── OrderDTO.java
│   │       ├── OrderItemDTO.java
│   │       ├── AddToCartRequestDTO.java
│   │       └── UpdateCartQuantityRequestDTO.java
│   │
│   ├── 📁 repository/ (7 archivos) ✅
│   │   ├── UserRepository.java              [@Repository]
│   │   ├── ProductRepository.java           [@Repository]
│   │   ├── ProductItemRepository.java       [@Repository]
│   │   ├── CartRepository.java              [@Repository]
│   │   ├── CartItemRepository.java          [@Repository]
│   │   ├── OrderRepository.java             [@Repository]
│   │   └── OrderItemRepository.java         [@Repository]
│   │
│   ├── 📁 service/ (2 archivos) ✅
│   │   ├── UserService.java                 [@Service]
│   │   └── ProductService.java              [@Service]
│   │
│   ├── 📁 controller/ (Próximo) ⏳
│   │   ├── AuthController.java
│   │   ├── ProductController.java
│   │   ├── CartController.java
│   │   ├── OrderController.java
│   │   └── UserController.java
│   │
│   └── 📁 config/ (Próximo) ⏳
│       ├── SecurityConfig.java
│       ├── JwtTokenProvider.java
│       └── CorsConfig.java
│
├── 📁 src/main/resources/
│   ├── application.properties                [Configuración]
│   └── 📁 static/
│       └── 📁 templates/
│
├── 📁 target/                                [Build artifacts]
│
├── 📄 pom.xml                                ✅ Actualizado
├── 📄 DATABASE_DDL.sql                       ✅ Schema SQL
├── 📄 APPLICATION_PROPERTIES_TEMPLATE.properties ✅ Config template
├── 📄 ENTITIES_IMPLEMENTATION.md             ✅ Documentación técnica
├── 📄 QUICK_START.md                         ✅ Guía rápida
├── 📄 RESUMEN_EJECUTIVO.md                   ✅ Resumen
├── 📄 ARCHITECTURE_DIAGRAM.md                ✅ Este archivo
├── README.md
└── HELP.md
```

## 🗂️ Estructura de Carpetas

```
C:\Users\rodri\IdeaProjects\planetbooks-back\
└── src\main\java\com\rodrigomv\planetbooksback\
    ├── model\
    │   ├── entity\          (9 entidades)
    │   └── dto\             (11 DTOs)
    ├── repository\          (7 repositorios)
    ├── service\             (2 servicios)
    ├── controller\          (pendiente)
    └── config\              (pendiente)
```

## 🔄 Diagrama de Relaciones de Entidades

```
┌─────────────────────────────────────────────────────────┐
│                         USUARIOS                         │
│                         (User)                           │
│  id│name│email│password│role(Role)│enabled│timestamps  │
└──────────────┬──────────────────────┬──────────────────┘
               │                      │
         1:N   │                      │  1:N
               ▼                      ▼
        ┌────────────┐         ┌────────────┐
        │   CART     │         │   ORDER    │
        │(Cart)      │         │(Order)     │
        └──────┬─────┘         └──────┬─────┘
               │                      │
         1:N   │                      │  1:N
               ▼                      ▼
        ┌────────────────────────────────────────┐
        │         ITEMS                         │
        │  CartItem         │         OrderItem  │
        │  (snapshots)      │         (snapshots)│
        └────────────────────────────────────────┘
               △                      △
               │                      │
          N:1  │                      │
               └──────────┬───────────┘
                          │ (opcional)
                ┌─────────┴──────────┐
                │                    │
          N:1   │              1:N   │
                ▼                    ▼
            ┌──────────┐        ┌──────────────┐
            │PRODUCT   │◄───────┤ PRODUCTITEM  │
            │(Product) │   N:1  │(ProductItem) │
            └──────────┘        └──────────────┘
            id│title│desc│
            categories(List)
            gallery(List)
```

## 📈 Capas de la Aplicación

```
┌─────────────────────────────────────────────────┐
│          REST API LAYER (Controllers)           │ ⏳
│  @RestController @RequestMapping("/api/*")     │
└──────────────────────┬──────────────────────────┘
                       │ HTTP Requests/Responses
┌──────────────────────▼──────────────────────────┐
│         BUSINESS LOGIC LAYER (Services)        │ ✅
│  @Service @Transactional                       │
│  - UserService                                  │
│  - ProductService                              │
│  - CartService (próximo)                       │
│  - OrderService (próximo)                      │
└──────────────────────┬──────────────────────────┘
                       │ DTO Conversion
┌──────────────────────▼──────────────────────────┐
│    DATA ACCESS LAYER (Repositories)            │ ✅
│  @Repository extends JpaRepository             │
│  - UserRepository                              │
│  - ProductRepository                           │
│  - CartRepository                              │
│  - OrderRepository                             │
└──────────────────────┬──────────────────────────┘
                       │ SQL Queries
┌──────────────────────▼──────────────────────────┐
│         PERSISTENCE LAYER (Entities)           │ ✅
│  @Entity @Table                                │
│  - User, Product, ProductItem                  │
│  - Cart, CartItem                              │
│  - Order, OrderItem                            │
└──────────────────────┬──────────────────────────┘
                       │ CRUD Operations
┌──────────────────────▼──────────────────────────┐
│       DATABASE LAYER (MySQL/PostgreSQL)        │ ✅
│  - users                                        │
│  - products, product_items                     │
│  - product_categories, product_gallery        │
│  - carts, cart_items                           │
│  - orders, order_items                         │
└─────────────────────────────────────────────────┘
```

## 🔀 Flujos de Datos

### Flujo 1: Registro de Usuario
```
POST /api/auth/register (UserRegistrationDTO)
    │
    ▼
UserController.register()
    │
    ▼
UserService.registerUser()
    │
    ├─ Validar email único
    ├─ Hash contraseña (BCrypt)
    └─ Crear User entity
        │
        ▼
    UserRepository.save(user)
        │
        ▼
    Database: INSERT INTO users
        │
        ▼
    UserDTO (sin password)
        │
        ▼
JSON Response: 200 OK
```

### Flujo 2: Búsqueda de Productos
```
GET /api/products?tag=YLE&level=Beginner&page=0&size=10
    │
    ▼
ProductController.getProducts()
    │
    ▼
ProductService.getProductsByTag() o getProductsByLevel()
    │
    ▼
ProductRepository.findByTag() / findByLevel() (+ paginación)
    │
    ▼
Database: SELECT * FROM products...
    │
    ▼
Mapeo a ProductDTO (con ProductItemDTO)
    │
    ▼
JSON Response: Page<ProductDTO>
```

### Flujo 3: Crear Orden desde Carrito
```
POST /api/orders
    │
    ▼
OrderController.createOrder()
    │
    ▼
OrderService.createOrderFromCart()
    │
    ├─ CartService.getCart(userId)
    │   │
    │   ▼
    │   CartRepository.findByUserId()
    │
    ├─ Calcular total del carrito
    │
    ├─ Crear Order entity
    │   │
    │   ▼
    │   OrderRepository.save()
    │
    ├─ Copiar CartItems → OrderItems (snapshot)
    │   │
    │   ▼
    │   OrderItemRepository.saveAll()
    │
    └─ Vaciar carrito
        │
        ▼
        CartItemRepository.deleteByCartId()
        │
        ▼
OrderDTO Response: 201 Created
```

## 📋 Stack Tecnológico por Capa

| Capa | Tecnologías |
|-----|-------------|
| API | Spring Web MVC, REST |
| Seguridad | Spring Security, JWT, BCrypt |
| Servicios | Spring Services, Transactional |
| Datos | Spring Data JPA, Hibernate |
| Base de Datos | MySQL 8.0+, PostgreSQL 12+ |
| Herramientas | Lombok, Maven, Java 17 |

## 🎯 Responsabilidades por Capa

### Entity Layer (✅ Completado)
- Mapeo relacional ORM
- Validaciones con anotaciones JPA
- Relaciones con cascade/orphan removal
- Índices para performance

### DTO Layer (✅ Completado)
- Segregación de datos expuestos
- Request/Response mapping
- Omisión de campos sensibles
- Serialización controlada

### Repository Layer (✅ Completado)
- Acceso a datos
- Consultas personalizadas
- Paginación y sorting
- Transacciones ACID

### Service Layer (✅ Parcial - 2 servicios)
- Lógica de negocio
- Validaciones de reglas
- Conversión Entity↔DTO
- Orquestación de operaciones

### Controller Layer (⏳ Próximo)
- Endpoints REST
- Decoradores HTTP
- Error handling
- Validación de entrada

### Config Layer (⏳ Próximo)
- Configuración de seguridad
- Beans personalizados
- CORS, JWT, etc.

## 📊 Estado de Completitud

```
Entidades JPA          ████████████ 100% ✅
DTOs                  ████████████ 100% ✅
Repositorios          ████████████ 100% ✅
Servicios             ████████░░░░  25% ⏳ (2 de 8)
Controladores         ░░░░░░░░░░░░   0% ⏳
Configuración         ░░░░░░░░░░░░   0% ⏳
Seguridad             ░░░░░░░░░░░░   0% ⏳
Tests                 ░░░░░░░░░░░░   0% ⏳
Documentación         ████████░░░░  75% ✅
```

## 🔑 Decisiones de Arquitectura

### ✅ Microservicios-Ready
La estructura permite fácil migración a microservicios separando servicios.

### ✅ Escalabilidad
DTOs permiten cambiar entidades sin afectar API.

### ✅ ACID Compliance
Transacciones en servicios garantizan consistencia.

### ✅ Security First
Campos sensibles omitidos en DTOs, roles definidos, contraseñas hasheadas.

### ✅ Performance Optimized
Índices en búsquedas frecuentes, lazy loading, paginación.

### ✅ DDD-Inspired
Entidades con lógica de dominio, servicios de negocio.

### ✅ Clean Code
Separación de responsabilidades, nombres claros, documentación.

## 🚀 Próximas Fases

```
Fase 2-3: Controladores y Seguridad
├── AuthController (login, register)
├── ProductController (búsqueda)
├── CartController (CRUD)
├── OrderController (crear, listar)
└── Spring Security + JWT

Fase 4-5: Servicios Avanzados
├── CartService completo
├── OrderService completo
├── PaymentService (interfaz)
├── NotificationService
└── Tests unitarios

Fase 6: Production-Ready
├── Swagger/OpenAPI
├── Logging y Monitoring
├── Caching (Redis)
├── Rate Limiting
└── CI/CD Pipeline
```

---

**Arquitectura PlanetBooks Backend** © 2026 - Rodrigo MV

