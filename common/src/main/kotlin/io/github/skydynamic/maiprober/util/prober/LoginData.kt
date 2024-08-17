package io.github.skydynamic.maiprober.util.prober

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val username:String,
    val password:String
)