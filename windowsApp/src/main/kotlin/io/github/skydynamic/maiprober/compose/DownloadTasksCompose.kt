package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import java.nio.file.Path

data class DownloadTask(
    val title: String, val savePath: Path,
    val data: MultiDownloadTaskDetailData, val downloadStartSignal: MaiproberSignal<Unit>,
    val downloadFinishedSignal: MaiproberSignal<Unit>, val finishSignal: MaiproberSignal<Unit>,
    val updateSizeSignal: MaiproberSignal<Int>
)

class MultiDownloadTaskDetailData {
    var maxSize by mutableStateOf(0)
    var isDownloading = mutableStateOf(false)
    var finishedNum by mutableStateOf(0)
}

val tasks: MutableList<DownloadTask> = mutableListOf()

fun createMultiDownloadTask(
    downloadStartSignal: MaiproberSignal<Unit>,
    downloadFinishedSignal: MaiproberSignal<Unit>,
    finishSignal: MaiproberSignal<Unit>,
    updateSizeSignal: MaiproberSignal<Int>,
    title: String, savePath: Path,
    task: () -> Unit
) {
    val data = MultiDownloadTaskDetailData()
    updateSizeSignal.connect {
        data.maxSize = it
    }
    finishSignal.connect {
        data.finishedNum += 1
    }
    val taskDetail = DownloadTask(
        title, savePath, data, downloadStartSignal, downloadFinishedSignal, finishSignal, updateSizeSignal
    )
    tasks.add(taskDetail)
    task()
}

@Composable
fun MultiDownloadTaskDetailCompose(
    modifier: Modifier = Modifier,
    title: String,
    savePath: String,
    data: MultiDownloadTaskDetailData
) {
    val taskData = remember { data }

    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = title)
                Text(text = "${taskData.finishedNum} / ${taskData.maxSize}")
            }
            Spacer(modifier = Modifier.weight(1.0f))

            LinearProgressIndicator(
                progress = {
                    taskData.finishedNum.toFloat() / taskData.maxSize
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
        }
    }
}

@Composable
fun DownloadTaskCompose() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "下载任务",
            textAlign = TextAlign.Center
        )

        for (task in tasks) {
            MultiDownloadTaskDetailCompose(
                modifier = Modifier.padding(8.dp).height(120.dp),
                title = task.title,
                savePath = task.savePath.toString(),
                data = task.data
            )
        }
    }
}