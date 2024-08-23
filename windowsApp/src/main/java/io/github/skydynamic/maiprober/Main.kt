package io.github.skydynamic.maiprober

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.skydynamic.maiprober.compose.DivingFishCompose
import io.github.skydynamic.maiprober.compose.SettingCompose
import io.github.skydynamic.maiprober.util.platform.windows.WindowsPlatformImpl
import io.github.skydynamic.maiprober.util.MemoryAppender
import io.github.skydynamic.maiprober.util.asIcon
import io.github.skydynamic.windowsapp.generated.resources.*
import io.github.skydynamic.windowsapp.generated.resources.Res
import io.github.skydynamic.windowsapp.generated.resources.dark_mode_24px
import io.github.skydynamic.windowsapp.generated.resources.fish_icon_24px
import io.github.skydynamic.windowsapp.generated.resources.light_mode_24px
import kotlinx.coroutines.launch
import org.apache.log4j.LogManager
import org.jetbrains.skiko.hostOs
import java.awt.Dimension

private val logger = LogManager.getLogger("GUI")
private var valueUpdater: (String) -> Unit = { }

@Preview
@Composable
fun mainComposable() {
    var logLines by remember { mutableStateOf(mutableListOf<String>()) }
    val darkDefault = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(darkDefault) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()
    var theme: ColorScheme by remember {
        mutableStateOf(
            if (isDarkTheme) {
                highContrastDarkColorScheme
            } else {
                lightScheme
            }
        )
    }
    var selectedItem = remember {
        mutableStateOf("diving_fish")
    }

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
                            selectedItem.value = "diving_fish"
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
                            selectedItem.value = "settings"
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxHeight()
                            .width(1.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 80.dp)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                ) {
                    when (selectedItem.value) {
                        "diving_fish" -> DivingFishCompose(theme)
                        "settings" -> SettingCompose(theme)
                    }
                }
            }
        }
    }
}

fun main() {
    LogManager.getRootLogger().also {
        it.addAppender(MemoryAppender{ s ->
            valueUpdater(s)
        })
    }
    application {
        Window(
            onCloseRequest = {
                val wp = WindowsPlatformImpl()
                wp.rollbackSystemProxy()
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