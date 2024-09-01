package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.util.score.MAIMAI_ICON_LIST
import io.github.skydynamic.maiprober.util.score.MAIMAI_PLATE_LIST
import io.github.skydynamic.maiprober.util.score.MAIMAI_SONG_INFO
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.exists

// Use Lxns resource
const val resourceUrl = "https://assets2.lxns.net/maimai"
const val iconResourceUrl = "$resourceUrl/icon"
const val plateResourceUrl = "$resourceUrl/plate"
const val jacketResourceUrl = "$resourceUrl/jacket"

data class DownloadInfo(val url: String, val fileName: String)

@OptIn(DelicateCoroutinesApi::class)
suspend fun downloadUrls(
    downloadStartSignal: MaiproberSignal<Unit>,
    downloadFinishSignal: MaiproberSignal<Unit>,
    finishSignal: MaiproberSignal<Unit>,
    updateSizeSignal: MaiproberSignal<Int>,
    downloadList: List<DownloadInfo>,
    savePath: Path
) {
    if (!savePath.exists()) savePath.toFile().mkdirs()
    downloadStartSignal.emit(Unit)
    val jobs = mutableListOf<Job>()

    val semaphore = Semaphore(36)
    updateSizeSignal.emit(downloadList.size)

    downloadList.forEach {
        val job = GlobalScope.async(Dispatchers.IO) {
            semaphore.acquire()
            downloadWithRetry(
                url = it.url,
                savePath = savePath.resolve(it.fileName)
            )
            finishSignal.emit(Unit)
            semaphore.release()
        }
        jobs.add(job)
    }

    jobs.forEach {
        it.join()
    }

    downloadFinishSignal.emit(Unit)
}

suspend fun downloadUrlsWithNotSignal(
    downloadList: List<DownloadInfo>,
    savePath: Path
) {
    downloadUrls(
        downloadList = downloadList,
        savePath = savePath,
        downloadStartSignal = MaiproberSignal(),
        downloadFinishSignal = MaiproberSignal(),
        finishSignal = MaiproberSignal(),
        updateSizeSignal = MaiproberSignal()
    )
}

suspend fun downloadMaimaiSongIcons(
    downloadStartSignal: MaiproberSignal<Unit>,
    downloadFinishSignal: MaiproberSignal<Unit>,
    finishSignal: MaiproberSignal<Unit>,
    updateSizeSignal: MaiproberSignal<Int>
) {
    val downloadInfos = mutableListOf<DownloadInfo>()
    MAIMAI_SONG_INFO.forEach {
        downloadInfos.add(DownloadInfo(jacketResourceUrl + "/${it.id}.png", "${it.id}.png"))
    }

    downloadUrls(
        downloadList = downloadInfos,
        savePath = jacketSavePath,
        downloadStartSignal = downloadStartSignal,
        downloadFinishSignal = downloadFinishSignal,
        finishSignal = finishSignal,
        updateSizeSignal = updateSizeSignal
    )
}

suspend fun downloadMaimaiPlayerIcons(
    downloadStartSignal: MaiproberSignal<Unit>,
    downloadFinishSignal: MaiproberSignal<Unit>,
    finishSignal: MaiproberSignal<Unit>,
    updateSizeSignal: MaiproberSignal<Int>
) {
    val downloadInfos = mutableListOf<DownloadInfo>()
    MAIMAI_ICON_LIST.forEach {
        downloadInfos.add(DownloadInfo(iconResourceUrl + "/${it}.png", "${it}.png"))
    }

    downloadUrls(
        downloadList = downloadInfos,
        savePath = iconSavePath,
        downloadStartSignal = downloadStartSignal,
        downloadFinishSignal = downloadFinishSignal,
        finishSignal = finishSignal,
        updateSizeSignal = updateSizeSignal
    )
}

suspend fun downloadMaimaiPlate(
    downloadStartSignal: MaiproberSignal<Unit>,
    downloadFinishSignal: MaiproberSignal<Unit>,
    finishSignal: MaiproberSignal<Unit>,
    updateSizeSignal: MaiproberSignal<Int>
) {
    val downloadInfos = mutableListOf<DownloadInfo>()
    MAIMAI_PLATE_LIST.forEach {
        downloadInfos.add(DownloadInfo(plateResourceUrl + "/${it}.png", "${it}.png"))
    }

    downloadUrls(
        downloadList = downloadInfos,
        savePath = plateSavePath,
        downloadStartSignal = downloadStartSignal,
        downloadFinishSignal = downloadFinishSignal,
        finishSignal = finishSignal,
        updateSizeSignal = updateSizeSignal
    )
}

suspend fun downloadWithRetry(
    url: String, savePath: Path, maxRetries: Int = 3,
    delayMillis: Long = 1000L
) {
    var retries = 0
    while (retries <= maxRetries) {
        val resp: HttpResponse = client.get(url)

        if (resp.status.isSuccess()) {
            val inputStream: InputStream = resp.body()
            withContext(Dispatchers.IO) {
                val outputFile = savePath.toFile()
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return
        } else {
            if (resp.status == HttpStatusCode.NotFound) continue
            retries++
            if (retries > maxRetries) {
                logger.error("Failed to download $url after $maxRetries retries.")
                return
            }
            logger.warn("Failed to download $url. Retrying... ($retries/$maxRetries)")
            delay(delayMillis)
        }
    }
}