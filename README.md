# SanaEscenciaApp

Aplicación móvil nativa para Android desarrollada para el **Centro Integrativo Sanaesencia**, enfocada en la centralización de servicios terapéuticos, bienestar integrativo, agendamiento de citas presenciales u online y trazabilidad de pacientes.

---

## Características Principales

- **Sistema de Roles y Navegación Dinámica:**
  - **Paciente:** Exploración de catálogo de terapias, catálogo de productos naturales de la marca, agendamiento de horas, centro de notificaciones push y perfil personal.
  - **Profesional / Especialista:** Consulta de turnos del día, contexto y antecedentes de pacientes asignados y gestión de agenda.
  - **Administrador:** Planificación y monitoreo de ocupación de los **8 boxes de atención presencial**, administración del equipo de profesionales, control de inventario de productos y moderación de información del centro.

- **Integración de Recursos Nativos del Dispositivo (IE 2.4.1):**
  - **Geolocalización GPS (`LocationServices`):** Cálculo de proximidad en tiempo real a la sede presencial en Los Vilos (Caupolicán 220). Orienta al usuario en la toma de decisiones sugiriendo atención presencial (< 20 km) u online (≥ 20 km) tanto en la pantalla de *Nosotros* como durante el *Agendamiento*, además de ofrecer navegación guiada mediante Google Maps.
  - **Notificaciones Push y Alertas Locales (`NotificationManager`):** Canales independientes para confirmación instantánea de citas, recordatorios de seguimiento médico para evitar deserción y difusión de promociones con códigos de descuento. Incluye centro de notificaciones in-app y gestión de permisos en tiempo de ejecución para Android 13+ (`POST_NOTIFICATIONS`).

- **Formularios Validados e Íconos Estandarizados (IE 2.1.2):**
  - Formularios completos de autenticación (Login), registro de pacientes (Sign Up), recuperación de contraseñas y alta de profesionales.
  - Cada campo incluye íconos descriptivos (`leadingIcon`), validación visual inmediata ante errores y mensajes de apoyo claros.

- **Arquitectura MVVM y Validación Desacoplada (IE 2.2.1):**
  - Validación lógica centralizada en los ViewModels (`UserViewModel`, `BookingViewModel`) mediante estados inmutables (`LoginFormState`, `SignUpFormState`, `BookingFormState`), separando por completo la lógica de negocio de las vistas Jetpack Compose.
  - Validador algorítmico de RUN chileno (Módulo 11), expresiones regulares para correo electrónico, teléfonos y seguridad de contraseñas.

- **Animaciones y Fluidez Visual (IE 2.2.2):**
  - Transiciones animadas entre pantallas principales con efectos de deslizamiento y desvanecimiento (`slideIntoContainer`, `slideOutOfContainer`, `fadeIn`, `fadeOut`).
  - Transiciones suaves (`fadeIn` / `fadeOut`) al alternar entre pestañas del Bottom Navigation.
  - Microinteracciones dinámicas en tarjetas de citas (`animateColorAsState`, `animateContentSize`) y despliegue animado de sugerencias y confirmaciones (`AnimatedVisibility`).

- **Persistencia Local con Room Database (IE 2.3.1):**
  - Almacenamiento local mediante SQLite/Room con soporte reactivo a través de `Flow` y `StateFlow`.
  - Entidades para usuarios, citas médicas, productos naturales de bienestar y reseñas, con precarga de datos iniciales (Seed Data) y asignación automática de boxes (1 al 8) para prevenir colisiones de horario.

---

## Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **Interfaz de Usuario (UI):** Jetpack Compose con componentes Material Design 3.
- **Patrón Arquitectónico:** MVVM (Model-View-ViewModel) + Repository Pattern.
- **Navegación:** Navigation Compose con rutas estructuradas y animaciones de transición.
- **Base de Datos Local:** Room Database (SQLite) + TypeConverters.
- **Servicios de Ubicación:** Google Play Services Location (`FusedLocationProviderClient`).
- **Concurrencia:** Kotlin Coroutines y Kotlin Flow / StateFlow.
- **Configuración de Construcción:** Gradle con Kotlin DSL (`build.gradle.kts`).

---



### Requisitos del Sistema
- **Android Studio:** Ladybug / Iguana o superior.
- **JDK:** Java 17 o superior.
- **Android SDK:** `minSdk = 26` (Android 8.0) / `targetSdk = 34` (Android 14) / `compileSdk = 35`.



---

## Credenciales de Prueba (Datos Semilla)

- **Administrador:**
  - **RUN / Usuario:** `admin`
  - **Contraseña:** `admin`
- **Profesional de Muestra:**
  - **RUN / Usuario:** `20000001-3`
  - **Contraseña:** `1234`
- **Registro de Paciente:**
  - **RUN / Usuario:** `20000100-1`
  - **Contraseña:** `1234`
