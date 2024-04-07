package io.github.vinicreis.rsa

import org.gciatto.kt.math.BigInteger
import org.gciatto.kt.math.toKotlin
import kotlin.random.Random

object RSA {
    private const val KEY_LENGTH = 128
    private const val HEXA_RADIX = 16
    private val random = Random(0x93ED)

    sealed class Key {
        abstract val n: BigInteger

        data class Private(val d: BigInteger, override val n: BigInteger) : Key()
        data class Public(val e: BigInteger, override val n: BigInteger) : Key()
    }

    private fun String.fromHexToBigInt(radix: Int = HEXA_RADIX): BigInteger = toBigInteger(radix).toKotlin()

    fun generateKeys(n: String, d: String, e: Int): Pair<Key.Private, Key.Public> =
        Pair(
            first = Key.Private(d.fromHexToBigInt(), n.fromHexToBigInt()),
            second = Key.Public(BigInteger.of(e), n.fromHexToBigInt())
        )

    private fun e(phi: BigInteger): BigInteger {
        var e: BigInteger

        do {
            e = java.math.BigInteger(random.nextBytes(6)).toKotlin()
        } while (phi.gcd(e) != BigInteger.ONE)

        return e
    }

    fun generateKeys(): Pair<Key.Private, Key.Public> {
        val p = BigInteger.probablePrime(KEY_LENGTH, random)
        val q = BigInteger.probablePrime(KEY_LENGTH, random)
        val n = p * q
        val phi = p.minus(1) * q.minus(1)
        val e = e(phi)
        val d = e.modPow(BigInteger.NEGATIVE_ONE, phi)

        return Pair(Key.Private(d, n), Key.Public(e, n))
    }

    fun encrypt(message: String, key: Key.Private): ByteArray =
        java.math.BigInteger(message.toByteArray()).toKotlin().modPow(key.d, key.n).toByteArray()

    fun decrypted(bytes: ByteArray, key: Key.Public): String =
        String(java.math.BigInteger(bytes).toKotlin().modPow(key.e, key.n).toByteArray())
}
