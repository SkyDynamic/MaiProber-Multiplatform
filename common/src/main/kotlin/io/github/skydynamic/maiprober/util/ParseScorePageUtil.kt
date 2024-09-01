package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.util.score.*
import org.jsoup.Jsoup

object ParseScorePageUtil {
    @JvmStatic
    fun parseMaimai(html: String, difficulty: MaimaiDifficulty): List<MaimaiMusicDetail> {
        val musicList = ArrayList<MaimaiMusicDetail>()

        val document = Jsoup.parse(html)
        val musicCards = document.getElementsByClass("w_450 m_15 p_r f_0")

        for (musicCard in musicCards) {
            val musicName = musicCard
                .getElementsByClass("music_name_block t_l f_13 break").text()
            val musicScore = musicCard
                .getElementsByClass("music_score_block w_112 t_r f_l f_12").text()
            val musicDxScore = musicCard
                .getElementsByClass("music_score_block w_190 t_r f_l f_12").text()

            if (musicScore.isEmpty() && musicDxScore.isEmpty()) {
                continue
            }

            val musicClearTypes = musicCard.getElementsByClass("h_30 f_r")

            val musicClearType = MaimaiClearType
                .getClearTypeByScore(musicScore.replace("%", "").toFloat())
            var musicSyncType = MaimaiSyncType.NULL
            var musicSpecialClearType = MaimaiSpecialClearType.NULL

            val isDeluxe = musicCard.getElementsByClass("music_kind_icon")
                .attr("src")
                .contains("music_dx")
            val musicKind = if (isDeluxe) MaimaiMusicKind.DELUXE else MaimaiMusicKind.STANDARD

            val res = MAIMAI_SONG_INFO.find { it.title == musicName }
            val musicLevel = if (isDeluxe) {
                if (res != null) res.dxLevel[difficulty.diffIndex] else -1f
            } else {
                if (res != null) res.standardLevel[difficulty.diffIndex] else -1f
            }

            val musicRating = calcScore(musicScore, musicLevel)
            val musicVersion = res?.version ?: 10000

            for (musicClearTypeElement in musicClearTypes) {
                val regex = Regex(".*music_icon_(.*?)?.png?.*")
                val value = regex.find(musicClearTypeElement.attr("src"))?.groupValues?.get(1)
                if (value != null) {
                    when (value) {
                        "sync"  -> musicSyncType         = MaimaiSyncType.SYNC
                        "fs"    -> musicSyncType         = MaimaiSyncType.FS
                        "fsp"   -> musicSyncType         = MaimaiSyncType.FSP
                        "fdx"   -> musicSyncType         = MaimaiSyncType.FDX
                        "fdxp"  -> musicSyncType         = MaimaiSyncType.FDXP
                        "fc"    -> musicSpecialClearType = MaimaiSpecialClearType.FC
                        "fcp"   -> musicSpecialClearType = MaimaiSpecialClearType.FCP
                        "ap"    -> musicSpecialClearType = MaimaiSpecialClearType.AP
                        "app"   -> musicSpecialClearType = MaimaiSpecialClearType.APP
                    }
                }
            }
            musicList.add(
                MaimaiMusicDetail(
                    musicName, musicLevel,
                    musicScore, musicDxScore,
                    musicRating, musicVersion,
                    musicKind, difficulty,
                    musicClearType, musicSyncType,
                    musicSpecialClearType
                )
            )
        }
        return musicList
    }

    @JvmStatic
    fun parseChunithm(html: String) {
        TODO()
    }
}