package io.github.skydynamic.maiprober.util.prober.interfact

import io.github.skydynamic.maiprober.util.singal.MaiproberSignal

interface ProberUtil {
    val updateStartedSignal: MaiproberSignal<String>
    val updateFinishedSignal: MaiproberSignal<String>

    suspend fun validateProberAccount(username: String, password: String) : Boolean
    suspend fun updateAccountInfo(username: String, password: String)
    suspend fun uploadMaimaiProberData(username: String, password: String, authUrl: String, isCache: Boolean)
    suspend fun uploadChunithmProberData(username: String, password: String, authUrl: String, isCache: Boolean)
}