package io.github.skydynamic.maiprober

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.skydynamic.maiprober.compose.loginComposable
import io.github.skydynamic.windowsapp.generated.resources.Res
import io.github.skydynamic.windowsapp.generated.resources.dark_mode_24px
import io.github.skydynamic.windowsapp.generated.resources.light_mode_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainComposable() {
    val darkDefault = isSystemInDarkTheme()
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
    MaterialTheme(
        colorScheme = theme
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
            }
        }
    }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "MaimaiProber-MultiPlatform") {
        mainComposable()
    }
}