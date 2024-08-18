package io.github.skydynamic.maiprober.util.prober.interfact

interface ProberUtil {
    suspend fun validateProberAccount(username: String, password: String) : Boolean
    suspend fun uploadMaimaiProberData(username: String, password: String, authUrl: String)
    suspend fun updaloadChunithmProberData(username: String, password: String, authUrl: String)
}