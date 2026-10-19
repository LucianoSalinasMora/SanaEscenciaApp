package com.sanaesencia.app.viewmodel

/**
 * Estado reactivo del formulario de inicio de sesión (Login).
 * Desacopla la lógica de validación de la interfaz de usuario (Compose).
 */
data class LoginFormState(
    val run: String = "",
    val isRunValid: Boolean = true,
    val password: String = "",
    val isPasswordValid: Boolean = true,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

/**
 * Estado reactivo del formulario de registro de nuevos pacientes (Sign Up).
 * Centraliza validaciones de RUT/RUN, correo, teléfono y contraseñas.
 */
data class SignUpFormState(
    val run: String = "",
    val isRunValid: Boolean = true,
    val name: String = "",
    val isNameValid: Boolean = true,
    val email: String = "",
    val isEmailValid: Boolean = true,
    val phone: String = "",
    val isPhoneValid: Boolean = true,
    val password: String = "",
    val isPasswordValid: Boolean = true,
    val securityQuestion: String = "¿Nombre de tu primera mascota?",
    val securityAnswer: String = "",
    val isSecurityAnswerValid: Boolean = true,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false
)
