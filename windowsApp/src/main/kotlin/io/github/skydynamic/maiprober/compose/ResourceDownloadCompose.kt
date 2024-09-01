package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.*
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.path.exists

@Composable
@OptIn(DelicateCoroutinesApi::class)
fun ResourceDownloadCompose() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "资源预下载",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        val maimaiJacketExists = jacketSavePath.exists()
        val maimaiIconExists = iconSavePath.exists()
        val maimaiPlateExists = plateSavePath.exists()

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "头像资源预下载",
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
                enabled = !maimaiIconExists
            ) {
                Text(if (maimaiIconExists) "已下载" else "下载")
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "头像框资源预下载",
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
                enabled = !maimaiPlateExists
            ) {
                Text(if (maimaiPlateExists) "已下载" else "下载")
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "曲绘资源预下载",
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
                enabled = !maimaiJacketExists
            ) {
                Text(if (maimaiJacketExists) "已下载" else "下载")
            }
        }
    }
}