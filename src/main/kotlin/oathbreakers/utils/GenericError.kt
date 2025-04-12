package oathbreakers.utils

import kotlinx.serialization.Serializable

@Serializable
data class GenericError(val error: String)