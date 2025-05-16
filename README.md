# Prometeo-Gym-Module: Módulo de Seguimiento Físico, Rutinas y Reservas del Gimnasio 🏋️‍♂️

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Test Coverage](https://img.shields.io/badge/coverage-85%25-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Cloud-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![License](https://img.shields.io/badge/license-MIT-blue)

Este módulo forma parte del sistema Prometeo, que permite a los usuarios registrar su progreso físico, planificar y reservar sesiones de entrenamiento, generar reportes y recibir recomendaciones automatizadas, todo integrado con el sistema institucional de Bienestar.

## Tabla de Contenidos 📋

- [Integrantes](#integrantes)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura)
- [Sprints y Desarrollo](#sprints-y-desarrollo)
    - [Sprint 1: Diseño e implementación básica](#sprint-1-diseño-e-implementación-básica)
    - [Sprint 2: Pruebas y CI/CD](#sprint-2-pruebas-y-cicd)
    - [Sprint 3: Integración y despliegue](#sprint-3-integración-y-despliegue)
- [Patrones de Diseño](#patrones-de-diseño)
- [Dependencias del Proyecto](#dependencias-del-proyecto)
- [Configuración del Proyecto](#configuración-del-proyecto)
- [Documentación API (Swagger)](#documentación-api-swagger)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Contribuciones](#contribuciones)

## Integrantes

- Andersson David Sánchez Méndez
- Cristian Santiago Pedraza Rodríguez
- Ricardo Andres Ayala Garzon
- Santiago Botero García
- Juan Andrés Rodríguez Peñuela

## Descripción

Módulo de Seguimiento Físico, Rutinas y Reservas del Gimnasio para la plataforma institucional Prometeo.  
Permite a usuarios registrar y consultar su progreso físico, gestionar rutinas, reservar sesiones de entrenamiento y recibir recomendaciones personalizadas.  
Los entrenadores pueden monitorear el progreso, asignar rutinas, gestionar cupos y horarios, y generar reportes detallados.

---
## Tecnologías Utilizadas 🛠️

- **Java OpenJDK 17**: Lenguaje principal
- **Spring Boot 3.4.4**: Framework para el desarrollo backend
- **Spring Data MongoDB**: Integración con MongoDB
- **MongoDB Atlas**: Base de datos NoSQL en la nube
- **JUnit 5 & Mockito**: Pruebas unitarias y mock
- **Maven**: Gestión de dependencias y construcción
- **GitHub Actions**: Pipeline CI/CD
- **Swagger/OpenAPI**: Documentación REST
- **Lombok**: Reducción de código boilerplate
- **Docker**: Contenedorización (opcional)

## Arquitectura

El proyecto sigue una arquitectura en capas:

- **Controller:** Endpoints REST para comunicación con el frontend.
- **Service:** Lógica de negocio.
- **Repository:** Acceso a datos con Spring Data MongoDB.
- **Model:** Entidades y DTOs.
- **Config:** Configuraciones de seguridad, CORS, base de datos y OpenAPI.
- **Excepciones:** Manejo centralizado de errores personalizados.

---

## Funcionalidades

- Registro y consulta de progreso físico (peso, medidas, metas).
- Creación, asignación y seguimiento de rutinas personalizadas.
- Reserva y gestión de sesiones en gimnasio con control de cupos.
- Recomendaciones automatizadas basadas en historial y metas.
- Reportes de uso, asistencia y evolución física.
- Integración con sistema institucional de Bienestar.

---

## Requisitos Funcionales

1. Registro detallado del progreso físico con datos personales y métricos.
2. Consulta del historial con gráficos y tablas comparativas.
3. Creación y modificación de rutinas por entrenadores.
4. Reserva de sesiones con control de cupos y notificaciones.
5. Gestión de horarios y cupos en tiempo real.
6. Sugerencias automáticas de rutinas validadas por entrenadores.
7. Generación de reportes exportables.
8. Integración con servicios generales de Bienestar.

---

## Estructura del Proyecto

```
prometeo-back/
├── src/main/java/edu/eci/cvds/prometeo/
│ ├── config/ # Configuraciones (CORS, DB, Seguridad, OpenAPI)
│ ├── controller/ # Controladores REST
│ ├── dto/ # Data Transfer Objects
│ ├── huggingface/ # Integración con Huggingface (si aplica)
│ ├── model/ # Modelos y entidades
│ ├── openai/ # Integración con OpenAI (si aplica)
│ ├── repository/ # Repositorios MongoDB
│ ├── service/ # Servicios (lógica de negocio)
│ ├── PrometeoApplication.java # Aplicación principal
│ └── PrometeoExceptions.java # Clases de excepciones personalizadas
├── src/test/java/edu/eci/cvds/prometeo/ # Pruebas unitarias y de integración
├── src/main/resources/ # Recursos y configuración
├── .github/ # Workflows de GitHub Actions
├── compose.yaml # Configuración para despliegue Docker Compose
├── pom.xml # Archivo de dependencias Maven
├── README.md # Este archivo
```

---

## Uso

### Requisitos previos

- Java 17 instalado
- MongoDB en ejecución o conexión remota
- Maven para construir el proyecto

### Construcción y ejecución

🧰 Uso
Requisitos Previos
Java 17

MongoDB local o Atlas

Maven

```bash
mvn clean install
mvn spring-boot:run
```
## Ejemplos de Endpoints

Registrar progreso físico:
POST /progreso
Cuerpo ejemplo:

json
Copiar
Editar
```
{
"usuarioId": "12345",
"peso": 70,
"medidas": {"cintura": 80, "pecho": 95, "brazos": 30},
"meta": "Perder 5 kg",
"observaciones": "Buen progreso"
}
```
Consultar historial de progreso:
GET /progreso/{usuarioId}

Crear rutina:
POST /rutinas
Cuerpo ejemplo:

json
Copiar
Editar

```
{
"nombre": "Rutina fuerza",
"duracionSemanas": 8,
"ejercicios": [...]
}
```

Reservar sesión:
POST /reservas
Cuerpo ejemplo:

json
Copiar
Editar

```
{
"usuarioId": "12345",
"sesionId": "abcde"
}
```

## Pruebas

Las pruebas unitarias se encuentran en la carpeta src/test/java/edu/eci/cvds/prometeo/.
Para ejecutar las pruebas, usa:

mvn test

## Despliegue y CI/CD

Se utiliza GitHub Actions para automatizar el pipeline de integración y despliegue:

Compilación y pruebas automáticas con Maven.

Construcción de imagen Docker (si aplica).

Despliegue en servidor Azure configurado.

Archivo de configuración: .github/workflows/maven.yml
Despliegue con Docker Compo se usando compose.yaml.

## 📦 Dependencias
Declaradas en pom.xml con Maven. Incluye:

Spring Boot Web + Data MongoDB

Lombok

Swagger/OpenAPI

JUnit 5

Mockito

Spring Boot Starter Test

## 🧠 Patrones de Diseño
MVC (Model-View-Controller)

DTO para transferencia eficiente de datos

Repository Pattern para abstracción de la base de datos

Service Layer para la lógica de negocio

Builder Pattern (si aplica en entidades compuestas)

## 📄 Documentación API (Swagger)

La documentación de la API se genera automáticamente con Swagger/OpenAPI.