package com.example.appthuongmaidientu

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val AES_ALGORITHM = "AES"

    @Throws(Exception::class)
    fun encrypt(plaintext: String, password: String): String {
        val secretKey: SecretKey? = generateKey(password)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes =
            cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decrypt(ciphertext: String?, password: String): String {
        val secretKey: SecretKey? = generateKey(password)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes =
            Base64.decode(ciphertext, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    private fun generateKey(passphrase: String): SecretKeySpec? {
        return try {
            // Sử dụng một giá trị muối không trống để tạo khóa
            val salt = "your_non_empty_salt".toByteArray(charset("UTF-8"))

            // Số lần lặp và độ dài khóa có thể được điều chỉnh dựa trên yêu cầu bảo mật của bạn
            val iterationCount = 65536 // Điều chỉnh nếu cần
            val keyLength = 256 // Điều chỉnh nếu cần
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val keySpec = PBEKeySpec(passphrase.toCharArray(), salt, iterationCount, keyLength)
            val secretKey = factory.generateSecret(keySpec)
            SecretKeySpec(secretKey.encoded, AES_ALGORITHM)
        } catch (e: Exception) {
            e.printStackTrace()
            // Xử lý ngoại lệ một cách thích hợp (ví dụ: ghi log hoặc ném một ngoại lệ tùy chỉnh)
            null
        }
    }
}