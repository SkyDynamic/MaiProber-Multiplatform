package io.github.skydynamic.maiprober.util.score

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicDetail(val name: String, val level: String, val score: String, val dxScore: String)

@Serializable
data class ChunithmMusicDetailList(
    var basic: List<ChunithmMusicDetail> = listOf(), var advanced: List<ChunithmMusicDetail> = listOf(),
    var expert: List<ChunithmMusicDetail> = listOf(), var master: List<ChunithmMusicDetail> = listOf(),
    var ultima: List<ChunithmMusicDetail> = listOf(), var recent: List<ChunithmMusicDetail> = listOf()
) {
    fun setMusicDetailByDifficulty(difficulty: ChunithmDifficulty, musicList: List<ChunithmMusicDetail>) {
        when (difficulty) {
            ChunithmDifficulty.BASIC -> basic = musicList
            ChunithmDifficulty.ADVANCED -> advanced = musicList
            ChunithmDifficulty.EXPERT -> expert = musicList
            ChunithmDifficulty.MASTER -> master = musicList
            ChunithmDifficulty.ULTIMA -> ultima = musicList
            ChunithmDifficulty.RECENT -> recent = musicList
        }
    }
}

enum class ChunithmDifficulty(private val diffName: String) {
    BASIC("basic"),
    ADVANCED("advanced"),
    EXPERT("expert"),
    MASTER("master"),
    ULTIMA("ultima"),
    RECENT("recent");

    fun getDifficultyByName(diffName: String): ChunithmDifficulty {
        for (diff in entries) {
            if (diff.diffName == diffName) {
                return diff
            }
        }
        throw IllegalArgumentException("Unknown difficulty: $diffName")
    }
}