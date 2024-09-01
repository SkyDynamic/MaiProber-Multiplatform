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
        SettingSwitchItem(
            modifier = Modifier.padding(top = 15.dp),
            text = "启用数据缓存",
            subtext = "启用后, 将会在查分后缓存所有成绩的记录\n开启后会导致查分速度变慢",
            initialValue = config.settings.useCache,
            onSettingChange = { newValue ->
                config.settings.useCache = newValue
                Config.write()
            }
        )

        SettingColorEditorItem(
            modifier = Modifier.padding(top = 15.dp),
            text = "舞萌DX B50 背景色调",
            subtext = "线性背景颜色色调",
            initialValue = config.settings.maimaiB50BackgroundColor,
            onSettingChange = { newValue ->
                config.settings.maimaiB50BackgroundColor = newValue
                Config.write()
            }
        )

        SettingSliderItem(
            modifier = Modifier.padding(top = 15.dp),
            text = "舞萌DX B50 背景相近颜色过渡区间",
            subtext = "启用后, 将使用自定义背景",
            minValue = 1F,
            maxValue = 20F,
            initialValue = config.settings.maimaiB50BackgroundLinearLayerCount.toFloat(),
            onSettingChange = { newValue ->
                config.settings.maimaiB50BackgroundLinearLayerCount = newValue.toInt()
                Config.write()
            }
        )
    }
}