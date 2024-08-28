package io.github.skydynamic.maiprober.util.score

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.exists

val MAIMAI_CACHE_PATH = Path("./data/cache/maimai_cache.json")

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@Serializable
data class MaimaiMusicDetail(
    val name: String, val level: String,
    val score: String, val dxScore: String,
    val clearType: MaimaiClearType, val syncType: MaimaiSyncType,
    val clearSpecialClearType: MaimaiSpecialClearType
)

@Serializable
data class MaimaiMusicDetailList(
    val timestamp: Long,
    var basic: List<MaimaiMusicDetail> = listOf(), var advanced: List<MaimaiMusicDetail> = listOf(),
    var expert: List<MaimaiMusicDetail> = listOf(), var master: List<MaimaiMusicDetail> = listOf(),
    var remaster: List<MaimaiMusicDetail> = listOf()
) {
    fun setMusicDetailByDifficulty(difficulty: MaimaiDifficulty, musicList: List<MaimaiMusicDetail>) {
        when (difficulty) {
            MaimaiDifficulty.BASIC -> basic = musicList
            MaimaiDifficulty.ADVANCED -> advanced = musicList
            MaimaiDifficulty.EXPERT -> expert = musicList
            MaimaiDifficulty.MASTER -> master = musicList
            MaimaiDifficulty.REMASTER -> remaster = musicList
        }
    }

    fun save() {
        if (!MAIMAI_CACHE_PATH.parent.exists()) {
            MAIMAI_CACHE_PATH.parent.toFile().mkdirs()
        }
        MAIMAI_CACHE_PATH.toFile().writeText(JSON.encodeToString(serializer(), this))
    }
}

enum class MaimaiDifficulty(val diffName: String) {
    BASIC("Basic"),
    ADVANCED("Advanced"),
    EXPERT("Expert"),
    MASTER("Master"),
    REMASTER("Re:Master");
}

enum class MaimaiClearType(val clearName: String) {
    CLEAR("clear"),
    S("s"),
    SS("ss"),
    SSP("ssp"),
    SSS("sss"),
    SSSP("sssp");
}

enum class MaimaiSpecialClearType(val sepcialClearName: String) {
    COMMON("back"),
    FC("fc"),
    FCP("fcp"),
    AP("ap"),
    APP("app");
}

enum class MaimaiSyncType(val syncName: String) {
    SYNC("sync"),
    FS("fs"),
    FSP("fsp"),
    FDX("fdx"),
    FDXP("fdxp");
}