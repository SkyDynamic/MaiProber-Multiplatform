package io.github.skydynamic.maiprober.util.prober.lxns

import io.github.skydynamic.maiprober.util.prober.client
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import io.github.skydynamic.maiprober.util.score.*
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.floor

class LxnsProberUtil : ProberUtil {
    private val baseApiUrl = "https://maimai.lxns.net"

    @Serializable
    data class LxnsResponse(
        val success: Boolean, val code: Int, val message: String = "", val data: List<MaimaiRecordData> = listOf()
    )

    @Serializable
    data class MaimaiRecordData(
        val id: Int, @SerialName("song_name") val songName: String, val level: String,
        @SerialName("level_index") val levelIndex: Int, val achievements: Float, val fc: String?,
        val fs: String?, @SerialName("dx_score") val dxScore: Int, @SerialName("dx_rating") val dxRating: Float,
        val rate: String, val type: String, @SerialName("play_time") val playTime: String = "",
        @SerialName("upload_time") val uploadTime: String = ""
    )

    private val updateStartedSignalBuilder = MaiproberSignal<String>()
    private val updateFinishedSignalBuilder = MaiproberSignal<String>()

    override val updateStartedSignal
        get() = updateStartedSignalBuilder
    override val updateFinishedSignal
        get() = updateFinishedSignalBuilder

    private fun processMaimaiSongData(music: MaimaiRecordData): MaimaiMusicDetail {
        val songInfo = MAIMAI_SONG_INFO.find { it.title == music.songName }
        val version = songInfo?.version ?: -1
        var notes = 0
        var level = 0f
        if (music.type == "dx") {
            notes = songInfo!!.dxNotes[music.levelIndex]
            level = songInfo.dxLevel[music.levelIndex]
        } else if (music.type == "standard") {
            notes = songInfo!!.standardNotes[music.levelIndex]
            level = songInfo.standardLevel[music.levelIndex]
        }
        return MaimaiMusicDetail(
            music.songName, level, "${music.achievements}%",
            "${music.dxScore} / ${notes * 3}",
            floor(music.dxRating).toInt(), version,
            MaimaiMusicKind.getMusicKind(music.type),
            MaimaiDifficulty.getDifficultyWithIndex(music.levelIndex),
            MaimaiClearType.getClearTypeByScore(music.achievements),
            MaimaiSyncType.getSyncType(music.fs ?: ""),
            MaimaiSpecialClearType.getSpecialClearType(music.fc ?: "")
        )
    }

    // Lxns do not need this method :(
    override suspend fun validateProberAccount(username: String, password: String): Boolean {
        throw RuntimeException("Lxns do not use username and password to login, please use lxns token")
    }

    override suspend fun uploadMaimaiProberData(username: String, password: String,
                                                authUrl: String, isCache: Boolean) {
        throw RuntimeException("Lxns do not use username and password to login, please use lxns token")
    }

    override suspend fun uploadChunithmProberData(username: String, password: String,
                                                  authUrl: String, isCache: Boolean) {
        throw RuntimeException("Lxns do not use username and password to login, please use lxns token")
    }

    override suspend fun getMaimaiSongScoreData(
        username: String, password: String, isCache: Boolean
    ): MaimaiMusicDetailList {
        throw RuntimeException("Lxns do not use username and password to login, please use lxns token")
    }

    override suspend fun getMaimaiSongScoreData(importToken: String, isCache: Boolean): MaimaiMusicDetailList {
        val data: HttpResponse = client.get("$baseApiUrl/api/v0/user/maimai/player/scores") {
            header("X-User-Token", importToken)
        }

        if (data.status.value == 400) {
            throw RuntimeException("Lxns token is invalid")
        }

        val musicDetailList = MaimaiMusicDetailList(System.currentTimeMillis())
        val resp: LxnsResponse = data.body()

        for (music in resp.data) {
            musicDetailList.songs.add(processMaimaiSongData(music))
        }
        if (isCache) musicDetailList.save()
        return musicDetailList
    }
}