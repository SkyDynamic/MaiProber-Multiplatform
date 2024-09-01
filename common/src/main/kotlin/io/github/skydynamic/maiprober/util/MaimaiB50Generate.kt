package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.score.*
import io.github.skydynamic.maiprober.util.score.MaimaiMusicKind.*
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*
import org.jetbrains.skia.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.colter.skiko.*
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.layout.*
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readBytes

const val latestMaimaiVersion = 24000

val jacketSavePath = Path("./resources/maimai/jacket")
val iconSavePath = Path("./resources/maimai/icon")
val plateSavePath = Path("./resources/maimai/plate")

val client = HttpClient(CIO)

val logger: Logger = LoggerFactory.getLogger("MaimaiB50Generate")

val songScoreBackgroundColorList = listOf(
    // Basic
    Color.makeRGB(0, 210, 20),
    // Advanced
    Color.makeRGB(240, 160, 0),
    // Expert
    Color.makeRGB(200, 0, 50),
    // Master
    Color.makeRGB(160, 0, 255),
    // Re:MASTER
    Color.makeRGB(210, 160, 245)
)

val showImageSignal = MaiproberSignal<Unit>()

val config: ConfigStorage by Config

fun calcScore(score: String, songLevel: Float): Int {
    var formatScore = score.replace("%", "").toFloat()
    val multiplierFactor = when (formatScore) {
        in 10.0000..19.9999 -> 0.016 // D
        in 20.0000..29.9999 -> 0.032 // D
        in 30.0000..39.9999 -> 0.048 // D
        in 40.0000..49.9999 -> 0.064 // D
        in 50.0000..59.9999 -> 0.080 // C
        in 60.0000..69.9999 -> 0.096 // B
        in 70.0000..74.9999 -> 0.112 // BB
        in 75.0000..79.9999 -> 0.120 // BBB
        in 80.0000..89.9999 -> 0.128 // A
        in 90.0000..93.9999 -> 0.152 // AA
        in 94.0000..96.9998 -> 0.168 // AAA
        in 96.9999..96.9999 -> 0.176 // AAA
        in 97.0000..97.9999 -> 0.200 // S
        in 98.0000..98.9998 -> 0.203 // S+
        in 98.9999..98.9999 -> 0.206 // S+
        in 99.0000..99.4999 -> 0.208 // SS
        in 99.5000..99.9998 -> 0.211 // SS+
        in 99.9999..99.9999 -> 0.214 // SSS+
        in 100.0000..100.4998 -> 0.216 // SSS
        in 100.4999..100.4999 -> 0.222 // SSS
        in 100.5000..101.0000 -> 0.224 // SSS+
        else -> 0.000 // < 10.0000%
    }
    if (formatScore >= 100.5) formatScore = 100.5F
    return (songLevel * multiplierFactor * formatScore).toInt()
}

