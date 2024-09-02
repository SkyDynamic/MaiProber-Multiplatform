package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.skydynamic.maiprober.util.*
import io.github.skydynamic.maiprober.util.score.MAIMAI_ICON_LIST
import io.github.skydynamic.maiprober.util.score.MAIMAI_PLATE_LIST
import io.github.skydynamic.maiprober.util.score.MAIMAI_SONG_INFO
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ResourceDownloadViewModel : ViewModel() {
    var iconDownloadStarted by mutableStateOf(false)
    var plateDownloadStarted by mutableStateOf(false)
    var jacketDownloadStarted by mutableStateOf(false)
}

@Composable
@OptIn(DelicateCoroutinesApi::class)
fun ResourceDownloadCompose() {
    val viewModel = remember { ResourceDownloadViewModel() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "资源预下载",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (shouldDownload, downloadNum) = ResourceManager.getDoNotDownloadResourceNum(
                resourceNum = MAIMAI_ICON_LIST.size,
                checkPath = iconSavePath,
                checkFileType = ".png"
            )
            val shouldDownload_ by remember { mutableStateOf(shouldDownload) }
            Text(
                "头像资源预下载 ($downloadNum / ${MAIMAI_ICON_LIST.size})",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.weight(1.0f))

            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    val dss = MaiproberSignal<Unit>()
                    val dfs = MaiproberSignal<Unit>()
                    val fs = MaiproberSignal<Unit>()
                    val uss = MaiproberSignal<Int>()
                    createMultiDownloadTask(
                        downloadStartSignal = dss,
                        downloadFinishedSignal = dfs,
                        finishSignal = fs,
                        updateSizeSignal = uss,
                        title = "头像",
                        savePath = iconSavePath,
                        task = { GlobalScope.launch { downloadMaimaiPlayerIcons(dss, dfs, fs, uss) } })
                },
                enabled = !shouldDownload_ && !viewModel.iconDownloadStarted
            ) {
                viewModel.iconDownloadStarted = !viewModel.iconDownloadStarted
                Text(
                    if (shouldDownload_) { "已下载" } else "下载"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (shouldDownload, downloadNum) = ResourceManager.getDoNotDownloadResourceNum(
                resourceNum = MAIMAI_PLATE_LIST.size,
                checkPath = plateSavePath,
                checkFileType = ".png"
            )
            val shouldDownload_ by remember { mutableStateOf(shouldDownload) }
            Text(
                "头像框资源预下载 ($downloadNum / ${MAIMAI_PLATE_LIST.size})",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.weight(1.0f))

            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    val dss = MaiproberSignal<Unit>()
                    val dfs = MaiproberSignal<Unit>()
                    val fs = MaiproberSignal<Unit>()
                    val uss = MaiproberSignal<Int>()
                    createMultiDownloadTask(
                        downloadStartSignal = dss,
                        downloadFinishedSignal = dfs,
                        finishSignal = fs,
                        updateSizeSignal = uss,
                        title = "头像框",
                        savePath = iconSavePath,
                        task = { GlobalScope.launch { downloadMaimaiPlate(dss, dfs, fs, uss) } }
                    )
                },
                enabled = !shouldDownload_ && !viewModel.plateDownloadStarted
            ) {
                viewModel.plateDownloadStarted = !viewModel.plateDownloadStarted
                Text(if (shouldDownload_) "已下载" else "下载")
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (shouldDownload, downloadNum) = ResourceManager.getDoNotDownloadResourceNum(
                resourceNum = MAIMAI_SONG_INFO.size,
                offset = 20,
                checkPath = jacketSavePath,
                checkFileType = ".png"
            )
            val shouldDownload_ by remember { mutableStateOf(shouldDownload) }
            Text(
                "曲绘资源预下载 ($downloadNum / ${MAIMAI_SONG_INFO.size})",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.weight(1.0f))

            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    val dss = MaiproberSignal<Unit>()
                    val dfs = MaiproberSignal<Unit>()
                    val fs = MaiproberSignal<Unit>()
                    val uss = MaiproberSignal<Int>()
                    createMultiDownloadTask(
                        downloadStartSignal = dss,
                        downloadFinishedSignal = dfs,
                        finishSignal = fs,
                        updateSizeSignal = uss,
                        title = "曲绘",
                        savePath = iconSavePath,
                        task = { GlobalScope.launch { downloadMaimaiSongIcons(dss, dfs, fs, uss) } }
                    )
                },
                enabled = !shouldDownload_ && !viewModel.jacketDownloadStarted
            ) {
                viewModel.jacketDownloadStarted = !viewModel.jacketDownloadStarted
                Text(if (shouldDownload_) "已下载" else "下载")
            }
        }
    }
}