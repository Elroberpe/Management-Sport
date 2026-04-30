# Management-Sport
Backend sobre un sistema que gestiona todo sobre un complejo deportivo
=======

Sistema de gestión de reservas de canchas de fútbol, diseñado bajo una arquitectura de **Monolito Modular** y pensado para evolucionar hacia una plataforma SaaS en el futuro.

## Módulos Principales (Monolito Modular)
- **company**: Gestión de Empresas, Sucursales y Canchas.
- **identity**: Gestión de Usuarios, Clientes, Autenticación y Autorización.
- **events**: Gestión de Eventos y Mantenimiento.
- **booking**: Gestión de Reservas (Core del negocio).
- **finance**: Gestión de Pagos y Finanzas.
- **common**: Componentes transversales y utilidades (Shared Kernel).

## Tecnologías
- Java 17
- Spring Boot
- SQL Server

## Base de datos
El script inicial de la base de datos se encuentra en `Docs/BDD/SQL.txt`.
>>>>>>> cd2e094 (Initial commit: Full project setup with CRUD APIs for all modules)
