package io.github.skydynamic.maiprober.util.prober.interfact

interface IProberUtil {
    suspend fun verifyProberAccount(username: String, password: String) : Boolean
    suspend fun uploadMaimaiProberData(username: String, password: String, authUrl: String, data: String) : String
}