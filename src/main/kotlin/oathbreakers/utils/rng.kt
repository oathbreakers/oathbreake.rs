package oathbreakers.utils

import java.security.SecureRandom

private val rng = SecureRandom()
private const val CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun randomString(length: Int): String {
    val array = CharArray(length)
    for (i in 0 until length) {
        array[i] = CHARSET[rng.nextInt(CHARSET.length)]
    }
    return array.concatToString()
}