fun generatePlayerInfo(rating: Int): Image {
    return return View(
        modifier = Modifier(720.dp, 116.dp)
    ) {
        Box(modifier = Modifier().fillMaxSize()) {
            Image(
                image = Image.makeFromEncoded(
                    plateSavePath.resolve("${config.personalInfo.maimaiPlate}.png").toFile().readBytes()
                ),
                modifier = Modifier().width(720.dp)
            )
            Row(
                modifier = Modifier().fillMaxSize().padding(top = 5.dp, left = 5.dp)
            ) {
                Image(
                    image = Image.makeFromEncoded(
                        iconSavePath.resolve("${config.personalInfo.maimaiIcon}.png").toFile().readBytes()
                    ),
                    modifier = Modifier(106.dp, 106.dp)
                )

                Column(
                    modifier = Modifier().fillMaxSize().padding(left = 5.dp)
                ) {
                    Box(
                        modifier = Modifier().height(35.dp)
                    ) {
                        Image(
                            image = Image.makeFromEncoded(
                                ResourceManager.MaimaiResources.getDXRatingIconWithRating(rating).toFile().readBytes()
                            ),
                            modifier = Modifier().height(35.dp)
                        )
                        Box(
                            modifier = Modifier().padding(right = 15.dp),
                            alignment = LayoutAlignment.CENTER_RIGHT
                        ) {
                            Grid(
                                maxLineCount = 5,
                                space = 1.dp
                            ) {
                                for (i in rating.toString()) {
                                    val int = i.toString().toInt()
                                    Image(
                                        image = Image.makeFromEncoded(
                                            ResourceManager.MaimaiResources
                                                .getNumberIconWihmNumber(int).toFile().readBytes()
                                        ),
                                        modifier = Modifier().height(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier().width(width / 2 - 110.dp)
                            .height(40.dp)
                            .padding(left = 10.dp)
                            .border(8.dp, 8.dp, Color.TRANSPARENT)
                            .background(Color.WHITE)
                    ) {
                        Text(
                            text = config.personalInfo.name,
                            fontSize = 36.dp,
                            modifier = Modifier().fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier().padding(right = 5.dp),
                            alignment = LayoutAlignment.CENTER_RIGHT
                        ) {
                            Image(
                                image = Image.makeFromEncoded(
                                    ResourceManager.MaimaiResources
                                        .getDanIconWithIndex(config.personalInfo.maimaiDan.danIndex)
                                        .toFile()
                                        .readBytes()
                                ),
                                modifier = Modifier().height(35.dp)
                            )
                        }
                    }

                    Box(Modifier().height(5.dp))

                    Box(
                        modifier = Modifier().width(width / 2 - 110.dp)
                            .height(25.dp)
                            .padding(left = 10.dp)
                            .border(16.dp, 16.dp, Color.TRANSPARENT)
                            .background(Color.WHITE)
                    ) {
                        Text(
                            text = config.personalInfo.maimaiTitle,
                            fontSize = 12.dp,
                            modifier = Modifier().fillMaxWidth(),
                            alignment = LayoutAlignment.CENTER
                        )
                    }
                }
            }
        }
    }
}

fun generateSongScore(data: MaimaiMusicDetail): Image {
    val songInfo = MAIMAI_SONG_INFO.find { it.title == data.name }
    val songId = songInfo?.id ?: -1
    val iconPath = jacketSavePath.resolve("${songId}.png")
    val fontColor = if (data.diff == MaimaiDifficulty.REMASTER) Color.makeRGB(128, 5, 210) else Color.WHITE

    if (!iconPath.exists()) {
        if (songInfo != null) {
            runBlocking {
                if (!jacketSavePath.exists()) jacketSavePath.toFile().mkdirs()
                downloadWithRetry(
                    url = "$jacketResourceUrl/${songInfo.id}.png",
                    savePath = jacketSavePath.resolve("${songInfo.id}.png")
                )
            }
        } else {
            logger.error("Song info not found for ${data.name}")
        }
    }

    return View(
        modifier = Modifier(380.dp, 180.dp)
            .background(Color.TRANSPARENT)
    ) {
        Row(modifier = Modifier().fillMaxSize()) {
            Column(
                modifier = Modifier(130.dp, 180.dp)
                    .border(8.dp, 8.dp, Color.TRANSPARENT)
                    .background(songScoreBackgroundColorList[data.diff.diffIndex].withAlpha(0.8f))
            ) {
                // icon
                Box (
                    modifier = Modifier(120.dp, 120.dp)
                        .padding(top = 5.dp, left = 5.dp)
                ) {
                    Image(
                        image = Image.makeFromEncoded(iconPath.toFile().readBytes()),
                        modifier = Modifier().fillMaxSize().border(8.dp, 8.dp, Color.TRANSPARENT)
                    )
                }
                // song kind
                val kindIcon = when(data.uepInfo) {
                    DELUXE -> ResourceManager.MaimaiResources.DELUXE
                    STANDARD -> ResourceManager.MaimaiResources.STANDARD
                }
                Box(
                    modifier = Modifier(117.dp, 32.dp).padding(top = 15.dp),
                    alignment = LayoutAlignment.CENTER
                ) {
                    Image(
                        image = Image.makeFromEncoded(kindIcon.toFile().readBytes()),
                        modifier = Modifier().fillMaxSize()
                    )
                }
            }

            Box(
                modifier = Modifier(250.dp, 168.dp).padding(top = 9.dp)
            ) {
                Column(
                    modifier = Modifier().fillMaxSize()
                        .border(8.dp, listOf(0.dp, 8.dp, 8.dp, 0.dp), Color.TRANSPARENT)
                        .background(songScoreBackgroundColorList[data.diff.diffIndex].withAlpha(0.8f)),
                ) {
                    // title
                    Box(
                        modifier = Modifier(250.dp, 24.dp).padding(top = 5.dp, left = 5.dp),
                    ) {
                        Text(data.name, fontColor, fontSize = 24.dp, fontStyle = FontStyle.BOLD)
                    }
                    // line
                    Box(
                        modifier = Modifier(250.dp, 24.dp).padding(top = 5.dp, left = 5.dp),
                    ) {
                        Text(
                            "—————————————",
                            color = Color.WHITE,
                            fontStyle = FontStyle.BOLD
                        )
                    }
                    //score
                    Box(
                        modifier = Modifier(250.dp, 24.dp).padding(left = 5.dp),
                        alignment = LayoutAlignment.CENTER
                    ) {
                        Text(data.score, fontColor, fontSize = 28.dp, fontStyle = FontStyle.BOLD)
                    }
                    // level and dxscore
                    Box(
                        modifier = Modifier(250.dp, 24.dp).padding(left = 5.dp),
                    ) {
                        Text(
                            "${data.level} -> ${data.rating}",
                            fontColor,
                            fontSize = 16.dp
                        )
                        Box(modifier = Modifier().fillMaxSize().padding(right = 5.dp)) {
                            Text(
                                data.dxScore,
                                fontColor,
                                fontSize = 16.dp,
                                alignment = LayoutAlignment.CENTER_RIGHT
                            )
                        }
                    }
                    // Clear Type
                    Box(
                        modifier = Modifier(239.dp, 48.dp).padding(left = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier(239.dp, 48.dp)
                                .border(8.dp, 8.dp, Color.TRANSPARENT)
                                .background(Color.WHITE),
                        ) {
                            val clearTypeIcon = Image.makeFromEncoded(data.clearType.iconPath.toFile().readBytes())
                            val clearTypeIconRatio = clearTypeIcon.width.toFloat() / clearTypeIcon.height.toFloat()
                            val clearTypeIconWidget = clearTypeIcon.width * 0.75
                            val clearTypeIconHeight = clearTypeIconWidget / clearTypeIconRatio

                            Box(
                                modifier = Modifier(
                                    clearTypeIconWidget.toInt().dp + 3.dp,
                                    clearTypeIconHeight.toInt().dp + 2.dp
                                ).padding(left = 5.dp),
                                alignment = LayoutAlignment.CENTER
                            ) {
                                Image(
                                    image = clearTypeIcon,
                                    modifier = Modifier().fillMaxSize()
                                )
                            }

                            val paddingValue = 239 - 100 - clearTypeIconWidget.toInt()

                            Row(
                                modifier = Modifier().fillMaxSize().padding(left = paddingValue.dp)
                            ) {
                                val syncTypeIcon = Image.makeFromEncoded(data.syncType.iconPath.toFile().readBytes())
                                val syncTypeIconWidget = syncTypeIcon.width * 0.75
                                val syncTypeIconHeight = syncTypeIcon.height * 0.75

                                val specialClearTypeIcon =
                                    Image.makeFromEncoded(data.specialClearType.iconPath.toFile().readBytes())
                                val specialClearTypeIconWidget = specialClearTypeIcon.width * 0.75
                                val specialClearTypeIconHeight = specialClearTypeIcon.height * 0.75

                                Box(
                                    modifier = Modifier(
                                        specialClearTypeIconWidget.toInt().dp + 2.dp,
                                        specialClearTypeIconHeight.toInt().dp + 2.dp
                                    ),
                                    alignment = LayoutAlignment.CENTER_RIGHT
                                ) {
                                    Image(
                                        image = specialClearTypeIcon,
                                        modifier = Modifier().fillMaxSize()
                                    )
                                }

                                Box(
                                    modifier = Modifier(
                                        syncTypeIconWidget.toInt().dp + 2.dp,
                                        syncTypeIconHeight.toInt().dp + 2.dp
                                    ),
                                    alignment = LayoutAlignment.CENTER_RIGHT
                                ) {
                                    Image(
                                        image = syncTypeIcon,
                                        modifier = Modifier().fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun generateB50(data: MaimaiMusicDetailList, timestamp: Long) {
    val musicList = data.songs
    var rating = 0
    val b35List = musicList.filter { it.version < latestMaimaiVersion }.sortedByDescending { it.rating }.take(35)
    val b15List = musicList.filter { it.version >= latestMaimaiVersion }.sortedByDescending { it.rating }.take(15)

    b35List.forEach { rating += it.rating }
    b15List.forEach { rating += it.rating }

    val iconNotFoundList = mutableListOf<DownloadInfo>()
    (b35List + b15List).forEach {
        val id = MAIMAI_SONG_INFO.find { info ->
            info.title == it.name
        }?.id ?: -1
        if (!jacketSavePath.resolve("${id}.png").exists()) {
            iconNotFoundList.add(DownloadInfo(url = jacketResourceUrl + "/${id}.png", fileName = "${id}.png"))
        }
    }

    if (iconNotFoundList.isNotEmpty()) {
        runBlocking {
            downloadUrlsWithNotSignal(iconNotFoundList, jacketSavePath)
        }
    }

    val saveFile = File("data/maimai/b50/output_$timestamp.png")
    if (!saveFile.exists()) saveFile.parentFile.mkdirs()

    View(
        file = saveFile,
        modifier = Modifier().width(2080.dp).height(2750.dp)
    ) {
        Box(modifier = Modifier().fillMaxSize()) {
            val color = Config.settings.maimaiB50BackgroundColor
            val colors = color.split(";", "；").map { Color.makeRGB(it.trim()) }
            val linearLayoutCount = when (Config.settings.maimaiB50BackgroundLinearLayerCount) {
                in 1..20 -> Config.settings.maimaiB50BackgroundLinearLayerCount
                else -> 12
            }
            Image(
                image = ColorfulBackgroundGenerate.makeCardBg(2080, 2750, colors, linearLayoutCount)
            )
        }
        Column(modifier = Modifier().fillMaxSize().padding(60.dp)) {
            Box(
                modifier = Modifier().fillMaxWidth()
            ) {
                Image(
                    image = generatePlayerInfo(rating),
                    modifier = Modifier().height(200.dp),
                    alignment = LayoutAlignment.CENTER
                )
            }

            Box(modifier = Modifier().fillMaxWidth().padding(top = 30.dp)) {
                Column(modifier = Modifier().fillMaxSize()) {
                    Box(modifier = Modifier().fillMaxWidth().height(150.dp)) {
                        Box(modifier = Modifier().width(1500.dp).padding(left = 440.dp)) {
                            Image(
                                image =
                                Image.makeFromEncoded(ResourceManager.MaimaiResources.BESTS_BACKGROUND.readBytes()),
                            )
                            Text(
                                "Bests 35",
                                fontSize = 80.dp,
                                alignment = LayoutAlignment.TOP_CENTER,
                                color = Color.BLACK
                            )
                        }
                    }

                    Grid(
                        maxLineCount = 5,
                        space = 15.dp,
                        modifier = Modifier().fillMaxSize()
                    ) {
                        for (music in b35List) {
                            Box(modifier = Modifier(380.dp, 180.dp)) {
                                Image(
                                    image = generateSongScore(music)
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier().fillWidth().padding(top = 60.dp)) {
                Column(modifier = Modifier().fillMaxSize()) {
                    Box(modifier = Modifier().fillMaxWidth().height(150.dp)) {
                        Box(modifier = Modifier().width(1500.dp).padding(left = 440.dp)) {
                            Image(
                                image =
                                Image.makeFromEncoded(ResourceManager.MaimaiResources.BESTS_BACKGROUND.readBytes()),
                            )
                            Text(
                                "Bests 15",
                                fontSize = 80.dp,
                                alignment = LayoutAlignment.TOP_CENTER,
                                color = Color.BLACK
                            )
                        }
                    }

                    Grid(
                        maxLineCount = 5,
                        space = 15.dp,
                        modifier = Modifier().fillMaxSize()
                    ) {
                        for (music in b15List) {
                            Box(modifier = Modifier(380.dp, 180.dp)) {
                                Image(
                                    image = generateSongScore(music)
                                )
                            }
                        }
                    }
                }
            }
            Text(
                "Generate by MaimaiProber-MultiPlatform | Style by SkyDynamic",
                alignment = LayoutAlignment.BOTTOM_CENTER,
                color = Color.BLACK,
                fontSize = 48.dp
            )
        }
    }
    showImageSignal.emit(Unit)
}