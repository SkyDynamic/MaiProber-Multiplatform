package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.util.score.MaimaiClearType
import io.github.skydynamic.maiprober.util.score.MaimaiMusicDetail
import io.github.skydynamic.maiprober.util.score.MaimaiSpecialClearType
import io.github.skydynamic.maiprober.util.score.MaimaiSyncType
import org.jsoup.Jsoup

class ParseScorePageUtil {

    companion object {
        @JvmStatic
        fun parseMaimai(html: String): List<MaimaiMusicDetail> {
            val musicList = ArrayList<MaimaiMusicDetail>()

            val document = Jsoup.parse(html)
            val musicDetails = document.getElementsByClass("music_basic_score_back pointer p_3") +
                    document.getElementsByClass("music_advanced_score_back pointer p_3") +
                    document.getElementsByClass("music_expert_score_back pointer p_3") +
                    document.getElementsByClass("music_master_score_back pointer p_3") +
                    document.getElementsByClass("music_remaster_score_back pointer p_3")
            for (musicDetail in musicDetails) {
                val musicName = musicDetail
                    .getElementsByClass("music_name_block t_l f_13 break").text()
                val musicLevel = musicDetail
                    .getElementsByClass("music_lv_block f_r t_c f_14").text()
                val musicScore = musicDetail
                    .getElementsByClass("music_score_block w_112 t_r f_l f_12").text()
                val musicDxScore = musicDetail
                    .getElementsByClass("music_score_block w_190 t_r f_l f_12").text()

                val musicClearTypes = musicDetail.getElementsByClass("h_30 f_r")

                var musicClearType = MaimaiClearType.CLEAR
                var musicSyncType = MaimaiSyncType.SYNC
                var musicSpecialClearType = MaimaiSpecialClearType.COMMON

                for (musicClearTypeElement in musicClearTypes) {
                    val regex = Regex(".*music_icon_(.*?)?.png?.*")
                    val value = regex.find(musicClearTypeElement.attr("src"))?.groupValues?.get(1)

                    if (value != null) {
                        when (value) {
                            "clear" -> musicClearType        = MaimaiClearType.CLEAR
                            "s"     -> musicClearType        = MaimaiClearType.S
                            "ss"    -> musicClearType        = MaimaiClearType.SS
                            "ssp"   -> musicClearType        = MaimaiClearType.SSP
                            "sss"   -> musicClearType        = MaimaiClearType.SSS
                            "sssp"  -> musicClearType        = MaimaiClearType.SSSP
                            "sync"  -> musicSyncType         = MaimaiSyncType.SYNC
                            "fs"    -> musicSyncType         = MaimaiSyncType.FS
                            "fsp"   -> musicSyncType         = MaimaiSyncType.FSP
                            "fdx"   -> musicSyncType         = MaimaiSyncType.FDX
                            "fdxp"  -> musicSyncType         = MaimaiSyncType.FDXP
                            "back"  -> musicSpecialClearType = MaimaiSpecialClearType.COMMON
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
}