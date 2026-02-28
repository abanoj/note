# NoteApp

API REST de gestión de notas y checklists construida con Spring Boot. Incluye autenticación JWT con rotación de tokens, seguimiento de progreso en checklists, notas de texto y operaciones CRUD completas — todo containerizado con Docker. Incluye un frontend en React como cliente de pruebas.

## Stack Tecnológico

**Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA, MySQL, JWT (jjwt), Maven

**Frontend:** React 19, TypeScript, Vite, TailwindCSS

**Infraestructura:** Docker, Docker Compose, Nginx, Adminer

## Funcionalidades

- **Autenticación** — Registro, login y logout con rotación de tokens JWT (access + refresh)
- **Checklists** — Crear, editar y eliminar checklists con ítems anidados
- **Ítems de Checklist** — Seguimiento de estado (Pendiente, En Progreso, Completado) y prioridad (Baja, Media, Alta)
- **Progreso** — Cálculo automático de progreso por checklist (ítems completados / total)
- **Notas de Texto** — CRUD completo para notas con título y contenido
- **Paginación** — Paginación del lado del servidor ordenada por última actualización
- **Aislamiento de Datos** — Cada usuario solo puede acceder a sus propios recursos
- **Documentación de API** — Swagger UI disponible en modo desarrollo

## Arquitectura

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Frontend   │─────▶│   Backend   │─────▶│    MySQL    │
│  React/Nginx │ :3000│ Spring Boot │ :8080│             │ :3306
└─────────────┘      └─────────────┘      └─────────────┘
                                                 │
                                           ┌─────────────┐
                                           │   Adminer    │
                                           │              │ :8888
                                           └─────────────┘
