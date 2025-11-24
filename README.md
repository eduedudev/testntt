# Sistema Bancario - NTT Data

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-blue.svg)](https://gradle.org/)
[![Docker](https://img.shields.io/badge/Docker-20.10+-blue.svg)](https://www.docker.com/)

Sistema bancario microservicios desarrollado con arquitectura hexagonal, implementando gestión de clientes, cuentas y movimientos bancarios con comunicación asíncrona mediante Kafka.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Uso](#-uso)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Casos de Uso](#-casos-de-uso)

## 🚀 Características

- **Arquitectura Hexagonal (Ports & Adapters)**: Separación clara entre dominio, aplicación e infraestructura
- **API-First Development**: Especificaciones OpenAPI con generación automática de código
- **Programación Reactiva**: Spring WebFlux con R2DBC para alta concurrencia
- **Mensajería Asíncrona**: Apache Kafka para comunicación entre microservicios
- **Bases de Datos Separadas**: PostgreSQL con R2DBC para cada microservicio
- **Documentación Interactiva**: Swagger UI integrado con SpringDoc
- **Contenerización**: Docker y Docker Compose para despliegue simplificado
- **Clean Code**: Lombok y MapStruct para código limpio y mantenible

## 🏗️ Arquitectura

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND / CLIENTS                       │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ├─────────────────────┬──────────────────────────┐
                 ▼                     ▼                          ▼
         ┌───────────────┐     ┌───────────────┐        ┌──────────────┐
         │  Customer API │     │  Account API  │        │  Swagger UI  │
         │   (Port 8080) │     │  (Port 8081)  │        │              │
         └───────┬───────┘     └───────┬───────┘        └──────────────┘
                 │                     │
         ┌───────▼───────┐     ┌───────▼───────┐
         │  Customer     │     │   Account     │
         │  Service      │◄────┤   Service     │
         │               │     │               │
         │ - Customers   │     │ - Accounts    │
         │ - Persons     │     │ - Movements   │
         │               │     │ - Reports     │
         └───────┬───────┘     └───────┬───────┘
                 │                     │
                 │    ┌────────────┐   │
                 └────►   Kafka    ◄───┘
                      │ (Messaging)│
                      └────────────┘
                 │                     │
         ┌───────▼────────┐    ┌──────▼────────┐
         │  Customer DB   │    │  Account DB   │
         │  PostgreSQL    │    │  PostgreSQL   │
         │  (Port 5432)   │    │  (Port 5433)  │
         └────────────────┘    └───────────────┘
```

### Arquitectura Hexagonal

Cada microservicio sigue el patrón de arquitectura hexagonal:

```
customer-service/
├── domain/               # Capa de Dominio (Entidades, Value Objects, Excepciones)
│   ├── model/           # Entidades y Value Objects
│   ├── exception/       # Excepciones de negocio
│   └── port/            # Interfaces (Puertos)
│       ├── in/          # Casos de uso (entrada)
│       └── out/         # Repositorios (salida)
├── application/         # Capa de Aplicación (Lógica de negocio)
│   ├── service/        # Implementación de servicios
│   └── usecase/        # Implementación de casos de uso
└── infrastructure/      # Capa de Infraestructura (Adaptadores)
    ├── adapter/
    │   ├── in/         # Adaptadores de entrada (REST, Events)
    │   └── out/        # Adaptadores de salida (DB, Kafka)
    └── config/         # Configuración (Spring, Kafka, OpenAPI)
```

## 🛠️ Tecnologías

### Core Framework
- **Java 17**: Lenguaje de programación
- **Spring Boot 3.4.1**: Framework principal
- **Spring WebFlux**: Programación reactiva
- **Spring Data R2DBC**: Acceso reactivo a base de datos

### Comunicación
- **Apache Kafka**: Mensajería asíncrona entre servicios
- **Reactor Kafka**: Cliente reactivo para Kafka

### Base de Datos
- **PostgreSQL 15**: Base de datos relacional
- **R2DBC PostgreSQL**: Driver reactivo

### Documentación
- **OpenAPI 3.0**: Especificación de API
- **OpenAPI Generator 7.10.0**: Generación de código desde OpenAPI
- **SpringDoc OpenAPI 2.3.0**: Documentación Swagger UI

### Utilidades
- **Lombok**: Reducción de código boilerplate
- **MapStruct 1.5.5**: Mapeo de objetos
- **Jackson**: Serialización JSON

### Infraestructura
- **Docker & Docker Compose**: Contenerización
- **Gradle 8.5**: Build automation

## 📦 Requisitos Previos

- **Java 17** o superior
- **Docker** 20.10+ y **Docker Compose** 2.0+
- **Gradle** 8.5+ (opcional, incluye wrapper)
- **jq** (para ejecutar casos de uso): `sudo apt-get install jq`

## 🔧 Instalación

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd testntt
```

### 2. Construir los servicios

```bash
# Generar las clases desde OpenAPI y compilar
./gradlew clean build -x test
```

### 3. Levantar la infraestructura

```bash
# Iniciar todos los servicios con Docker Compose
docker compose up -d
```

Esto levantará:
- **customer-db**: PostgreSQL en puerto 5432
- **account-db**: PostgreSQL en puerto 5433
- **zookeeper**: Coordinador de Kafka en puerto 2181
- **kafka**: Broker de mensajería en puerto 9092
- **customer-service**: API REST en puerto 8080
- **account-service**: API REST en puerto 8081

### 4. Verificar el estado

```bash
# Ver los logs de los servicios
docker compose logs -f

# Verificar que todos los contenedores estén corriendo
docker compose ps
```

## ⚙️ Configuración

### Variables de Entorno

Las siguientes variables pueden ser configuradas en `docker-compose.yml`:

#### Customer Service
```yaml
SPRING_R2DBC_URL: r2dbc:postgresql://customer-db:5432/customer_db
SPRING_R2DBC_USERNAME: postgres
SPRING_R2DBC_PASSWORD: postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

#### Account Service
```yaml
SPRING_R2DBC_URL: r2dbc:postgresql://account-db:5432/account_db
SPRING_R2DBC_USERNAME: postgres
SPRING_R2DBC_PASSWORD: postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

### Puertos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| customer-service | 8080 | API REST de clientes |
| account-service | 8081 | API REST de cuentas |
| customer-db | 5432 | PostgreSQL (clientes) |
| account-db | 5433 | PostgreSQL (cuentas) |
| kafka | 9092 | Broker Kafka |
| zookeeper | 2181 | Coordinador Kafka |

## 🎯 Uso

### API Endpoints

#### Customer Service (Puerto 8080)

**Crear Cliente**
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Perez",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Calle Principal 123",
    "phone": "0987654321",
    "password": "password123",
    "status": true
  }'
```

**Obtener Todos los Clientes**
```bash
curl http://localhost:8080/api/v1/customers
```

**Obtener Cliente por ID**
```bash
curl http://localhost:8080/api/v1/customers/{customerId}
```

**Actualizar Cliente**
```bash
curl -X PUT http://localhost:8080/api/v1/customers/{customerId} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Perez Actualizado",
    "address": "Nueva Direccion 456",
    "phone": "0999999999",
    "status": true
  }'
```

**Eliminar Cliente**
```bash
curl -X DELETE http://localhost:8080/api/v1/customers/{customerId}
```

#### Account Service (Puerto 8081)

**Crear Cuenta**
```bash
curl -X POST http://localhost:8081/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "123456",
    "accountType": "SAVING",
    "initialBalance": 1000.00,
    "status": true,
    "customerId": "{customer-uuid}"
  }'
