package oathbreakers.utils

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Constants
import de.mkammerer.argon2.Argon2Factory

val argon2 = Argon2Factory.create(
    Argon2Factory.Argon2Types.ARGON2id,
    Argon2Constants.DEFAULT_SALT_LENGTH,
    Argon2Constants.DEFAULT_HASH_LENGTH
)

fun Argon2.hash(password: String): String = hash(6, 8192, 1, password.toCharArray())