package com.example.mealmate.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Utility for handling password hashing and verification.
 *
 * Note: In a production app, you should use a proper library like BCrypt.
 * This is a simplified implementation for demonstration purposes.
 */
object SecurityUtil {
    private const val SALT_LENGTH = 16
    private const val ALGORITHM = "SHA-256"

    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hashWithSalt(password, salt)
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash)
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split(":")
        if (parts.size != 2) return false

        val salt = Base64.getDecoder().decode(parts[0])
        val expectedHash = Base64.getDecoder().decode(parts[1])

        val actualHash = hashWithSalt(password, salt)

        return MessageDigest.isEqual(expectedHash, actualHash)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    private fun hashWithSalt(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance(ALGORITHM)
        md.reset()
        md.update(salt)
        return md.digest(password.toByteArray())
    }
}