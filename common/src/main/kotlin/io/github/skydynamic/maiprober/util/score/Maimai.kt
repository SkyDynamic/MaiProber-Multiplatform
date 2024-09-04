package io.github.skydynamic.maiprober.util.score

import io.github.skydynamic.maiprober.util.ResourceManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

val MAIMAI_CACHE_PATH = Path("./data/cache/maimai_cache.json")

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

val MAIMAI_SONG_INFO: List<MaimaiSongInfo> = MaimaiInterfaceResourcePath.getMaimaiSongInfoFile()?.let {
    Json.decodeFromString(
        it.readText()
    )
} ?: listOf()

val MAIMAI_ICON_LIST: List<Int> = MaimaiInterfaceResourcePath.getMaimaiIconListFile()?.let {
    Json.decodeFromString(
        it.readText()
    )
} ?: listOf()

val MAIMAI_PLATE_LIST: List<Int> = MaimaiInterfaceResourcePath.getMaimaiPlateListFile()?.let {
    Json.decodeFromString(
        it.readText()
    )
} ?: listOf()

object MaimaiInterfaceResourcePath {
    @JvmStatic
    fun getMaimaiSongInfoFile() : URL? {
        return this::class.java.classLoader.getResource("maimai/song_info.json")
    }

    @JvmStatic
    fun getMaimaiIconListFile() : URL? {
        return this::class.java.classLoader.getResource("maimai/icon_list.json")
    }

    @JvmStatic
    fun getMaimaiPlateListFile() : URL? {
        return this::class.java.classLoader.getResource("maimai/plate_list.json")
    }
}

@Serializable
data class MaimaiSongInfo(
    val id: Int, val title: String, val version: Int,
    @SerialName("dx_difficulties_notes") val dxNotes: List<Int>,
    @SerialName("standard_difficulties_notes") val standardNotes: List<Int>,
    @SerialName("dx_levels") val dxLevel: List<Float>,
    @SerialName("standard_levels") val standardLevel: List<Float>
)

@Serializable
data class MaimaiMusicDetail(
    val name: String, val level: Float,
    val score: String, val dxScore: String,
    val rating: Int, val version: Int,
    val uepInfo: MaimaiMusicKind, val diff: MaimaiDifficulty,
    val clearType: MaimaiClearType, val syncType: MaimaiSyncType,
    val specialClearType: MaimaiSpecialClearType
)

@Serializable
data class MaimaiMusicDetailList(
    val timestamp: Long,
    var songs: MutableList<MaimaiMusicDetail> = mutableListOf()
) {
    fun save() {
        if (!MAIMAI_CACHE_PATH.parent.exists()) {
            MAIMAI_CACHE_PATH.parent.toFile().mkdirs()
        }
        MAIMAI_CACHE_PATH.toFile().writeText(JSON.encodeToString(serializer(), this))
    }

    companion object {
        @JvmStatic
        fun getCache(): MaimaiMusicDetailList {
            if (MAIMAI_CACHE_PATH.exists()) {
                val cache = JSON.decodeFromString<MaimaiMusicDetailList>(MAIMAI_CACHE_PATH.toFile().readText())
                return cache
            }
            return MaimaiMusicDetailList(System.currentTimeMillis())
        }
    }
}

@Serializable
enum class MaimaiMusicKind(val musicKindName: String) {
    DELUXE("dx"),
    STANDARD("sd"),
    UTAGE("utage");

    companion object {
        @JvmStatic
        fun getMusicKind(musicKindName: String): MaimaiMusicKind {
            for (kind in entries) {
                if (
                    kind.musicKindName == musicKindName.lowercase()
                    || kind.name.lowercase() == musicKindName.lowercase()
                ) {
                    return kind
                }
            }
            throw IllegalArgumentException("No such music kind")
        }
    }
}

@Serializable
enum class MaimaiDifficulty(val diffName: String, val diffIndex: Int) {
    BASIC("Basic", 0),
    ADVANCED("Advanced", 1),
    EXPERT("Expert", 2),
    MASTER("Master", 3),
    REMASTER("Re:Master", 4);

    companion object {
        @JvmStatic
        fun getDifficultyWithIndex(diffIndex: Int): MaimaiDifficulty {
            for (difficulty in entries) {
                if (difficulty.diffIndex == diffIndex) {
                    return difficulty
                }
            }
            throw IllegalArgumentException("No such difficulty")
        }
    }
}

