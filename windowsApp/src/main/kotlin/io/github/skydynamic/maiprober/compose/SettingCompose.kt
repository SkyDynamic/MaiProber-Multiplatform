package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage

@Composable
fun SettingCompose() {
    val config: ConfigStorage by Config

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SettingItem(
            modifier = Modifier.padding(top = 15.dp),
            text = "启用数据缓存",
            subtext = "启用后, 将会在查分后缓存所有成绩的记录\n开启后会导致查分速度变慢",
            initialValue = config.settings.useCache,
            onSettingChange = { newValue ->
                config.settings.useCache = newValue
                Config.write()
            }
        )
    }

}