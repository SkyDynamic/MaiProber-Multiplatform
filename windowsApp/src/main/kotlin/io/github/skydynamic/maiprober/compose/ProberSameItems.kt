package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.ClipDataUtil
import io.github.skydynamic.maiprober.util.OauthTokenUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun gameChooseTypeItem(
    currentGameType: MutableState<Boolean>,
    currentOauthUrl: MutableState<String>
) {
    Row(
        modifier = Modifier.padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("舞萌DX  ")
        Switch(
            checked = currentGameType.value,
            onCheckedChange = {
                currentGameType.value = it
                currentOauthUrl.value = if (currentGameType.value) {
                    currentOauthUrl.value.replace("maimai-dx", "chunithm")
                } else {
                    currentOauthUrl.value.replace("chunithm", "maimai-dx")
                }
            }
        )
        Text("  中二节奏")
    }
}

@Composable
@OptIn(DelicateCoroutinesApi::class)
fun generateOauthItem(
    modifier: Modifier,
    proxyRunning: MutableState<Boolean>,
    settingUpInProgress: MutableState<Boolean>,
    openCopySuccessDialog: MutableState<Boolean>,
    gameType: MutableState<Boolean>,
    oauthUrl: MutableState<String>,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    val config = maimaiProberMain.getConfig()
                    val token = OauthTokenUtil.generateToken(
                        config.userName,
                        config.secretKey,
                        60 * 15
                    )
                    val type = if (!gameType.value) "maimai-dx" else "chunithm"
                    val url = "http://127.0.0.1:${config.proxyPort}/oauth/$type?token=$token"
                    oauthUrl.value = url
                }
            },
            enabled = proxyRunning.value,
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
                    if (proxyRunning.value && !settingUpInProgress.value) {
                        ClipDataUtil.copyToClipboard(oauthUrl.value)
                        openCopySuccessDialog.value = true
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
                            append(oauthUrl.value)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}