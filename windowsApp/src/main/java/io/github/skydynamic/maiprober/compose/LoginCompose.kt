package io.github.skydynamic.maiprober.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import kotlinx.coroutines.runBlocking

var currentSelectProberPlatform: ProberPlatform = ProberPlatform.DIVING_FISH

fun getProberPlatformSelectMenuHeight(count: Int) : Dp {
    return if (count <= 5) {
        55.dp * count
    } else {
        55.dp * 5
    }
}

fun verifyProberAccount(username: String, password: String) = runBlocking {
    val proberUtil: ProberUtil = currentSelectProberPlatform.factory()
    return@runBlocking proberUtil.validateProberAccount(username, password)
}

@Composable
fun proberPlatformSelectMenu() {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(ProberPlatform.DIVING_FISH) }
    val options = ProberPlatform.entries

    TextField(
        value = selectedOption.proberName,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { dropdownExpanded = true }) {
                Icon(Icons.Filled.ArrowDropDown, null)
            }
        },
        modifier = Modifier
            .width(300.dp)
            .height(55.dp),
        label = { Text("选择查分平台") }
    )

    DropdownMenu(
        modifier = Modifier.size(300.dp, getProberPlatformSelectMenuHeight(options.size)),
        expanded = dropdownExpanded,
        onDismissRequest = { dropdownExpanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(onClick = {
                selectedOption = option
                currentSelectProberPlatform = option
                dropdownExpanded = false
            }) {
                Text(option.proberName)
            }
        }
    }
}

@Preview
@Composable
fun loginComposable() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Column {
            proberPlatformSelectMenu()
        }

        Spacer(modifier = Modifier.height(25.dp))

        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier.size(300.dp, 55.dp),
            singleLine = true,
            label = {
                Text("账号")
            },
            leadingIcon = {
                Icon(Icons.Filled.Email, null)
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.size(300.dp, 55.dp),
            singleLine = true,
            label = {
                Text("密码")
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, null)
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        var openLoginFailedDialog by remember { mutableStateOf(false) }

        Button(
            onClick = {
                if (verifyProberAccount(username, password)) {
                    println("登录成功")
                } else {
                    openLoginFailedDialog = true
                }
            }
        ) {
            Text("登录")
        }

        if (openLoginFailedDialog) {
            AlertDialog(
                onDismissRequest = {
                    openLoginFailedDialog = false
                },
                title = {
                    Text(text = "登录失败")
                },
                text = {
                    Text(text = "账号或密码错误")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openLoginFailedDialog = false
                        }
                    ) {
                        Text("确定")
                    }
                }
            )
        }
    }
}