```

**Crear Movimiento**
```bash
curl -X POST http://localhost:8081/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "{account-uuid}",
    "movementType": "CREDIT",
    "value": 500.00
  }'
```

**Generar Reporte de Estado de Cuenta**
```bash
curl "http://localhost:8081/api/v1/reports/{customerId}?startDate=2025-11-01&endDate=2025-11-30"
```

### Tipos de Datos

#### Gender (Género)
- `MALE`: Masculino
- `FEMALE`: Femenino
- `OTHER`: Otro

#### Account Type (Tipo de Cuenta)
- `SAVING`: Cuenta de Ahorros
- `CHECKING`: Cuenta Corriente

#### Movement Type (Tipo de Movimiento)
- `CREDIT`: Depósito (incrementa saldo)
- `DEBIT`: Retiro (decrementa saldo)

## 📚 API Documentation

### Swagger UI

Accede a la documentación interactiva de las APIs:

- **Customer Service**: http://localhost:8080/swagger-ui.html
- **Account Service**: http://localhost:8081/swagger-ui.html

### OpenAPI Specifications

Las especificaciones OpenAPI en formato JSON están disponibles en:

- **Customer API**: http://localhost:8080/api-docs
- **Account API**: http://localhost:8081/api-docs

### Archivos de Especificación

Los archivos YAML con las definiciones OpenAPI se encuentran en:

- `customer-api.yaml`: Especificación del servicio de clientes
- `account-api.yaml`: Especificación del servicio de cuentas

## 🧪 Testing

### Ejecutar Casos de Uso Completos

El proyecto incluye un script que ejecuta todos los casos de uso del sistema:

```bash
# Asegurarse de tener jq instalado
sudo apt-get install jq

