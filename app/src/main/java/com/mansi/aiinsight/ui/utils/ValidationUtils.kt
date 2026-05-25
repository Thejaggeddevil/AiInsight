package com.mansi.aiinsight.ui.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10}$"))
    }

    fun isValidOtp(otp: String): Boolean {
        return otp.length == 6 && otp.matches(Regex("^[0-9]{6}$"))
    }

    fun validateRegistration(fullName: String, email: String, password: String): String? {
        return when {
            fullName.isBlank() -> "Full name is required"
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password is required"
            !isValidPassword(password) -> "Password must be at least 6 characters"
            else -> null
        }
    }

    fun validateLogin(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password is required"
            else -> null
        }
    }
}