@Serializable
enum class MaimaiClearType(
    val clearName: String, private val closedFloatingPointRange: ClosedFloatingPointRange<Double>,
    val iconPath: Path
) {
    D("d", 0.0000..49.9999, ResourceManager.MaimaiResources.RANK_D),
    C("c", 50.0000..59.9999, ResourceManager.MaimaiResources.RANK_C),
    B("b", 60.0000..69.9999, ResourceManager.MaimaiResources.RANK_B),
    BB("bb", 70.0000..74.9999, ResourceManager.MaimaiResources.RANK_BB),
    BBB("bbb", 75.0000..79.9999, ResourceManager.MaimaiResources.RANK_BBB),
    A("a", 80.0000..89.9999, ResourceManager.MaimaiResources.RANK_A),
    AA("aa", 90.0000..93.9999, ResourceManager.MaimaiResources.RANK_AA),
    AAA("aaa", 94.0000..96.9999, ResourceManager.MaimaiResources.RANK_AAA),
    S("s", 97.0000..97.9999, ResourceManager.MaimaiResources.RANK_S),
    SP("sp", 98.0000..98.9999, ResourceManager.MaimaiResources.RANK_SP),
    SS("ss", 99.0000..99.4999, ResourceManager.MaimaiResources.RANK_SS),
    SSP("ssp", 99.5000..99.9999, ResourceManager.MaimaiResources.RANK_SSP),
    SSS("sss", 100.0000..100.4999, ResourceManager.MaimaiResources.RANK_SSS),
    SSSP("sssp", 100.5000..101.0000, ResourceManager.MaimaiResources.RANK_SSSP);

    companion object {
        @JvmStatic
        fun getClearTypeByScore(score: Float): MaimaiClearType {
            var returnValue = D
            for (clearType in entries) {
                if (score in clearType.closedFloatingPointRange) {
                    returnValue = clearType
                }
            }
            return returnValue
        }
    }
}

@Serializable
enum class MaimaiSpecialClearType(val sepcialClearName: String, val iconPath: Path) {
    @SerialName("") NULL("", ResourceManager.MaimaiResources.BLAND),
    FC("fc", ResourceManager.MaimaiResources.FC),
    FCP("fcp", ResourceManager.MaimaiResources.FCP),
    AP("ap", ResourceManager.MaimaiResources.AP),
    APP("app", ResourceManager.MaimaiResources.APP);

    companion object {
        @JvmStatic
        fun getSpecialClearType(specialClearName: String): MaimaiSpecialClearType {
            for (specialClearType in entries) {
                if (specialClearType.sepcialClearName == specialClearName.lowercase()) {
                    return specialClearType
                }
            }
            throw IllegalArgumentException("No such special clear type")
        }
    }
}

@Serializable
enum class MaimaiSyncType(val syncName: String, val iconPath: Path) {
    @SerialName("") NULL("", ResourceManager.MaimaiResources.BLAND),
    SYNC("sync", ResourceManager.MaimaiResources.SYNC),
    FS("fs", ResourceManager.MaimaiResources.FS),
    FSP("fsp", ResourceManager.MaimaiResources.FSP),
    FDX("fsd", ResourceManager.MaimaiResources.FDX),
    FDXP("fsdp", ResourceManager.MaimaiResources.FXDP);

    companion object {
        @JvmStatic
        fun getSyncType(syncName: String): MaimaiSyncType {
            for (syncType in entries) {
                if (syncType.syncName == syncName.lowercase()) {
                    return syncType
                }
            }
            return NULL
        }
    }
}

@Serializable
enum class MaimaiDan(val danName: String, val danIndex: Int) {
    DAN0("初学者", 0),
    DAN1("初段", 1),
    DAN2("二段", 2),
    DAN3("三段", 3),
    DAN4("四段", 4),
    DAN5("五段", 5),
    DAN6("六段", 6),
    DAN7("七段", 7),
    DAN8("八段", 8),
    DAN9("九段", 9),
    DAN10("十段", 10),
    DAN11("真传", 11),
    DAN12("真初段", 12),
    DAN13("真二段", 13),
    DAN14("真三段", 14),
    DAN15("真四段", 15),
    DAN16("真五段", 16),
    DAN17("真六段", 17),
    DAN18("真七段", 18),
    DAN19("真八段", 19),
    DAN20("真九段", 20),
    DAN21("真十段", 21),
    DAN22("真皆传", 22),
    DAN23("里皆传", 23);

    override fun toString(): String {
        return this.danName
    }

    companion object {
        fun getDanByIndex(index: Int): MaimaiDan {
            return when (index) {
                0 -> DAN0
                1 -> DAN1
                2 -> DAN2
                3 -> DAN3
                4 -> DAN4
                5 -> DAN5
                6 -> DAN6
                7 -> DAN7
                8 -> DAN8
                9 -> DAN9
                10 -> DAN10
                11 -> DAN11
                12 -> DAN12
                13 -> DAN13
                14 -> DAN14
                15 -> DAN15
                16 -> DAN16
                17 -> DAN17
                18 -> DAN18
                19 -> DAN19
                20 -> DAN20
                21 -> DAN21
                22 -> DAN22
                else -> DAN0
            }
        }
    }
}