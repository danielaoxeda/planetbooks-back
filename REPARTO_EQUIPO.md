# Reparto de trabajo: controllers, services y config

## Descripción corta

Dividir el backend entre dos personas para avanzar más rápido: una parte enfocada en usuarios y seguridad, y la otra en productos, carrito, órdenes y configuración general.

## Objetivo

Evitar solapamientos y repartir tareas claras para revisar y entregar el trabajo más fácil.

---

## Reparto propuesto

| Área | Tú | Anthony |
|---|---|---|
| **Controllers** | `AuthController`, `UserController` | `ProductController`, `CartController`, `OrderController` |
| **Services** | `UserService` | `ProductService` |
| **Configuración** | `SecurityConfig`, `JwtTokenProvider` | `CorsConfig`, `application.properties` / variables de entorno |

---

## Detalle por persona

### Tú

Responsable del flujo de autenticación y usuarios:

- `AuthController`
- `UserController`
- `UserService`
- `SecurityConfig`
- `JwtTokenProvider`

### Anthony

Responsable del catálogo y operaciones de compra:

- `ProductController`
- `CartController`
- `OrderController`
- `ProductService`
- `CorsConfig`
- Ajustes de `application.properties` y entorno de despliegue

---

## Cómo dividir el trabajo sin pisarse

### Tú
1. Implementar login/registro.
2. Exponer endpoints de usuario.
3. Proteger rutas con seguridad JWT.
4. Definir permisos y acceso por rol.

### Anthony
1. Exponer endpoints de productos.
2. Implementar carrito y órdenes.
3. Ajustar CORS y configuración general.
4. Preparar parámetros de entorno y despliegue.

---

## Orden recomendado de implementación

1. `SecurityConfig` + `JwtTokenProvider`
2. `AuthController` + `UserController`
3. `ProductController`
4. `CartController`
5. `OrderController`
6. Revisión final de `application.properties`

---

## Nota importante

Actualmente el proyecto tiene implementados `UserService` y `ProductService`, pero los controladores y la configuración de seguridad todavía están pendientes. Por eso este reparto está pensado para la **siguiente fase** del backend.


