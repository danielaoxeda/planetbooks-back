# PlanetBooks Backend - Guía de Inicio Rápido

## 📦 ¿Qué se ha implementado?

### ✅ Entidades JPA Completas (9 entidades)
- **Role** (enum) - ADMIN, USER
- **OrderStatus** (enum) - PENDING, PAID, CANCELLED, SHIPPED, COMPLETED
- **User** - Usuarios del sistema con autenticación
- **Product** - Catálogo de libros
- **ProductItem** - Variaciones de libros con precios
- **Cart** - Carritos de compra
- **CartItem** - Items en el carrito
- **Order** - Órdenes de compra
- **OrderItem** - Items en una orden

### ✅ DTOs (10 DTOs)
Para serialización segura sin exponer campos sensibles.

### ✅ Repositorios JPA (6 repositorios)
Con métodos de búsqueda personalizados.

### ✅ Servicios de Ejemplo (2 servicios)
- **UserService** - Gestión de usuarios
- **ProductService** - Búsqueda y filtrado de productos

### ✅ Base de Datos
- DDL SQL completo con índices y constraints
- Vistas útiles
- Procedimientos almacenados de ejemplo

## 🚀 Primeros Pasos

### 1. Configurar la Base de Datos

#### Opción A: MySQL (recomendado)
```bash
# Crear base de datos
CREATE DATABASE planetbooks CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Crear usuario
CREATE USER 'planetbooks_user'@'localhost' IDENTIFIED BY 'tu_contraseña_segura';

# Otorgar permisos
GRANT ALL PRIVILEGES ON planetbooks.* TO 'planetbooks_user'@'localhost';
FLUSH PRIVILEGES;

# Cargar el DDL
mysql -u planetbooks_user -p planetbooks < DATABASE_DDL.sql
```

#### Opción B: PostgreSQL
```bash
createdb planetbooks
psql planetbooks < DATABASE_DDL.sql
```

### 2. Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/planetbooks
spring.datasource.username=planetbooks_user
spring.datasource.password=tu_contraseña_segura
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.rodrigomv.planetbooksback=DEBUG

# Server
server.port=8080
server.servlet.context-path=/api
```

O copia y adapta `APPLICATION_PROPERTIES_TEMPLATE.properties`

### 3. Instalar Dependencias

```bash
cd planetbooks-back
mvn clean install
```

### 4. Ejecutar la Aplicación

```bash
# Opción 1: Maven
mvn spring-boot:run

# Opción 2: Desde IDE
# Click derecho en PlanetbooksBackApplication.java → Run

# Opción 3: Build y ejecutar JAR
mvn clean package
java -jar target/planetbooks-back-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: `http://localhost:8080/api`

## 📝 Estructura del Proyecto

```
src/main/java/com/rodrigomv/planetbooksback/
├── model/
│   ├── entity/              ✅ Entidades JPA
│   │   ├── Role.java
│   │   ├── OrderStatus.java
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── ProductItem.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   └── dto/                 ✅ Data Transfer Objects
│       ├── UserDTO.java
│       ├── ProductDTO.java
│       ├── CartDTO.java
│       ├── OrderDTO.java
│       └── ...
├── repository/              ✅ JPA Repositories
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   └── ...
├── service/                 ✅ Business Logic
│   ├── UserService.java
│   ├── ProductService.java
│   ├── CartService.java     ⏳ (próximo)
│   ├── OrderService.java    ⏳ (próximo)
│   └── ...
└── controller/              ⏳ REST Controllers (próximo)
```

## 🔑 Características de las Entidades

### Validaciones Automáticas
- Email válido y único
- Precios no negativos
- Cantidades mínimo 1
- Campos requeridos

### Relaciones JPA Bien Definidas
- OneToMany con cascada y orphan removal
- ManyToOne con lazy loading
- ElementCollection para listas simples

### Performance Optimizado
- Índices en búsquedas frecuentes
- Lazy loading en relaciones
- Constraints a nivel BD

### Seguridad
- Contraseñas hasheadas (BCrypt)
- DTOs sin campos sensibles
- Validaciones en entrada

## 📚 Ejemplos de Uso

### UserService
```java
@Autowired
private UserService userService;

// Registrar usuario
UserDTO newUser = userService.registerUser(new UserRegistrationDTO(
    "Juan", "juan@email.com", "password123"
));

// Obtener usuario
UserDTO user = userService.getUserById(1L);

// Actualizar
userService.updateUser(1L, updateDTO);

// Promover a admin
userService.promoteToAdmin(1L);
```

### ProductService
```java
@Autowired
private ProductService productService;

// Obtener productos con paginación
Page<ProductDTO> page = productService.getAllProducts(
    PageRequest.of(0, 10)
);

// Buscar por título
Page<ProductDTO> results = productService.searchByTitle(
    "Cambridge", 
    PageRequest.of(0, 10)
);

// Filtrar por tag
Page<ProductDTO> yle = productService.getProductsByTag(
    "YLE",
    PageRequest.of(0, 10)
);

// Obtener producto específico
ProductDTO product = productService.getProductById(1L);
```

## 🔐 Configuración de Seguridad (Próximo)

Se recomienda implementar:

```java
// 1. Usar BCryptPasswordEncoder
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// 2. JWT Authentication
// 3. Spring Security con roles
// 4. CORS configuration
```

## 🛣️ Endpoints Sugeridos (Próximo)

```
# Autenticación
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout

# Productos
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search?q=...&tag=...&level=...

# Carrito
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{id}
DELETE /api/cart/items/{id}

# Órdenes
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
PUT    /api/orders/{id}

# Usuarios (admin)
GET    /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

## 📋 Checklist de Desarrollo

- [x] Entidades JPA
- [x] DTOs
- [x] Repositorios
- [x] Servicios básicos
- [ ] Controladores REST
- [ ] Spring Security
- [ ] JWT Authentication
- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Documentación Swagger/OpenAPI
- [ ] Logging y monitoreo
- [ ] Validación de payload
- [ ] Manejo de errores
- [ ] CORS configuration

## 📖 Documentación Adicional

- `ENTITIES_IMPLEMENTATION.md` - Detalles de implementación
- `DATABASE_DDL.sql` - Schema completo de BD
- `APPLICATION_PROPERTIES_TEMPLATE.properties` - Configuración
- `pom.xml` - Dependencias Maven

## 🛠️ Stack Tecnológico

- **Java 17**
- **Spring Boot 4.0.6**
- **Spring Data JPA**
- **Hibernate**
- **Spring Security**
- **Lombok**
- **MySQL 8.0** (o PostgreSQL)
- **Maven**

## 🤝 Contribuir

Las próximas tareas son:
1. Crear controladores REST
2. Implementar seguridad con JWT
3. Crear tests unitarios
4. Documentar con Swagger

## 📞 Soporte

En caso de problemas:
1. Verificar MySQL/PostgreSQL está corriendo
2. Validar credenciales en application.properties
3. Revisar logs del servidor: `target/` → `build` logs
4. Ejecutar: `mvn clean install`

## 📄 Licencia

PlanetBooks Backend © 2026

---

**¡El backend está listo para empezar a agregar controladores REST!**

