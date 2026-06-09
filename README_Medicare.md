# MediCare API — Guía Paso a Paso para Junior Developers

## Índice

1. [Descripción del Proyecto](#1-descripción-del-proyecto)
2. [Prerrequisitos](#2-prerrequisitos)
3. [Configuración del Entorno](#3-configuración-del-entorno)
4. [Estructura del Proyecto](#4-estructura-del-proyecto)
5. [Hitos de Desarrollo](#5-hitos-de-desarrollo)
6. [Requerimientos Funcionales Completos](#6-requerimientos-funcionales-completos)
7. [Requerimientos Técnicos](#7-requerimientos-técnicos)
8. [Testing](#8-testing)
9. [Troubleshooting](#9-troubleshooting)
10. [Entregables y Evaluación](#10-entregables-y-evaluación)

---

## 1. Descripción del Proyecto

**MediCare API** es el backend de un sistema de gestión hospitalaria que administra:

- **Usuarios y roles** (Admin, Doctor, Patient)
- **Citas médicas** con validación de disponibilidad
- **Historial clínico** en base de datos NoSQL
- **Integración con API externa** de medicamentos

El sistema garantiza seguridad con JWT, auditoría de acciones críticas y arquitectura limpia por capas.

---

## 2. Prerrequisitos

### Software necesario

| Software | Versión mínima | Comando para verificar | Enlace de descarga |
|---|---|---|---|
| Java (JDK) | 25 LTS | `java -version` | https://adoptium.net/temurin/ |
| Gradle | 8.x+ | `gradle -v` | https://gradle.org/download/ |
| Docker | 24+ | `docker --version` | https://docs.docker.com/get-docker/ |
| Docker Compose | v2+ | `docker compose version` | Viene con Docker Desktop |
| IntelliJ IDEA | 2025.1+ | O cualquier IDE de preferencia | https://www.jetbrains.com/idea/download/ |
| Postman | Última versión | O usar curl/Swagger | https://www.postman.com/downloads/ |

### Cuentas necesarias

- GitHub (para el repositorio)
- No se requieren API keys externas para el desarrollo base

---

## 3. Configuración del Entorno

### Paso 3.1 — Crear el repositorio

1. Crear un repositorio en GitHub llamado `medicare-api`
2. Clonarlo localmente:

```bash
git clone https://github.com/TU_USUARIO/medicare-api.git
cd medicare-api
```

### Paso 3.2 — Estructura del proyecto

El proyecto utiliza **Gradle** como sistema de build. La estructura del repositorio es:

```
medicare-api/
├── medicare/              # Módulo principal
│   ├── build.gradle       # Archivo de build Gradle
│   ├── settings.gradle    # Configuración de Gradle
│   └── src/
│       ├── main/
│       └── test/
└── README_Medicare.md     # Esta guía
```

### Paso 3.3 — Levantar las bases de datos

Crear un archivo `docker-compose.yml` en la carpeta `medicare/` con estos servicios:

**PostgreSQL** (para datos relacionales: Users, Doctors, Patients, Appointments):
- Imagen: `postgres:17`
- Puerto: `5432`
- Base de datos: `medicare`
- Usuario: `admin`
- Contraseña: `admin123`

**MongoDB** (para datos no estructurados: MedicalRecords, AuditLogs):
- Imagen: `mongo:7`
- Puerto: `27017`
- Usuario: `admin`
- Contraseña: `admin123`

Levantar los contenedores:

```bash
docker compose up -d
```

Verificar que ambos estén corriendo:

```bash
docker compose ps
```

Ambos servicios deben mostrar estado `Up`.

### Paso 3.4 — Inicializar el proyecto Spring Boot

**Opción A — Spring Initializr (recomendada para juniors):**
1. Ir a https://start.spring.io/
2. Configurar:
   - Project: **Gradle - Groovy**
   - Language: Java
   - Spring Boot: 4.0.6
   - Group: `com.medicare`
   - Artifact: `api`
   - Name: `api`
   - Package name: `com.medicare.api`
   - Packaging: Jar
   - Java: 25
3. Agregar las dependencias (ver sección 3.5)
4. Generate → Extraer en la carpeta `medicare/` del proyecto

**Opción B — Crear manualmente:**
Crear la estructura de carpetas manualmente y el `build.gradle` con todas las dependencias.

### Paso 3.5 — Dependencias del build.gradle

Agregar estas dependencias en tu `build.gradle`. Busca en https://mvnrepository.com/ la última versión estable de cada una:

**Core de Spring Boot:**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-webmvc'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

**Base de datos:**
```groovy
runtimeOnly 'org.postgresql:postgresql'
testImplementation 'com.h2database:h2'
```

**Migraciones:**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-flyway'
runtimeOnly 'org.flywaydb:flyway-database-postgresql'
```

**Seguridad JWT:**
```groovy
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

**Documentación API:**
```groovy
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3'
```

**MapStruct (mapeo DTO ↔ Entity):**
```groovy
implementation 'org.mapstruct:mapstruct:1.6.3'
annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
```

**Resiliencia:**
```groovy
implementation 'io.github.resilience4j:resilience4j-spring-boot4:2.4.0'
```

**Logging:**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-log4j2'

configurations {
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
}
```

**Testing:**
```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.boot:spring-boot-data-jpa-test'
testImplementation 'org.springframework.boot:spring-boot-webmvc-test'
testImplementation 'org.springframework.security:spring-security-test'
```

### Paso 3.6 — Estructura de paquetes

```
com.medicare.api
├── config/           # Configuraciones globales (Security, OpenAPI, Auditing)
├── controller/       # Endpoints REST
├── dto/              # Records para request/response
│   ├── request/      # DTOs de entrada
│   └── response/     # DTOs de salida
├── model/            # Entidades JPA y documentos MongoDB
│   ├── entity/       # Entidades JPA (User, Doctor, Patient, Appointment)
│   └── document/     # Documentos MongoDB (MedicalRecord, AuditLog)
├── repository/       # Interfaces de repositorio
│   ├── jpa/          # Repositorios JPA
│   └── mongo/        # Repositorios MongoDB
├── service/          # Lógica de negocio
│   └── impl/         # Implementaciones
├── security/         # Filtros JWT, Provider, Config
├── exception/        # Excepciones personalizadas y handler global
├── util/             # Utilidades (mappers, constantes)
└── integration/      # Consumo de APIs externas
```

### Paso 3.8 — Verificar compilación

```bash
# Usando Gradle
./gradlew clean build

# O usando el wrapper de Gradle si está configurado
gradle clean build
```

Si compila sin errores, el entorno está listo. Si hay errores de dependencias, revisar la sección de Troubleshooting (sección 9).

---

## 4. Estructura del Proyecto

### Arquitectura por capas

```
┌─────────────────────────────────────────┐
│           Controller (REST API)         │  ← Recibe HTTP, valida DTOs, retorna DTOs
├─────────────────────────────────────────┤
│             Service (Business)          │  ← Lógica de negocio, validaciones, transacciones
├─────────────────────────────────────────┤
│           Repository (Data Access)      │  ← Comunicación con bases de datos
├─────────────────────────────────────────┤
│     Database (PostgreSQL / MongoDB)     │  ← Persistencia
└─────────────────────────────────────────┘
```

### Reglas de la arquitectura

1. **Los Controllers NUNCA contienen lógica de negocio** — solo validan el DTO de entrada y delegan al Service
2. **Los Services NUNCA exponen entidades JPA** — retornan DTOs
3. **Los DTOs son Records de Java** (inmutables, con constructor automático)
4. **Las Entidades JPA nunca se exponen en respuestas HTTP**
5. **MapStruct** se encarga del mapeo Entity ↔ DTO (o métodos manuales en el Service)

---

## 5. Hitos de Desarrollo

### Hito 1 — Modelo de datos y CRUD básico (Semana 1-2)

**Objetivo:** Entidades JPA funcionando con PostgreSQL y Flyway.

**Tareas:**

1. **Crear las entidades JPA** con sus relaciones:
   - `User` (id, email, password, role, createdAt, updatedAt)
   - `Doctor` (id, specialization, user → relación con User)
   - `Patient` (id, dateOfBirth, phone, user → relación con User)
   - `Appointment` (id, dateTime, status, doctor → relación, patient → relación)
   - `Role` como enum: `ADMIN`, `DOCTOR`, `PATIENT`

2. **Configurar JPA Auditing:**
   - Habilitar `@EnableJpaAuditing` en una clase de configuración
   - Usar `@CreatedDate` y `@LastModifiedDate` en las entidades

3. **Crear migraciones Flyway:**
   - Archivo: `V1__init_schema.sql`
   - Crear tablas: `users`, `doctors`, `patients`, `appointments`
   - Definir constraints: foreign keys, unique constraints
   - **Nunca usar `ddl-auto=update`** — solo Flyway para gestionar el esquema

4. **Crear Repositorios JPA:**
   - `UserRepository extends JpaRepository<User, Long>`
   - `DoctorRepository`, `PatientRepository`, `AppointmentRepository`

5. **Crear DTOs de request/response:**
   - `CreateUserRequest` (record)
   - `UserResponse` (record)
   - `CreateDoctorRequest`, `DoctorResponse`, etc.

6. **Implementar CRUD para Users:**
   - `UserController` con endpoints GET, POST, PUT, DELETE
   - `UserService` con lógica de negocio
   - Usar `@Valid` en los DTOs de entrada

7. **Escribir Unit Tests:**
   - Test de validación de campos obligatorios
   - Test de que el Service llama al Repository

**Criterio de aceptación:**
- Las migraciones se ejecutan correctamente al iniciar la app
- Se puede crear, leer, actualizar y eliminar Users via HTTP
- Los DTOs no exponen la entidad JPA directamente

---

### Hito 2 — Seguridad y JWT (Semana 3)

**Objetivo:** Autenticación funcionando con JWT y control de acceso por roles.

**Tareas:**

1. **Configurar Spring Security 7:**
   - Crear clase de configuración con `SecurityFilterChain`
   - Deshabilitar CSRF (es una API REST)
   - Configurar rutas públicas: `POST /api/auth/register`, `POST /api/auth/login`
   - Proteger todas las demás rutas

2. **Implementar registro de pacientes:**
   - Endpoint `POST /api/auth/register` (público)
   - Crea usuario con rol `PATIENT`
   - Hashear la contraseña con `BCryptPasswordEncoder`

3. **Implementar login:**
   - Endpoint `POST /api/auth/login`
   - Retornar JWT en el body de la respuesta
   - Estructura del token: subject (email), roles, expiration

4. **Crear filtro JWT:**
   - Interceptar cada request
   - Extraer el header `Authorization: Bearer <token>`
   - Validar el token
   - Establecer el usuario en el `SecurityContext`

5. **Control de acceso por roles:**
   - `POST /api/admin/users` → solo `ADMIN`
   - `POST /api/doctors` → solo `ADMIN`
   - `GET /api/appointments/mine` → `PATIENT` o `DOCTOR`
   - Usar `@PreAuthorize` en los controllers

6. **Crear endpoint para que Admin cree doctores:**
   - `POST /api/admin/doctors` → solo `ADMIN`
   - Crear usuario con rol `DOCTOR`

**Criterio de aceptación:**
- Login retorna JWT válido
- Requests sin token son rechazados (401)
- Requests con token inválido son rechazados (401)
- Un `PATIENT` no puede acceder a endpoints de `ADMIN`
- Un `ADMIN` puede crear doctores

---

### Hito 3 — Gestión de Citas (Semana 4)

**Objetivo:** Agendamiento de citas con validación de disponibilidad.

**Tareas:**

1. **Implementar servicio de Citas:**
   - `AppointmentService` con método `createAppointment`
   - `AppointmentController` con endpoints

2. **Implementar validación de overlap (CRÍTICO):**
   - Antes de crear una cita, verificar que el doctor NO tenga otra cita en el mismo horario
   - Algoritmo: buscar si existe alguna cita del doctor donde `existingDateTime` esté dentro del rango de la nueva cita
   - Lanzar `AppointmentConflictException` (409 Conflict) si hay conflicto

3. **Implementar cancelación de citas:**
   - `PATCH /api/appointments/{id}/cancel`
   - Solo el paciente que posee la cita puede cancelarla
   - Validar que falten más de 24 horas para la cita
   - Lanzar `LateCancellationException` (400 Bad Request) si es menos de 24h

4. **Crear excepciones personalizadas:**
   - `AppointmentConflictException`
   - `LateCancellationException`
   - `ResourceNotFoundException` (para 404)
   - `UnauthorizedActionException` (para 403)

5. **Crear handler global de excepciones:**
   - Clase con `@RestControllerAdvice`
   - Método para cada tipo de excepción
   - Retornar formato RFC 7807 (Problem Details):
     ```json
     {
       "type": "https://api.medicare.com/errors/appointment-conflict",
       "title": "Appointment Conflict",
       "status": 409,
       "detail": "Doctor already has an appointment at 2026-06-10T10:00:00",
       "instance": "/api/appointments"
     }
     ```

6. **Escribir Unit Tests para la lógica de overlap:**
   - Test: doctor disponible → cita creada exitosamente
   - Test: doctor ocupado en mismo horario → `AppointmentConflictException`
   - Test: cancelación con más de 24h → exitosa
   - Test: cancelación con menos de 24h → `LateCancellationException`

**Criterio de aceptación:**
- No se puede agendar dos citas para el mismo doctor en el mismo horario
- La cancelación valida las 24 horas de antelación
- Los errores retornan formato RFC 7807
- Los tests cubren casos de éxito y fallo

---

### Hito 4 — Historial Clínico y API Externa (Semana 5)

**Objetivo:** MongoDB funcionando para notas clínicas + integración con API externa.

**Tareas:**

1. **Crear documento MongoDB:**
   - `MedicalRecord` con campos: `appointmentId`, `doctorId`, `patientId`, `notes`, `symptoms`, `prescriptions`, `createdAt`
   - Campos flexibles: `notes` puede ser texto libre, `symptoms` puede ser una lista, `prescriptions` un objeto anidado
   - Usar `@Document(collection = "medical_records")`

2. **Crear repositorio MongoDB:**
   - `MedicalRecordRepository extends MongoRepository<MedicalRecord, String>`

3. **Implementar servicio de notas clínicas:**
   - `ClinicalNoteService`
   - Endpoint: `POST /api/appointments/{id}/notes` (solo el doctor asignado)
   - Endpoint: `GET /api/patients/{id}/records` (solo el paciente o un admin)

4. **Implementar integración con API externa:**
   - Crear un `RestClient` para consumir una API de medicamentos
   - Usar la API pública OpenFDA (https://api.fda.gov/) o crear un Mock en Postman
   - Endpoint a consultar: `https://api.fda.gov/drug/drugsfda.json?search=active_ingredients.name:{medicamento}&limit=1`

5. **Implementar Circuit Breaker:**
   - Configurar Resilience4j en `application.yml`
   - Tiempo de espera: 2 segundos
   - Si la API falla o tarda más de 2s:
     - Retornar advertencia: `"Verificación de medicamento pendiente"`
     - Guardar la nota localmente sin verificar
   - No usar `@CircuitBreaker` directamente — usar el patrón con `fallbackMethod`

6. **Implementar Auditoría:**
   - Crear documento MongoDB `AuditLog` con: `userId`, `action`, `entityType`, `entityId`, `timestamp`, `details`
   - Registrar cada acción crítica: crear/modificar cita, agregar nota clínica

**Criterio de aceptación:**
- Las notas clínicas se guardan en MongoDB
- Los datos flexibles se almacenan sin alterar esquema SQL
- Si la API externa falla, la app no se cae
- Las acciones críticas quedan registradas en AuditLog

---

### Hito 5 — Testing, Documentación y Pulido (Semana 6)

**Objetivo:** Tests completos, Swagger funcional, logging estructurado.

**Tareas:**

1. **Unit Tests (JUnit 5 + Mockito):**
   - Testear la lógica de negocio en Services
   - Mockear Repositorios con `@Mock`
   - Verificar interacciones con `verify()`
   - Cubrir: happy path, validaciones, excepciones

2. **Integration Tests:**
   - Usar `@SpringBootTest` con `@AutoConfigureMockMvc`
   - Testear el flujo completo: Login → Agendar Cita → Agregar Nota
   - Usar H2 para la base de datos de test
   - Crear datos de prueba en `@BeforeEach`

3. **Configurar SpringDoc OpenAPI:**
   - Agregar anotaciones `@Operation`, `@ApiResponse` en los controllers
   - Configurar `OpenAPI` bean con título, descripción, versión
   - Verificar Swagger UI en `http://localhost:8080/swagger-ui.html`

4. **Configurar Log4j2:**
   - Excluir `spring-boot-starter-logging` del build.gradle
   - Agregar `spring-boot-starter-log4j2`
   - Crear `log4j2-spring.xml` en resources
   - Usar `logger.info()` en controllers y `logger.error()` en exception handlers
   - Ejemplo: `logger.info("Login successful for user: {}", username);`

5. **Documentación del README del proyecto:**
   - Cómo levantar el proyecto (docker compose + ./gradlew bootRun)
   - Variables de entorno necesarias
   - Endpoints disponibles
   - Ejemplos de uso con curl o Postman

6. **Crear colección Postman:**
   - Exportar como JSON
   - Incluir: Register, Login, Create Doctor, Create Appointment, Add Note
   - Incluir casos de error

**Criterio de aceptación:**
- Tests unitarios cubren la lógica de negocio principal
- Tests de integración cubren el flujo completo
- Swagger muestra todos los endpoints con descripciones
- La app tiene logs estructurados para debugging

---

## 6. Requerimientos Funcionales Completos

### 6.1 Autenticación

| Endpoint | Método | Rol | Descripción |
|---|---|---|---|
| `/api/auth/register` | POST | Público | Registra un paciente |
| `/api/auth/login` | POST | Público | Retorna JWT |

### 6.2 Gestión de Usuarios (Admin)

| Endpoint | Método | Rol | Descripción |
|---|---|---|---|
| `/api/admin/doctors` | POST | ADMIN | Crea un doctor |
| `/api/admin/doctors` | GET | ADMIN | Lista todos los doctores |
| `/api/admin/patients` | GET | ADMIN | Lista todos los pacientes |

### 6.3 Citas

| Endpoint | Método | Rol | Descripción |
|---|---|---|---|
| `/api/appointments` | POST | PATIENT | Agendar una cita |
| `/api/appointments/mine` | GET | PATIENT/DOCTOR | Ver mis citas |
| `/api/appointments/{id}/cancel` | PATCH | PATIENT | Cancelar una cita |
| `/api/appointments/{id}/notes` | POST | DOCTOR | Agregar nota clínica |

### 6.4 Historial Clínico

| Endpoint | Método | Rol | Descripción |
|---|---|---|---|
| `/api/patients/{id}/records` | GET | PATIENT/ADMIN | Ver historial del paciente |

---

## 7. Requerimientos Técnicos

### Stack tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Framework | Spring Boot | 4.0.6 |
| Java | JDK | 25 LTS |
| Build Tool | Gradle | 8.x+ |
| Seguridad | Spring Security | 7.0.5 |
| ORM | Hibernate (via Spring Data JPA) | 7.2.x |
| DB Relacional | PostgreSQL | 17 |
| DB NoSQL | MongoDB | 7 |
| Migraciones | Flyway | 11.x (via Spring Boot starter) |
| Mapeo DTO | MapStruct | 1.6.3 |
| JWT | jjwt | 0.12.6 |
| Resiliencia | Resilience4j | 2.4.0 |
| Documentación | SpringDoc OpenAPI | 3.0.3 |
| Logging | Log4j2 | via Spring Boot |
| Testing | JUnit 5 + Mockito | via Spring Boot |

### Convenciones de código

- **Naming en inglés:** entidades, métodos, variables, paquetes
- **DTOs como Records:** `record CreateUserRequest(String email, String password, String role) {}`
- **Excepciones personalizadas:** heredan de `RuntimeException`
- **Auditoría:** usar `@CreatedDate`, `@LastModifiedDate` en entidades
- **Validación:** usar `@Valid`, `@NotNull`, `@NotBlank`, `@Email` en DTOs

---

## 8. Testing

### Estrategia de testing

| Tipo | Qué testea | Herramienta | Cuándo ejecutar |
|---|---|---|---|
| Unit | Lógica de negocio aislada | JUnit 5 + Mockito | Cada cambio en Service |
| Integration | Flujo completo Controller→Service→Repository | @SpringBootTest + MockMvc | Antes de cada hito |
| Integration DB | Migraciones y queries | H2 o Testcontainers | Con cada migración |

### Comandos de testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un archivo específico
./gradlew test --tests "*AppointmentServiceTest"

# Ejecutar tests con cobertura (opcional)
./gradlew test jacocoTestReport
```

### Qué testear en cada hito

**Hito 1:**
- Test de que las migraciones crean las tablas correctamente
- Test de CRUD de Users (crear, obtener, actualizar, eliminar)

**Hito 2:**
- Test de login con credenciales correctas
- Test de login con credenciales incorrectas (401)
- Test de acceso a endpoint protegido sin token (401)
- Test de acceso a endpoint de admin con token de paciente (403)

**Hito 3:**
- Test de crear cita exitosa
- Test de crear cita con doctor ocupado (409)
- Test de cancelar cita con más de 24h (éxito)
- Test de cancelar cita con menos de 24h (400)

**Hito 4:**
- Test de guardar nota clínica en MongoDB
- Test de que la app no falla cuando la API externa está caída
- Test de auditoría (se registra la acción)

**Hito 5:**
- Test de integración completo: Register → Login → Create Doctor → Create Appointment → Add Note

---

## 9. Troubleshooting

### Errores comunes y soluciones

| Error | Causa probable | Solución |
|---|---|---|
| `Port 5432 already in use` | PostgreSQL ya corriendo en el sistema | Detener el servicio: `sudo systemctl stop postgresql` o cambiar el puerto en docker-compose |
| `Port 27017 already in use` | MongoDB ya corriendo en el sistema | Detener el servicio: `sudo systemctl stop mongod` o cambiar el puerto |
| `Connection refused` a PostgreSQL | Docker no está corriendo o el contenedor no levantó | Ejecutar `docker compose up -d` y verificar con `docker compose ps` |
| `relation "users" does not exist` | Flyway no ejecutó las migraciones | Verificar que `spring.flyway.enabled=true` y que los archivos de migración están en `db/migration/` |
| `JWT signature does not match` | El secret del token no coincide | Verificar que el `jwt.secret` en `application.yml` sea el mismo al generar y validar el token |
| `Class not found: MapStruct` | Falta el annotation processor en Gradle | Agregar `annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'` en build.gradle |
| `No bean named 'securityFilterChain'` | Falta la configuración de Spring Security | Crear una clase con `@Configuration` y `@Bean` que retorne `SecurityFilterChain` |
| `MongoTimeoutError` | MongoDB no está corriendo o credenciales incorrectas | Verificar docker compose y la URI de conexión en `application.yml` |
| `FlywayException: Validate failed` | Se modificó una migración ya ejecutada | **Nunca modificar** un archivo de migración ya creado — crear uno nuevo con `V{next}__description.sql` |
| `Circular dependency` | Inyección de dependencias circular | Usar `@Lazy` en uno de los beans o reestructurar la lógica |

### Comandos de diagnóstico

```bash
# Ver logs de los contenedores
docker compose logs postgres
docker compose logs mongodb

# Conectar a PostgreSQL directamente
docker compose exec postgres psql -U admin -d medicare

# Ver las tablas creadas por Flyway
docker compose exec postgres psql -U admin -d medicare -c "\dt"

# Ver las migraciones ejecutadas
docker compose exec postgres psql -U admin -d medicare -c "SELECT * FROM flyway_schema_history;"

# Verificar dependencias del proyecto
./gradlew dependencies

# Limpiar y reconstruir
./gradlew clean build
```

---

## 10. Entregables y Evaluación

### Entregables

1. **Repositorio GitHub** con el código fuente (ignorar `target/`, `.idea/`, `*.iml`)
2. **README.md** explicando cómo levantar y ejecutar el proyecto
3. **Colección Postman** exportada como JSON
4. **Video corto** (3-5 min) mostrando el flujo completo

### Rúbrica de Evaluación (100 puntos)

| Categoría | Criterio | Puntos |
|---|---|---|
| **Arquitectura & Spring Boot** | Estructura de paquetes, DI, application.yml con perfiles, separación DTO/Entity | 20 |
| **Persistencia Híbrida** | Mapeo JPA correcto, Flyway funcionando, conexión a Postgres y Mongo | 20 |
| **Seguridad** | SecurityFilterChain, filtro JWT, protección por roles con `@PreAuthorize` | 20 |
| **Lógica & Resiliencia** | Validación de horarios, RestClient con Circuit Breaker | 20 |
| **Calidad** | Swagger funcional, tests cubren casos éxito y fallo | 20 |

### Niveles de desempeño

| Nivel | Puntos | Descripción |
|---|---|---|
| Senior | 90-100 | Arquitectura impecable, GlobalExceptionHandler con RFC 7807, tests exhaustivos |
| Semi-Senior | 75-89 | Funcionalidad completa, BD integradas, detalles pendientes en errores o tests |
| Junior | 60-74 | Happy path funciona, errores con datos inválidos, seguridad básica |
| Insuficiente | <60 | No compila, no conecta BD, no implementa seguridad |

---

## Consejos Finales

1. **Empieza por el Hito 1** — no saltes a JWT sin tener las entidades funcionando
2. **Commit frecuente** — haz commit después de cada tarea completada
3. **Lee los errores** — el stack trace de Spring te dice exactamente qué falla
4. **No copies código sin entenderlo** — si copias de Stack Overflow, asegúrate de saber qué hace cada línea
5. **Usa el debugger** — IntelliJ tiene un debugger muy potente para entender qué está pasando
6. **Documenta mientras avanzas** — no dejes el README para el final
7. **Si te bloqueas más de 2 horas** — busca ayuda o revisa la sección de Troubleshooting

---

*Última actualización: Junio 2026*
