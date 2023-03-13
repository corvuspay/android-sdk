package hr.corvuspay.demoshop.helpers

import android.util.Log
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {

    fun generateHashWithHmac256(content: String, key: String): String {
        return try {
            val bytes = hmac(key.toByteArray(), content.toByteArray())
            val messageDigest = bytesToHex(bytes)
            Log.v("ENCRYPTED STRING", "message digest: $messageDigest")
            messageDigest
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun hmac(key: ByteArray, content: ByteArray): ByteArray {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        mac.init(SecretKeySpec(key, algorithm))
        return mac.doFinal(content)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        var j = 0
        while (j < bytes.size) {
            val v = bytes[j].toInt().and(0xFF)
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            j++
        }
        return String(hexChars)
    }
}
