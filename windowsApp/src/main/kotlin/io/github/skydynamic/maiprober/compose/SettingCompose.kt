package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.compose.setting.*
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.score.MaimaiDan

@Composable
fun SettingCompose() {
    val config: ConfigStorage by Config

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SettingItemGroup(
            title = "查分设置",
            modifier = Modifier.fillMaxSize().padding(top = 15.dp)
        ) {
            SettingSwitchItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "启用数据缓存",
                subtext = "启用后, 将会在查分后缓存所有成绩的记录\n开启后会导致查分速度变慢",
                initialValue = config.settings.useCache,
                onSettingChange = { newValue ->
                    config.settings.useCache = newValue
                    Config.write()
                }
            )
        }

        SettingItemGroup(
            title = "查分器Token设置",
            modifier = Modifier.fillMaxSize().padding(top = 15.dp)
        ) {
            SettingTextFiledItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "水鱼查分器Token",
                subtext = "水鱼查分器Token, 用于获取水鱼个人信息 / 游玩记录",
                initialValue = config.token.divingFish,
                onSettingChange = { newValue ->
                    config.token.divingFish = newValue
                    Config.write()
                }
            )

            SettingTextFiledItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "LXNS Token",
                subtext = "LXNS Token, 用于获取LXNS个人信息 / 游玩记录",
                initialValue = config.token.lxns,
                onSettingChange = { newValue ->
                    config.token.lxns = newValue
                    Config.write()
                }
            )
        }

        SettingItemGroup(
            title = "个人信息设置",
            modifier = Modifier.fillMaxSize().padding(top = 15.dp)
        ) {
            SettingTextFiledItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "用户名",
                subtext = "B50中显示在的昵称",
                initialValue = config.personalInfo.name,
                onSettingChange = { newValue ->
                    config.personalInfo.name = newValue
                    Config.write()
                }
            )

            SettingTextFiledItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "自定义称号",
                subtext = "B50中显示在昵称下的称号(太长可能会被阶段)",
                initialValue = config.personalInfo.maimaiTitle,
                onSettingChange = { newValue ->
                    config.personalInfo.maimaiTitle = newValue
                    Config.write()
                }
            )

            SettingDropdownMenuItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "舞萌DX段位",
                subtext = "B50中现实的段位",
                initialValue = config.personalInfo.maimaiDan,
                itemList = MaimaiDan.entries,
                onSettingChange = { newValue ->
                    config.personalInfo.maimaiDan = newValue
                    Config.write()
                }
            )
        }

        SettingItemGroup(
            title = "舞萌DX B50 渲染设置",
            modifier = Modifier.fillMaxSize().padding(top = 15.dp)
        ) {
            SettingColorEditorItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "舞萌DX B50 背景色调",
                subtext = "线性背景颜色色调",
                initialValue = config.settings.maimaiB50BackgroundColor,
                onSettingChange = { newValue ->
                    config.settings.maimaiB50BackgroundColor = newValue
                    Config.write()
                }
            )

            SettingSliderItem(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                text = "舞萌DX B50 背景相近颜色过渡区间",
                subtext = "背景颜色的线性过渡区间",
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
}