# SanaEscenciaApp



Aplicación móvil nativa para Android diseñada para la gestión de citas y servicios del Centro Integral Sana Esencia. Esta app permite a los pacientes explorar servicios, agendar citas de forma interactiva y gestionar su perfil, mientras ofrece herramientas de administración para los profesionales del centro.

Características Principales

Sistema de Roles: Acceso y navegación dinámica diferenciada entre Pacientes y Administradores.

Catálogo de Servicios: Exploración detallada de terapias y especialidades (CatalogScreen).

Agendamiento Interactivo: Flujo de reservas (BookingScreen) con selección de fechas (DatePicker) y bloques horarios disponibles.

Gestión Administrativa: Panel dedicado para profesionales (AdminProfessionalsScreen) para la gestión de atenciones.

Autenticación y Registro: Pantallas de inicio de sesión (LoginScreen) y creación de nuevas cuentas (SignUpScreen).

Actualizaciones en Tiempo Real: Uso de StateFlow para reflejar inmediatamente los cambios de citas en el Dashboard y el Perfil del usuario.

Modo Offline/Persistencia Local: Almacenamiento seguro de citas médicas en el dispositivo mediante Room Database.

Tecnologías y Arquitectura

El proyecto está construido utilizando las últimas herramientas recomendadas para el desarrollo moderno en Android:

Lenguaje: Kotlin

Interfaz de Usuario (UI): Jetpack Compose con Material Design 3.

Arquitectura: MVVM (Model-View-ViewModel) para una clara separación de responsabilidades.

Navegación: Navigation Compose para transiciones fluidas entre pantallas.

Base de Datos: Room Database (SQLite) para persistencia local de datos (AppointmentEntity, AppointmentDAO).

Programación Asíncrona: Corrutinas (Coroutines) y StateFlow.

Configuración de Construcción: Gradle con Kotlin DSL (build.gradle.kts).

Requisitos del Sistema

Para compilar y ejecutar este proyecto, necesitas:

Android Studio: Versión Iguana o superior (recomendado).

JDK: Java 17 o superior.

SDK de Android: Nivel de API mínimo 24 (Android 7.0) / Nivel de API objetivo 34 (Android 14).

Diseño y UI

La aplicación incorpora una paleta de colores personalizada y tipografías adaptadas a la identidad de marca de Sana Esencia, garantizando una experiencia de usuario relajante y profesional. Soporta adaptación automática para dispositivos con configuraciones LTR (Left-to-Right) y RTL mediante iconos "AutoMirrored".


