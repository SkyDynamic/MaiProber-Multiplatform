package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.SettingManager

@Composable
fun SettingCompose(theme: ColorScheme) {
    val settingsManager = remember { SettingManager.instance }
    val cacheData = settingsManager.useCache.collectAsState()

    MaterialTheme(
        colorScheme = theme
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(15.dp))

            SettingItem(
                text = "启用数据缓存",
                subtext = "启用后, 将会在查分后缓存所有成绩的记录\n开启后会导致查分速度变慢",
                bindingState = cacheData,
                onSettingChange = { newValue ->
                    settingsManager.setUseCache(newValue)
                }
            )
        }
    }
}