package io.github.skydynamic.maiprober.util.prober.interfact

import io.github.skydynamic.maiprober.util.score.MaimaiMusicDetailList
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal

interface ProberUtil {
    val updateStartedSignal: MaiproberSignal<String>
    val updateFinishedSignal: MaiproberSignal<String>

    suspend fun validateProberAccount(username: String, password: String) : Boolean = false
    suspend fun updateAccountInfo(username: String, password: String) { }
    suspend fun updateAccountInfo(importToken: String) { }
    suspend fun uploadMaimaiProberData(username: String, password: String, authUrl: String, isCache: Boolean) { }
    suspend fun uploadMaimaiProberData(importToken: String, authUrl: String, isCache: Boolean) { }
    suspend fun uploadChunithmProberData(username: String, password: String, authUrl: String, isCache: Boolean) { }
    suspend fun uploadChunithmProberData(importToken: String, authUrl: String, isCache: Boolean) { }
    suspend fun getMaimaiSongScoreData(username: String, password: String, isCache: Boolean) : MaimaiMusicDetailList =
        MaimaiMusicDetailList(-1)
    suspend fun getMaimaiSongScoreData(importToken: String, isCache: Boolean) : MaimaiMusicDetailList =
        MaimaiMusicDetailList(-1)
}