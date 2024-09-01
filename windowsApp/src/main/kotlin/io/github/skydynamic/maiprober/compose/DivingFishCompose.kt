package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.skydynamic.maiprober.AppPlatform
import io.github.skydynamic.maiprober.MaimaiProberMain
import io.github.skydynamic.maiprober.util.ClipDataUtil
import io.github.skydynamic.maiprober.util.OauthTokenUtil
import io.github.skydynamic.maiprober.util.asIcon
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
    var proxyRunning by mutableStateOf(false)
    var username by mutableStateOf(maimaiProberMain.getConfig().userName)
    var password by mutableStateOf(maimaiProberMain.getConfig().password)
    var passwordHidden by mutableStateOf(false)
    var saveUsernameAndPassword by mutableStateOf(true)
    var oauthUrl by mutableStateOf("")
    var gameType by mutableStateOf(false)
    var openAccountVerifyResultDialog by mutableStateOf(false)
    var accountVerifyResult by mutableStateOf("")
    var openCopySuccessDialog by mutableStateOf(false)
    var openUpdateScoreStartedDialog by mutableStateOf(false)
    var openUpdateScoreFinishedDialog by mutableStateOf(false)
    var settingUpInProgress by mutableStateOf(false)
}

@Composable
@OptIn(DelicateCoroutinesApi::class)
fun DivingFishCompose() {
    val viewModel = remember { DivingFishViewModel }

    when {
        viewModel.openCopySuccessDialog -> {
            DialogCompose.infoDialog("已将Oauth链接复制到剪切板\n请复制到微信安全的聊天中并打开") {
                viewModel.openCopySuccessDialog = false
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).padding(top = 15.dp),
        ) {
            Button(
                onClick = {
                    viewModel.proxyRunning = !viewModel.proxyRunning
                    viewModel.settingUpInProgress = true
                    if (viewModel.proxyRunning) {
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val config = maimaiProberMain.getConfig()
                                val proberUtil = maimaiProberMain.getConfig().platform.factory
                                proberUtil.updateStartedSignal.connect {
                                    viewModel.openUpdateScoreStartedDialog = true
                                }
                                proberUtil.updateFinishedSignal.connect {
                                    viewModel.openUpdateScoreFinishedDialog = true
                                }
                                if (proberUtil.validateProberAccount(viewModel.username, viewModel.password)) {
                                    config.userName = viewModel.username
                                    config.password = viewModel.password
                                    maimaiProberMain.start()
                                    AppPlatform.setupSystemProxy(
                                        "127.0.0.1",
                                        maimaiProberMain.getConfig().proxyPort
                                    )
                                } else {
                                    viewModel.accountVerifyResult =
                                        "登录失败, 可能的原因: \n1. 网络出现错误\n2.账号密码错误"
                                    viewModel.openAccountVerifyResultDialog = true
                                    viewModel.proxyRunning = false
                                }
                            } catch (e: Exception) {
                                viewModel.accountVerifyResult =
                                    "启动代理失败，请查看日志输出并寻求帮助\n${e.stackTraceToString()}"
                                viewModel.openAccountVerifyResultDialog = true
                                viewModel.proxyRunning = false
                            }
                            viewModel.settingUpInProgress = false
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
                            viewModel.settingUpInProgress = false
                        }
                    }
                },
                modifier = Modifier.width(290.dp).height(50.dp).padding(5.dp),
                enabled = !viewModel.settingUpInProgress
            ) {
                Icon(
                    Res.drawable.rocket_icon_24px.asIcon(),
                    "",
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    if (!viewModel.proxyRunning) "开启代理(先输入账号密码)" else "关闭代理"
                )
            }

            Button(
                onClick = {
                    AppPlatform.openWechat()
                }, // Only Windows
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

        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("舞萌DX  ")

            Switch(
                checked = viewModel.gameType,
                onCheckedChange = {
                    viewModel.gameType = it
                    viewModel.oauthUrl = if (viewModel.gameType) {
                        viewModel.oauthUrl.replace("maimai-dx", "chunithm")
                    } else {
                        viewModel.oauthUrl.replace("chunithm", "maimai-dx")
                    }
                }
            )

            Text("  中二节奏")
        }

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
                Icon(Icons.Filled.Email, null)
            },
            singleLine = true,
            label = { Text("密码") },
            visualTransformation = if (!viewModel.passwordHidden) PasswordVisualTransformation() else VisualTransformation.None
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

        Button(
            onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    val config = maimaiProberMain.getConfig()
                    val token = OauthTokenUtil.generateToken(
                        config.userName,
                        config.secretKey,
                        60 * 15
                    )
                    val type = if (!viewModel.gameType) "maimai-dx" else "chunithm"
                    val url = "http://127.0.0.1:${config.proxyPort}/oauth/$type?token=$token"
                    viewModel.oauthUrl = url
                }
            },
            enabled = viewModel.proxyRunning,
            modifier = Modifier.width(300.dp).height(50.dp).padding(top = 15.dp),
        ) {
            Icon(
                Icons.Default.Build,
                "",
                modifier = Modifier.size(28.dp)
            )
            Text(
                "生成新Oauth链接"
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable {
                    if (viewModel.proxyRunning && !viewModel.settingUpInProgress) {
                        ClipDataUtil.copyToClipboard(viewModel.oauthUrl)
                        viewModel.openCopySuccessDialog = true
                    }
                },
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.W900)
                    ) {
                        append("Oauth链接 - 点击复制")
                    }
                })

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.W900)
                        ) {
                            append(viewModel.oauthUrl)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}