```

### Estructura del Backend

```
backend/src/main/java/com/abanoj/note/
├── auth/           # Servicio de autenticación y DTOs
├── checklist/      # Controller, service, repository, entity, DTOs, mapper
├── config/         # Configuración de seguridad y Swagger
├── exception/      # Manejador global de excepciones y excepciones personalizadas
├── item/           # Controller, service, repository, entity, DTOs, mapper
├── textnote/       # Controller, service, repository, entity, DTOs, mapper
├── token/          # Entity, repository y scheduler de limpieza
└── user/           # Entity y repository
```

### Patrones y Decisiones de Diseño

- **DTO Pattern** — DTOs separados para request y response, con mappers dedicados por entidad
- **Service Layer** — Interfaces con implementaciones concretas para desacoplar la lógica de negocio
- **Global Exception Handler** — Manejo centralizado de errores con respuestas estandarizadas
- **Stateless Auth** — JWT sin sesiones en servidor, con filtro personalizado en la cadena de Spring Security
- **Tenant Isolation** — Todas las queries filtran por el usuario autenticado del SecurityContext
- **Token Rotation** — Cada refresh revoca los tokens anteriores y emite un par nuevo
- **Scheduled Tasks** — Limpieza automática de tokens expirados/revocados (cron diario a las 3:00 AM)
- **Cascade Delete** — Eliminar un checklist elimina automáticamente sus ítems (`orphanRemoval`)

## Endpoints de la API

### Autenticación

| Método | Endpoint                        | Descripción              |
|--------|---------------------------------|--------------------------|
| POST   | `/api/v1/auth/register`         | Registrar nuevo usuario  |
| POST   | `/api/v1/auth/authenticate`     | Iniciar sesión           |
| POST   | `/api/v1/auth/refresh-token`    | Renovar access token     |
| POST   | `/api/v1/auth/logout`           | Cerrar sesión            |

### Checklists

| Método | Endpoint                  | Descripción                    |
|--------|---------------------------|--------------------------------|
| GET    | `/api/v1/checklists`      | Listar checklists (paginado)   |
| GET    | `/api/v1/checklists/:id`  | Obtener checklist por ID       |
| POST   | `/api/v1/checklists`      | Crear checklist                |
| PUT    | `/api/v1/checklists/:id`  | Actualizar checklist           |
| DELETE | `/api/v1/checklists/:id`  | Eliminar checklist             |

### Ítems de Checklist

| Método | Endpoint                                    | Descripción          |
|--------|---------------------------------------------|----------------------|
| GET    | `/api/v1/checklists/:id/items`              | Listar ítems         |
| GET    | `/api/v1/checklists/:id/items/:itemId`      | Obtener ítem por ID  |
| POST   | `/api/v1/checklists/:id/items`              | Crear ítem           |
| PUT    | `/api/v1/checklists/:id/items/:itemId`      | Actualizar ítem      |
| DELETE | `/api/v1/checklists/:id/items/:itemId`      | Eliminar ítem        |

### Notas de Texto

| Método | Endpoint                   | Descripción                       |
|--------|----------------------------|-----------------------------------|
| GET    | `/api/v1/text-notes`       | Listar notas de texto (paginado)  |
| GET    | `/api/v1/text-notes/:id`   | Obtener nota por ID               |
| POST   | `/api/v1/text-notes`       | Crear nota de texto               |
| PUT    | `/api/v1/text-notes/:id`   | Actualizar nota de texto          |
| DELETE | `/api/v1/text-notes/:id`   | Eliminar nota de texto            |

## Modelo de Base de Datos

```
┌──────────┐       ┌──────────────┐       ┌──────────┐
│   User   │──1:N──│   Checklist  │──1:N──│   Item   │
│          │       │              │       │          │
│ id       │       │ id           │       │ id       │
│ firstname│       │ title        │       │ title    │
│ lastname │       │ created      │       │ status   │
│ email    │       │ updated      │       │ priority │
│ password │       │ user_id (FK) │       │ created  │
│ role     │       └──────────────┘       │ updated  │
│          │                              │ checklist│
│          │       ┌──────────────┐       │ _id (FK) │
│          │──1:N──│   TextNote   │       └──────────┘
│          │       │              │
│          │       │ id           │
│          │       │ title        │
│          │       │ content      │
│          │       │ created      │
│          │       │ updated      │
│          │       │ user_id (FK) │
│          │       └──────────────┘
│          │
│          │       ┌──────────────┐
│          │──1:N──│    Token     │
│          │       │              │
│          │       │ id           │
│          │       │ token        │
│          │       │ type         │
│          │       │ revoked      │
│          │       │ expiresAt    │
│          │       │ user_id (FK) │
└──────────┘       └──────────────┘
```

## Seguridad

- Autenticación JWT stateless (sin sesiones en el servidor)
- Hashing de contraseñas con BCrypt
- Access tokens expiran en 15 minutos; refresh tokens en 7 días
- Rotación de tokens en cada refresh (tokens anteriores revocados)
- Limpieza programada de tokens expirados/revocados (diariamente a las 3:00 AM)
- Validación con Bean Validation (Jakarta): `@NotBlank`, `@Email`, `@Size`
- Usuario non-root en contenedores Docker
- Toda la configuración sensible mediante variables de entorno
- Swagger UI deshabilitado en producción
- Stack traces ocultos en respuestas de producción

## Primeros Pasos

### Requisitos Previos

- [Docker](https://www.docker.com/) y Docker Compose
- (Opcional para desarrollo local) Java 17, Maven 3.9+, Node.js 20+

### Ejecutar con Docker Compose

1. Clonar el repositorio:

```bash
git clone https://github.com/abanoj/note.git
cd note
```

2. Crear un archivo `.env` en la raíz del proyecto:

```env
DB_URL=note_db:3306
DB_USERNAME=root
DB_PASSWORD=tu_contraseña
DB_NAME=note
JWT_SECRET=tu_clave_secreta_en_base64
```

3. Iniciar todos los servicios:

```bash
docker compose up --build
```

4. Acceder a la aplicación:

| Servicio      | URL                          |
|---------------|------------------------------|
| Frontend      | http://localhost:3000         |
| API Backend   | http://localhost:8080/api/v1  |
| Swagger UI    | http://localhost:8080/docs    |
| Adminer (BD)  | http://localhost:8888         |

### Ejecutar Localmente (Desarrollo)

**Backend:**

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

El servidor de desarrollo del frontend inicia en `http://localhost:5173` y redirige las peticiones de la API al backend en el puerto 8080.

## Perfiles de Configuración

| Perfil | DDL Mode       | Swagger  | Logs  | Detalles de error |
|--------|----------------|----------|-------|-------------------|
| `dev`  | `create-drop`  | Activo   | DEBUG | Visibles          |
| `prod` | `validate`     | Inactivo | INFO  | Ocultos           |

## Variables de Entorno

| Variable       | Descripción                              |
|----------------|------------------------------------------|
| `DB_URL`       | Host y puerto de MySQL                   |
| `DB_USERNAME`  | Usuario de la base de datos              |
| `DB_PASSWORD`  | Contraseña de la base de datos           |
| `DB_NAME`      | Nombre de la base de datos               |
| `JWT_SECRET`   | Clave de firma JWT codificada en Base64  |
