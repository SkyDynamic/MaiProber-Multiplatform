package io.github.skydynamic.maiprober.util.prober.divingfish

import io.github.skydynamic.maiprober.util.config.Config.configStorage
import io.github.skydynamic.maiprober.util.prober.json
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.*

@Serializable
data class DivingFishChunithmMusicData(
    val id: String, val title: String,
    val ds: List<Float>, val level: List<String>,
    val cids: List<Int>, val chart: List<DivingFishChunithmMusicChart>,
    val basicInfo: DivingFishChunithmMusicBasicInfo
)

@Serializable
data class DivingFishChunithmMusicChart(val combo: Int, val charter: String)

@Serializable
data class DivingFishChunithmMusicBasicInfo(
    val title: String, val artist: String, val genre: String, val bpm: Int, val from: String
)

class DivingFishChuniMusicData {
    private val dataPath = Path("./data/chunithm_music_data.json")
    private var chunithmMusicData: List<DivingFishChunithmMusicData> = emptyList()

    init {
        init()
    }

    private fun init() = runBlocking {
        if (!dataPath.exists()) {
            val data: List<DivingFishChunithmMusicData> = DivingFishProberUtil.getChunithmMusicData()
            chunithmMusicData = data
            dataPath.createFile()
            dataPath.writeText(json.encodeToString(configStorage))
        } else {
            chunithmMusicData = json.decodeFromString(dataPath.readText())
        }
    }

    fun getMusicDataById(musicId: Int): DivingFishChunithmMusicData? {
        return chunithmMusicData.find { it.id == musicId.toString() }
    }

    fun getMusicDataByName(musicName: String): DivingFishChunithmMusicData? {
        return chunithmMusicData.find { it.basicInfo.title == musicName }
    }

    companion object {
        @JvmStatic
        val instance = DivingFishChuniMusicData()
    }
}

