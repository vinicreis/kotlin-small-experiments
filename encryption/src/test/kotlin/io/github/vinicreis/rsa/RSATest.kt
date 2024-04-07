package io.github.vinicreis.rsa

import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.toKotlin
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import java.math.BigInteger as JavaBigInteger

object RSA {
    private const val KEY_LENGTH = 128
    private val random = Random(0x93ED)

    sealed class Key {
        abstract val n: BigInteger

        data class Private(val d: BigInteger, override val n: BigInteger) : Key()
        data class Public(val e: BigInteger, override val n: BigInteger) : Key()
    }

    fun generateKeys(n: String, d: String, e: Int): Pair<Key.Private, Key.Public> =
        Pair(
            first = Key.Private(d.toBigInteger(16).toKotlin(), n.toBigInteger(16).toKotlin()),
            second = Key.Public(e.toBigInteger().toKotlin(), n.toBigInteger(16).toKotlin())
        )

    fun generateKeys(): Pair<Key.Private, Key.Public> {
        val p = BigInteger.probablePrime(KEY_LENGTH, random)
        val q = BigInteger.probablePrime(KEY_LENGTH, random)
        val n = p * q
        val phi = p.minus(1) * q.minus(1)
        val e = JavaBigInteger(random.nextBytes(6)).toKotlin()
        val d = e.modPow(BigInteger.NEGATIVE_ONE, phi)

        return Pair(Key.Private(d, n), Key.Public(e, n))
    }

    fun encrypt(message: String, key: Key.Private): ByteArray =
        JavaBigInteger(message.toByteArray()).toKotlin().modPow(key.d, key.n).toByteArray()

    fun decrypted(bytes: ByteArray, key: Key.Public): String =
        String(JavaBigInteger(bytes).toKotlin().modPow(key.e, key.n).toByteArray())
}

@OptIn(ExperimentalStdlibApi::class)
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

        println("Open message: $message")
        println("Open message bytes:" + message.toByteArray().toHexString())
        println("Encrypted:" + encrypted.toHexString())
        println("Decrypted bytes:" + decrypted.toByteArray().toHexString())
        println("Decrypted: $decrypted")

        assertEquals(message, decrypted)
    }
}