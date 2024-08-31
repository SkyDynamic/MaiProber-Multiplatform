package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.compose.DialogCompose.confirmDialog
import io.github.skydynamic.maiprober.util.downloadFinishSignal
import io.github.skydynamic.maiprober.util.downloadSongsIcon
import io.github.skydynamic.maiprober.util.downloadStartSignal
import io.github.skydynamic.maiprober.util.jacketSavePath
import io.github.skydynamic.maiprober.util.score.MaimaiMusicDetailList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.path.exists

enum class MaimaiB50Platform(val id: String, val index: Int) {
    LocalCache("本地缓存", 0),
    DivingFish("水鱼查分器", 1),
    Lxns("落雪查分器", 2)
}

fun generateB50(platform: MaimaiB50Platform) {
    when(platform) {
        MaimaiB50Platform.LocalCache -> {
            io.github.skydynamic.maiprober.util.generateB50(MaimaiMusicDetailList.getCache())
        }
        MaimaiB50Platform.DivingFish -> {
        }
        MaimaiB50Platform.Lxns -> {
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
fun MaimaiB50GenerateCompose(theme: ColorScheme) {
    var selectedPlatformIndex by remember { mutableStateOf(0) }
    var isExpandedDropdownMenu by remember { mutableStateOf(false) }
    var openDownloadJacketAskDialog by mutableStateOf(false)
    var openDownloadJacketStartedDialog by mutableStateOf(false)
    var openDownloadJacketFinishedDialog by mutableStateOf(false)

    val proberPlatformList = MaimaiB50Platform.entries

    MaterialTheme(
        colorScheme = theme
    ) {
        when {
            openDownloadJacketAskDialog -> {
                confirmDialog(
                    info = "尚未下载曲绘, 是否现在下载",
                    onConfirm = {
                        downloadStartSignal.connect { openDownloadJacketStartedDialog = true }
                        downloadFinishSignal.connect { openDownloadJacketFinishedDialog = true }
                        GlobalScope.launch {
                            downloadSongsIcon()
                        }
                        openDownloadJacketAskDialog = false
                    },
                    onCancel = {
                        openDownloadJacketAskDialog = false
                    }
                )
            }

            openDownloadJacketStartedDialog -> {
                DialogCompose.infoDialog(
                    info = "正在下载曲绘, 请稍后",
                    onRequest = { openDownloadJacketStartedDialog = false }
                )
            }

            openDownloadJacketFinishedDialog -> {
                DialogCompose.infoDialog(
                    info = "下载曲绘完成",
                    onRequest = { openDownloadJacketFinishedDialog = false }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = isExpandedDropdownMenu,
                    onExpandedChange = { isExpandedDropdownMenu = !isExpandedDropdownMenu }
                ) {
                    TextField(
                        readOnly = true,
                        value = proberPlatformList[selectedPlatformIndex].id,
                        onValueChange = { },
                        label = { Text("查分使用的平台") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isExpandedDropdownMenu
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isExpandedDropdownMenu,
                        onDismissRequest = { isExpandedDropdownMenu = false },
                    ) {
                        for (platform in proberPlatformList) {
                            DropdownMenuItem(
                                text = {
                                    Text(platform.id)
                                },
                                onClick = {
                                    selectedPlatformIndex = platform.index
                                    isExpandedDropdownMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (!jacketSavePath.exists()) {
                            openDownloadJacketAskDialog = true
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            generateB50(proberPlatformList[selectedPlatformIndex])
                        }
                    }
                ) {
                    Text("生成B50")
                }
            }
        }
    }
}