# Ejecutar el script de casos de uso
bash ejecutar-casos-uso.sh
```

Este script realizará:
1. ✅ Creación de 3 clientes (Jose Lema, Marianela Montalvo, Juan Osorio)
2. ✅ Creación de 5 cuentas bancarias
3. ✅ Ejecución de 4 movimientos (depósitos y retiros)
4. ✅ Generación de reportes de estado de cuenta

### Tests Unitarios

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un servicio específico
./gradlew :customer-service:test
./gradlew :account-service:test

# Ver reporte de cobertura
./gradlew test jacocoTestReport
```

### Tests de Integración

Los tests de integración utilizan Testcontainers para levantar contenedores de PostgreSQL y Kafka:

```bash
./gradlew integrationTest
```

## 📂 Estructura del Proyecto

```
testntt/
├── customer-service/                # Microservicio de Clientes
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/nttdata/customer/
│   │   │   │       ├── domain/              # Capa de Dominio
│   │   │   │       │   ├── model/           # Customer, Person, Address, Phone
│   │   │   │       │   ├── exception/       # Excepciones de negocio
│   │   │   │       │   └── port/
│   │   │   │       │       ├── in/          # CustomerUseCase
│   │   │   │       │       └── out/         # CustomerRepository, CustomerEventPublisher
│   │   │   │       ├── application/         # Capa de Aplicación
│   │   │   │       │   ├── service/         # CustomerService
│   │   │   │       │   └── usecase/         # Implementación de casos de uso
│   │   │   │       └── infrastructure/      # Capa de Infraestructura
│   │   │   │           ├── adapter/
│   │   │   │           │   ├── in/rest/     # Controladores REST (generados)
│   │   │   │           │   └── out/         # Repositorios R2DBC, Kafka Producer
│   │   │   │           └── config/          # KafkaConfig, OpenApiConfig
│   │   │   └── resources/
│   │   │       ├── application.yml          # Configuración del servicio
│   │   │       └── openapi/
│   │   │           └── customer-api.yaml    # Especificación OpenAPI
│   │   └── test/                           # Tests
│   ├── build.gradle                        # Dependencias y configuración Gradle
│   └── Dockerfile                          # Imagen Docker
│
├── account-service/                 # Microservicio de Cuentas
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/nttdata/account/
│   │   │   │       ├── domain/              # Capa de Dominio
│   │   │   │       │   ├── model/           # Account, Movement, AccountType
│   │   │   │       │   ├── exception/       # Excepciones de negocio
│   │   │   │       │   └── port/
│   │   │   │       │       ├── in/          # AccountUseCase, MovementUseCase, ReportUseCase
│   │   │   │       │       └── out/         # Repositorios
│   │   │   │       ├── application/         # Capa de Aplicación
│   │   │   │       │   ├── service/         # AccountService, MovementService, ReportService
│   │   │   │       │   └── usecase/         # Implementación de casos de uso
│   │   │   │       └── infrastructure/      # Capa de Infraestructura
│   │   │   │           ├── adapter/
│   │   │   │           │   ├── in/rest/     # Controladores REST (generados)
│   │   │   │           │   └── out/         # Repositorios R2DBC, Kafka Consumer
│   │   │   │           └── config/          # KafkaConsumerConfig, OpenApiConfig
│   │   │   └── resources/
│   │   │       ├── application.yml          # Configuración del servicio
│   │   │       └── openapi/
│   │   │           └── account-api.yaml     # Especificación OpenAPI
│   │   └── test/                           # Tests
│   ├── build.gradle                        # Dependencias y configuración Gradle
│   └── Dockerfile                          # Imagen Docker
│
├── gradle/                          # Gradle Wrapper
├── docker-compose.yml               # Orquestación de servicios
├── init-customer-db.sql            # Script de inicialización DB Clientes
├── init-account-db.sql             # Script de inicialización DB Cuentas
├── customer-api.yaml               # Especificación OpenAPI (raíz)
├── account-api.yaml                # Especificación OpenAPI (raíz)
├── ejecutar-casos-uso.sh           # Script de prueba de casos de uso
├── postman_collection.json         # Colección Postman
├── settings.gradle                 # Configuración multi-módulo Gradle
├── gradlew                         # Gradle Wrapper (Unix)
├── gradlew.bat                     # Gradle Wrapper (Windows)
├── SWAGGER.md                      # Documentación Swagger
└── README.md                       # Este archivo
```

## 📝 Casos de Uso

### Caso 1: Gestión de Clientes

