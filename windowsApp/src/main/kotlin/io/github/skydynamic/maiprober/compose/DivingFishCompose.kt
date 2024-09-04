package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.skydynamic.maiprober.AppPlatform
import io.github.skydynamic.maiprober.MaimaiProberMain
import io.github.skydynamic.maiprober.util.asIcon
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import io.github.skydynamic.windowsapp.generated.resources.Res
import io.github.skydynamic.windowsapp.generated.resources.eye_hidden_24px
import io.github.skydynamic.windowsapp.generated.resources.eye_show_24px
import io.github.skydynamic.windowsapp.generated.resources.rocket_icon_24px
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val maimaiProberMain = MaimaiProberMain()

object DivingFishViewModel : ViewModel() {
    var username by mutableStateOf(maimaiProberMain.getConfig().userName)
    var password by mutableStateOf(maimaiProberMain.getConfig().password)
    var passwordHidden by mutableStateOf(false)
    var saveUsernameAndPassword by mutableStateOf(true)
    var oauthUrl = mutableStateOf("")
    var gameType = mutableStateOf(false)
    var openAccountVerifyResultDialog by mutableStateOf(false)
    var accountVerifyResult by mutableStateOf("")
    var openCopySuccessDialog = mutableStateOf(false)
    var openUpdateScoreStartedDialog by mutableStateOf(false)
    var openUpdateScoreFinishedDialog by mutableStateOf(false)
    var settingUpInProgress = mutableStateOf(false)
}

@Composable
@OptIn(DelicateCoroutinesApi::class)
fun DivingFishCompose() {
    val viewModel = remember { DivingFishViewModel }
    val proberUtil = ProberPlatform.DIVING_FISH.factory
    val proxyRunning = mutableStateOf(maimaiProberMain.isEnable)
    var isUseThis by mutableStateOf(Config.configStorage.platform == ProberPlatform.DIVING_FISH)

    when {
        viewModel.openCopySuccessDialog.value -> {
            DialogCompose.infoDialog("已将Oauth链接复制到剪切板\n请复制到微信安全的聊天中并打开") {
                viewModel.openCopySuccessDialog.value = false
            }
        }

        viewModel.openAccountVerifyResultDialog -> {
            DialogCompose.infoDialog(viewModel.accountVerifyResult) {
                viewModel.openAccountVerifyResultDialog = false
            }
        }

        viewModel.openUpdateScoreStartedDialog -> {
            DialogCompose.infoDialog("更新成绩中, 请稍后") {
                viewModel.openUpdateScoreStartedDialog = false
            }
        }

        viewModel.openUpdateScoreFinishedDialog -> {
            DialogCompose.infoDialog("更新成绩完成") {
                viewModel.openUpdateScoreFinishedDialog = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.width(290.dp).height(50.dp).padding(5.dp),
            enabled = !isUseThis,
            onClick = {
                if (!isUseThis) {
                    Config.configStorage.platform = ProberPlatform.DIVING_FISH
                    Config.write()
                    isUseThis = true
                }
            }
        ) {
            Text(if (!isUseThis) "使用本查分器" else "已选用")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).padding(top = 15.dp),
        ) {
            Button(
                onClick = {
                    proxyRunning.value = !proxyRunning.value
                    viewModel.settingUpInProgress.value = true
                    if (proxyRunning.value) {
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val config = maimaiProberMain.getConfig()
                                proberUtil.updateStartedSignal.connect {
                                    viewModel.openUpdateScoreStartedDialog = true
                                }
                                proberUtil.updateFinishedSignal.connect {
                                    viewModel.openUpdateScoreFinishedDialog = true
                                }
                                config.userName = viewModel.username
                                config.password = viewModel.password
                                maimaiProberMain.start()
                                AppPlatform.setupSystemProxy(
                                    "127.0.0.1",
                                    maimaiProberMain.getConfig().proxyPort
                                )
                            } catch (e: Exception) {
                                viewModel.accountVerifyResult =
                                    "启动代理失败，请查看日志输出并寻求帮助\n${e.stackTraceToString()}"
                                viewModel.openAccountVerifyResultDialog = true
                                proxyRunning.value = false
                            }
                            viewModel.settingUpInProgress.value = false
                        }
                    } else {
                        GlobalScope.launch(Dispatchers.IO) {
                            if (!viewModel.saveUsernameAndPassword) {
                                maimaiProberMain.getConfig().userName = ""
                                maimaiProberMain.getConfig().password = ""
                            }
                            maimaiProberMain.saveConfig()
                            maimaiProberMain.stop()
                            AppPlatform.rollbackSystemProxy()
                            viewModel.settingUpInProgress.value = false
                        }
                    }
                },
                modifier = Modifier.width(290.dp).height(50.dp).padding(5.dp),
                enabled = !viewModel.settingUpInProgress.value
            ) {
                Icon(
                    Res.drawable.rocket_icon_24px.asIcon(),
                    "",
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    if (!proxyRunning.value) "开启代理" else "关闭代理"
                )
            }

            Button(
                onClick = {
                    AppPlatform.openWechat()
                },
                modifier = Modifier.width(290.dp).height(50.dp).padding(5.dp),
            ) {
                Icon(
                    Icons.Default.Create,
                    "",
                    modifier = Modifier.size(28.dp)
                )
                Text("打开微信")
            }
        }

        Button(
            onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    proberUtil.updateAccountInfo(viewModel.username, viewModel.password)
                }
                if (!viewModel.saveUsernameAndPassword) {
                    maimaiProberMain.getConfig().userName = ""
                    maimaiProberMain.getConfig().password = ""
                }
                maimaiProberMain.saveConfig()
            },
            modifier = Modifier.width(290.dp).height(50.dp).padding(top = 15.dp),
        ) {
            Icon(
                Icons.Default.Refresh,
                "",
                modifier = Modifier.size(28.dp)
            )
            Text("更新个人资料")
        }

        gameChooseTypeItem(
            viewModel.gameType,
            viewModel.oauthUrl
        )

        TextField(
            modifier = Modifier.padding(top = 15.dp),
            value = viewModel.username,
            onValueChange = {
                viewModel.username = it
            },
            leadingIcon = {
                Icon(Icons.Filled.Email, null)
            },
            singleLine = true,
            label = { Text("账号") }
        )

        TextField(
            modifier = Modifier.padding(top = 15.dp),
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        viewModel.passwordHidden = !viewModel.passwordHidden
                    }
                ) {
                    if (!viewModel.passwordHidden) Icon(
                        Res.drawable.eye_show_24px.asIcon(),
                        null,
                        modifier = Modifier.size(28.dp)
                    )
                    else Icon(
                        Res.drawable.eye_hidden_24px.asIcon(),
                        null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, null)
            },
            singleLine = true,
            label = { Text("密码") },
            visualTransformation = if (!viewModel.passwordHidden) PasswordVisualTransformation()
            else VisualTransformation.None
        )

        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = viewModel.saveUsernameAndPassword,
                onCheckedChange = { viewModel.saveUsernameAndPassword = it }
            )
            Text(
                "记住账号密码"
            )
        }

        generateOauthItem(
            Modifier,
            proxyRunning,
            viewModel.settingUpInProgress,
            viewModel.openCopySuccessDialog,
            viewModel.gameType,
            viewModel.oauthUrl
        )
    }

}