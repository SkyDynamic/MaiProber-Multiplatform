package io.github.skydynamic.maiprober

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.skydynamic.maiprober.compose.loginComposable
import io.github.skydynamic.maiprober.util.MemoryAppender
import io.github.skydynamic.maiprober.util.asIcon
import io.github.skydynamic.windowsapp.generated.resources.Res
import io.github.skydynamic.windowsapp.generated.resources.dark_mode_24px
import io.github.skydynamic.windowsapp.generated.resources.light_mode_24px
import kotlinx.coroutines.launch
import org.apache.log4j.LogManager
import org.jetbrains.skiko.hostOs
import java.awt.Dimension

private val logger = LogManager.getLogger("GUI")
private var valueUpdater: (String) -> Unit = { }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainComposable() {
    var logLines by remember { mutableStateOf(mutableListOf<String>()) }
    val darkDefault = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(darkDefault) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text(text = "MaimaiProber-MultiPlatform")
                    },
                    actions = {
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
                    }
                )
                loginComposable()
                Column(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                ) {
                    Text(text = logLines.joinToString("\n"), style = MaterialTheme.typography.bodyMedium)
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
            onCloseRequest = ::exitApplication,
            title = "MaimaiProber-MultiPlatform"
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