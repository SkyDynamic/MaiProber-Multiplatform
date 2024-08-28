package io.github.skydynamic.maiprober.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import java.io.File

class ParseScorePageUtilTest {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testParseMaimaiPage() {
        val musicList = ParseScorePageUtil.MaimaiMusicDetailList()
        for (diffName in listOf("basic", "advanced", "expert", "master", "remaster")) {
            val html = File("src/test/resources/maimai_${diffName}_score_page.html").readText()

            ParseScorePageUtil.parseMaimai(html)
            musicList.setMusicDetailByDifficulty(ParseScorePageUtil.MaimaiDifficulty.valueOf(diffName.uppercase()), ParseScorePageUtil.parseMaimai(html))
        }

        File("src/test/resources/${System.currentTimeMillis()}_maimai.json")
            .writeText(json.encodeToString(musicList))
    }
}