package io.github.skydynamic.maiprober.util.prober.interfact

interface ProberUtil {
    suspend fun validateProberAccount(username: String, password: String) : Boolean
    suspend fun uploadMaimaiProberData(authUrl: String, data: String) : String
}