**Crear un nuevo cliente**
1. El usuario envía una solicitud POST con datos del cliente
2. El sistema valida que no exista un cliente con la misma identificación
3. Se crea el cliente en la base de datos
4. Se publica un evento `CUSTOMER_CREATED` en Kafka
5. Se retorna el cliente creado con su UUID

**Actualizar un cliente**
1. El usuario envía una solicitud PUT con el ID del cliente
2. El sistema verifica que el cliente exista
3. Se actualizan los datos del cliente
4. Se publica un evento `CUSTOMER_UPDATED` en Kafka
5. Se retorna el cliente actualizado

**Eliminar un cliente**
1. El usuario envía una solicitud DELETE con el ID del cliente
2. El sistema verifica que el cliente exista
3. Se marca el cliente como inactivo (soft delete)
4. Se publica un evento `CUSTOMER_DELETED` en Kafka
5. Se retorna confirmación

### Caso 2: Gestión de Cuentas

**Crear una cuenta bancaria**
1. El usuario envía una solicitud POST con datos de la cuenta
2. El sistema valida que no exista una cuenta con el mismo número
3. El account-service escucha eventos de Kafka para sincronizar datos del cliente
4. Se crea la cuenta con el saldo inicial
5. Se retorna la cuenta creada

**Consultar cuentas de un cliente**
1. El usuario envía una solicitud GET con el ID del cliente
2. El sistema busca todas las cuentas del cliente
3. Se retorna la lista de cuentas con sus saldos actuales

### Caso 3: Gestión de Movimientos

**Registrar un movimiento (depósito o retiro)**
1. El usuario envía una solicitud POST con datos del movimiento
2. El sistema verifica que la cuenta exista
3. Si es un DEBIT (retiro), valida que haya saldo suficiente
4. Se actualiza el saldo de la cuenta
5. Se registra el movimiento en el historial
6. Se retorna el movimiento con el nuevo saldo

**Validación de saldo insuficiente**
- Si se intenta retirar más dinero del disponible
- El sistema rechaza la operación con error 400
- Se retorna mensaje: "Saldo no disponible"

### Caso 4: Reportes

**Generar estado de cuenta**
1. El usuario solicita un reporte con ID de cliente y rango de fechas
2. El sistema obtiene información del cliente
3. Busca todas las cuentas del cliente
4. Para cada cuenta, obtiene movimientos en el rango de fechas
5. Genera un reporte consolidado con:
   - Información del cliente
   - Lista de cuentas con saldo actual
   - Movimientos de cada cuenta (fecha, tipo, valor, saldo resultante)
6. Se retorna el reporte en formato JSON

## 🔄 Flujo de Comunicación Kafka

### Eventos Publicados

**Customer Service → Kafka**
```json
{
  "eventType": "CUSTOMER_CREATED",
  "customerId": "uuid",
  "customerName": "Jose Lema",
  "timestamp": "2025-11-24T10:30:00Z"
}
```

**Account Service ← Kafka**
- El account-service escucha el topic `customer-events`
- Sincroniza información del cliente en la tabla `customer_info`
- Permite generar reportes sin llamadas síncronas entre servicios

## 🐛 Troubleshooting

### Los servicios no inician

```bash
# Verificar logs
docker compose logs customer-service
docker compose logs account-service

# Verificar que las bases de datos estén listas
docker compose logs customer-db
docker compose logs account-db

# Reiniciar servicios
docker compose restart
```

### Error de conexión a Kafka

```bash
# Verificar que Kafka esté corriendo
docker compose ps kafka

# Ver logs de Kafka
docker compose logs kafka

# Recrear Kafka
docker compose stop kafka zookeeper
docker compose up -d zookeeper kafka
```

### Base de datos no inicializa correctamente

```bash
# Eliminar volúmenes y recrear
docker compose down -v
docker compose up -d
```

### El script de casos de uso falla

```bash
# Verificar que jq esté instalado
which jq || sudo apt-get install jq

# Dar permisos de ejecución
chmod +x ejecutar-casos-uso.sh

# Verificar que los servicios estén corriendo
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

## 🔐 Seguridad

⚠️ **NOTA**: Esta es una aplicación de demostración. Para producción, considerar:

- Implementar autenticación y autorización (Spring Security, JWT)
- Encriptar contraseñas con BCrypt
- Usar HTTPS/TLS
- Implementar rate limiting
- Validar y sanitizar todas las entradas
- Usar secrets management para credenciales
- Implementar auditoría de operaciones

## 📄 Licencia

Este proyecto es parte de una prueba técnica para NTT Data.

## 👥 Contacto

Para preguntas o soporte, contactar al equipo de desarrollo.

---

**Desarrollado con ❤️ usando Spring Boot, Kafka y arquitectura hexagonal**
