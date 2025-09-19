# PillPal (Backend️)

Bienvenido a la API backend para Pillpal, una app web diseñado para permitir el seguimiento de la toma de medicamentos. Esta API gestiona el funcionamento con base de datos para registar medicamentos, programar recordatorios y llevar un control de la toma diaria según las indicaciones locations and connect with an existing React and Next frontend.

## 📋 Índice

- [Características](#-características-mvp)
- [Tecnologías](#-tecnologías-backend)
- [Herramientas](#-herramientas)
- [Arquitectura](#️-arquitectura)
- [Preparación + Ejecución](#-preparación--ejecución-del-backend)
- [Docker](#-docker)
- [Endpoints](#-algunos-endpoints)
- [Testing](#-testing)
- [Relaciones](#-relaciones)
- [Autores](#-backend-by)

## 🔹 Características (MVP)

- Registro de medicamentos con persistencia de datos.
- Listado de medicamentos activos y opción de marcarlos como "tomado".
- Visualización del estado de los medicamentos según la hora del día.
- Creación y gestión de cuentas de usuario.
- Separación de frontend y backend con consumo de API REST.
- Interfaz moderna y responsiva.

#### General:

- Configuración CORS: Configurado para integración con la aplicación frontend

## 🖥 Technologias (Backend)

###

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
- Lombok
- Mockito
- Data initialization (via data.sql)
- Jakarta Validation
- Docker

## 🛠 Herramientas

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=Cloudinary&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

## 🏗 ️️Arquitectura

Se ha construído el backend con arquitectura de 3 capas dividas en paquetes por features, así manteniendo separación de a 3-layer architecture, lo que promueve la separación de responsabilidades y facilidad de mantenimiento:

- Controllers (Capa de presentación): Gestionan las solicitudes HTTP entrantes, las dirigen a los servicios apropiados y devuelven respuestas HTTP. Utilizan DTOs (Objetos de Transferencia de Datos) para el intercambio de información.
- Services (Capa de Lógica de Negocio): Contienen la lógica de negocio central, orquestando las operaciones y aplicando las reglas de validación.
- Repositories (Capa de Acceso a Datos): Interactúan directamente con la base de datos, realizando operaciones CRUD a través de Spring Data JPA.

## 🛞 Preparación + Ejecución del Backend

### 1. 📦 Clonar el Repositorio

```shell
git clone https://github.com/PillPal-FullStack/PillPal-Back.git
```

### 2. 🔑 Configurar variables de entorno

Create a .env file in the project root with your database credentials:

```shell
DB_URL=jdbc:mysql://localhost:3306/pillpal
DB_USER=your_mysql_user
DB_PASSWORD=your_mysql_password
```

Nota:
El backend usa java-dotenv para cargar estos variables.

### 3. 🧱 Construir el proyecto

```shell
mvn clean install
```

### 4. 🚀 Ejecutar la aplicación

```shell
mvn spring-boot:run
```

## 🐋 Docker

### Opción 1: Usar imagen pre-construida

```bash
# Descargar la imagen más reciente
docker pull tizzifona/pillpal:latest

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/pillpal \
  -e DB_USER=your_mysql_user \
  -e DB_PASSWORD=your_mysql_password \
  tizzifona/pillpal:latest
```

### Opción 2: Construir desde código fuente

```bash
# Construir la imagen
docker build -t pillpal-backend .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/pillpal \
  -e DB_USER=your_mysql_user \
  -e DB_PASSWORD=your_mysql_password \
  pillpal-backend
```

### Notas importantes:

- Asegúrate de que MySQL esté ejecutándose en tu máquina local
- Usa `host.docker.internal` para conectar desde el contenedor a MySQL en tu máquina local
- La aplicación estará disponible en `http://localhost:8080`
- Swagger UI estará disponible en `http://localhost:8080/swagger-ui.html`

## ➡️ Algunos Endpoints

### Medications

- GET `/api/medications` → Muestra una lista de todos los medicamentos

```json
{
  "id": 1,
  "name": "paracetamol",
  "imgUrl": "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583230/paracetamol-8-img.jpg",
  "dosage": "500g cada mañana",
  "active": true,
  "startDate": "2025-09-18",
  "endDate": "2025-09-25",
  "lifetime": false,
  "userId": 1
}
```

### Medication Management

- PUT `/api/medications/{id} → Editar un medicamento

```json
 {
        "name": paracetamol,
        "description": "analgésico",
        "imageUrl": "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583230/paracetamol-8-img.jpg",
        "dosage": "1000g cada mañana",
        "active": true,
        "username": "May"startDate": "2025-09-18",
        "endDate": "2025-09-30",
        "lifetime": false
    }
```

## 🧪 Testing

Este proyecto incluye tests unitarios y tests de integración.

- Tests Unitarios: Se centran en componentes individuales (ejemplo, métodos de servicio)
- Tests de Integración: Usan MockMvc para simular peticiones HTTP y comprobar comportamiento de controller.

## 🗄️ Esquema de Base de Datos

La base de datos de PillPal está diseñada con las siguientes entidades principales:

### Entidades

#### 👤 **Users** (usuarios)

- `id` (PK) - Identificador único
- `username` - Nombre de usuario (único)
- `email` - Correo electrónico (único)
- `password` - Contraseña encriptada
- `role` - Rol del usuario (ADMIN/USER)

#### 💊 **Medications** (medicamentos)

- `id` (PK) - Identificador único
- `name` - Nombre del medicamento
- `description` - Descripción del medicamento
- `img_url` - URL de la imagen del medicamento
- `dosage` - Dosificación
- `active` - Estado activo/inactivo
- `start_date` - Fecha de inicio del tratamiento
- `end_date` - Fecha de fin del tratamiento
- `lifetime` - Indica si es medicamento de por vida
- `user_id` (FK) - Referencia al usuario propietario

#### 📅 **Medication_Intakes** (registros de toma)

- `id` (PK) - Identificador único
- `date_time` - Fecha y hora de la toma
- `status` - Estado de la toma (TAKEN/SKIPPED/PENDING)
- `medication_id` (FK) - Referencia al medicamento

#### ⏰ **Reminders** (recordatorios)

- `id` (PK) - Identificador único
- `time` - Hora del recordatorio
- `frequency` - Frecuencia (DAILY/WEEKLY)
- `enabled` - Estado habilitado/deshabilitado
- `medication_id` (FK) - Referencia al medicamento

### Relaciones

[![temp-Image3g-CV9-Z.avif](https://i.postimg.cc/fbW8B170/temp-Image3g-CV9-Z.avif)](https://postimg.cc/5XG5t7Rx)

- Un **Usuario** puede tener múltiples **Medicamentos**
- Un **Medicamento** puede tener múltiples **Registros de Toma**
- Un **Medicamento** puede tener múltiples **Recordatorios**

### Enums

- **Status**: `TAKEN`, `SKIPPED`, `PENDING`
- **Frequency**: `DAILY`, `WEEKLY`

#### ---------------------------

### 🏖 Backend by:

#### Nadiia Alaieva (https://github.com/tizzifona)

#### Mary Kenny (https://github.com/marykenny123)
