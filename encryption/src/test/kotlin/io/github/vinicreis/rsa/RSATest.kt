package io.github.vinicreis.rsa

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RSATest {
    @Test
    fun `Should encrypt and decrypt correctly`() {
        val (privateKey, publicKey) = RSA.generateKeys(
            n = "DCBFFE3E51F62E09CE7032E2677A78946A849DC4CDDE3A4D0CB81629242FB1A5",
            d = "74D806F9F3A62BAE331FFE3F0A68AFE35B3D2E4794148AACBC26AA381CD7D30D",
            e = 65537
        )
        val message = "A grin without a cat!"
        val encrypted = RSA.encrypt(message, privateKey)
        val decrypted = RSA.decrypted(encrypted, publicKey)

        assertEquals(message, decrypted)
    }

    @Test
    fun `Should generate working keys`() {
        val (privateKey, publicKey) = RSA.generateKeys()
        val message = "This is test message!"
        val encrypted = RSA.encrypt(message, privateKey)
        val decrypted = RSA.decrypted(encrypted, publicKey)

        assertEquals(message, decrypted)
    }
}