package io.github.skydynamic.maiprober

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.skydynamic.maiprober.compose.*
import io.github.skydynamic.maiprober.compose.DialogCompose.confirmDialog
import io.github.skydynamic.maiprober.util.*
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import io.github.skydynamic.windowsapp.generated.resources.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.hostOs
import java.awt.Dimension
import kotlin.io.path.exists

private var valueUpdater: (String) -> Unit = { }

@OptIn(DelicateCoroutinesApi::class)
@Preview
@Composable
fun mainComposable() {
    val darkDefault = isSystemInDarkTheme()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()
    val selectedItem = remember {
        mutableStateOf("diving_fish")
    }
    var pageIndex by remember { mutableStateOf(0) }
    var logLines by remember { mutableStateOf(mutableListOf<String>()) }
    var isDarkTheme by remember { mutableStateOf(darkDefault) }
    var theme: ColorScheme by remember {
        mutableStateOf(
            if (isDarkTheme) {
                highContrastDarkColorScheme
            } else {
                lightScheme
            }
        )
    }

    val pages: List<@Composable () -> Unit> = listOf(
        @Composable { DivingFishCompose() },
        @Composable { MaimaiB50GenerateCompose() },
        @Composable { SettingCompose() },
        @Composable { ResourceDownloadCompose() },
        @Composable { DownloadTaskCompose() }
    )

    valueUpdater = {
        logLines.add(it)
        logLines = logLines
        coroutineScope.launch {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    MaterialTheme(
        colorScheme = theme
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    header = {
                        IconButton(
                            onClick = {
                                isDarkTheme = !isDarkTheme
                                theme = if (isDarkTheme) {
                                    highContrastDarkColorScheme
                                } else {
                                    lightScheme
                                }
                            }
                        ) {
                            Icon(
                                (if (isDarkTheme) Res.drawable.dark_mode_24px else Res.drawable.light_mode_24px).asIcon(),
                                "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.width(80.dp).fillMaxHeight()
                ) {
                    NavigationRailItem(
                        selected = selectedItem.value == "diving_fish",
                        icon = {
                            Icon(
                                Res.drawable.fish_icon_24px.asIcon(),
                                "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = { Text("水鱼查分器", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            pageIndex = 0
                            selectedItem.value = "diving_fish"
                        }
                    )

                    NavigationRailItem(
                        selected = selectedItem.value == "b50",
                        icon = {
                            Icon(
                                Icons.Default.Check,
                                "",
                                tint = MaterialTheme.colorScheme.primary
                            )

                        },
                        label = { Text("MaimaiB50", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            pageIndex = 1
                            selectedItem.value = "b50"
                        }
                    )

                    NavigationRailItem(
                        selected = selectedItem.value == "settings",
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        label = { Text("设置", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            pageIndex = 2
                            selectedItem.value = "settings"
                        }
                    )

                    NavigationRailItem(
                        selected = selectedItem.value == "resource",
                        icon = {
                            Icon(
                                Icons.Default.Menu,
                                "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = { Text("资源下载", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                           coroutineScope.launch {
                               drawerState.close()
                           }
                            pageIndex = 3
                            selectedItem.value = "resource"
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    NavigationRailItem(
                        modifier = Modifier.padding(bottom = 15.dp),
                        selected = selectedItem.value == "downloadTasks",
                        icon = {
                            Icon(
                                Res.drawable.download_icon_24px.asIcon(),
                                "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = { Text("下载任务", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            pageIndex = 4
                            selectedItem.value = "downloadTasks"
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                ) {
                    Crossfade(
                        targetState = pageIndex,
                        animationSpec = tween(durationMillis = 300)
                    ) {
                        pages[it]()
                    }
                }
            }
        }
    }
}

fun main() {
    application {
        Window(
            onCloseRequest = {
                println("?")
                AppPlatform.rollbackSystemProxy()
                Config.write()
                exitApplication()
            },
            title = "MaimaiProber-MultiPlatform",
            icon = Res.drawable.logo.asIcon()
        ) {
            val density = LocalDensity.current
            val width = 500.dp
            val height = 500.dp
            LaunchedEffect(density) {
                window.minimumSize = with(density) {
                    if (hostOs.isWindows) {
                        Dimension(width.toPx().toInt(), height.toPx().toInt())
                    } else {
                        Dimension(width.value.toInt(), height.value.toInt())
                    }
                }
            }
            mainComposable()
        }
    }
}