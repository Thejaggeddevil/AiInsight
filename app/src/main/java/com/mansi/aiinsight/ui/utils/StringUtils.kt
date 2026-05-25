package com.mansi.aiinsight.ui.utils

import java.util.Locale
import java.util.Locale.getDefault

object StringUtils {
    fun normalizeCertificateName(value: String): String {
        val cleaned = value
            .replace(Regex("^PM-"), "")
            .replace(Regex("-[A-F0-9]{6,8}$"), "")
            .replace(Regex("\\s+Certificate$"), "")
            .trim()

        if (cleaned.isEmpty()) return "Certificate"

        return if (cleaned.lowercase(getDefault()).startsWith("advanced-")) {
            val baseName = cleaned
                .replace(Regex("^advanced-"), "")
                .split("-")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
            "Advanced $baseName"
        } else if (Regex("^[a-z0-9]+(?:-[a-z0-9]+)+$", RegexOption.IGNORE_CASE).matches(cleaned)) {
            cleaned
                .split("-")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        } else {
            cleaned
        }
    }

    fun truncate(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength) + "..."
        } else {
            text
        }
    }
}