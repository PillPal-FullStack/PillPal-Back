# PillPal (Backend️)

Bienvenido a la API backend para Pillpal, una app web diseñado para permitir el seguimiento de la toma de medicamentos. Esta API gestiona el funcionamento con base de datos para registar medicamentos, programar recordatorios y llevar un control de la toma diaria según las indicaciones locations and connect with an existing React and Next frontend.

## 🔹 Características (MVP)
* Registro de medicamentos con persistencia de datos.
* Listado de medicamentos activos y opción de marcarlos como "tomado".
* Visualización del estado de los medicamentos según la hora del día.
* Creación y gestión de cuentas de usuario.
* Separación de frontend y backend con consumo de API REST.
* Interfaz moderna y responsiva.

#### General:

- Configuración CORS: Configurado para integración con la aplicación frontend

## 🖥Technologias (Backend)
### 
- Java 21
- Spring Boot
- Maven
- Jakarta Validation
- JWT
- Lombok
- Mockito
- MySQL
- Data initialization (via data.sql)
- Docker


## 🛠Herramientas
- IntelliJ IDEA
- Swagger
- Cloudinary

## 🏗 ️️Arquitectura
Se ha construído el backend con arquitectura de 3 capas dividas en paquetes por features, así manteniendo separación de a 3-layer architecture, lo que promueve la separación de responsabilidades y facilidad de mantenimiento:
- Controllers (Capa de presentación): Gestionan las solicitudes HTTP entrantes, las dirigen a los servicios apropiados y devuelven respuestas HTTP. Utilizan DTOs (Objetos de Transferencia de Datos) para el intercambio de información.
- *Services (Capa de Lógica de Negocio): Contienen la lógica de negocio central, orquestando las operaciones y aplicando las reglas de validación.
- **Repositories (Capa de Acceso a Datos): Interactúan directamente con la base de datos, realizando operaciones CRUD a través de Spring Data JPA.


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
### 5. 🚀 Ejecutar la aplicación
```shell
mvn spring-boot:run
```

### 6. 🐋 Docker
Requirimiento previo: instalar Docker

## ➡️ Algunos Endpoints
### Medications
- GET `/api/medications` → LMuestra una lista de todos los medicamentos
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



#### ---------------------------


### 🏖   Backend by:

#### Nadiia Alaieva (https://github.com/tizzifona)

#### Mary Kenny (https://github.com/marykenny123)
 
