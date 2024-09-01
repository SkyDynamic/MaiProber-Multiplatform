package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiprober.util.asIcon
import io.github.skydynamic.windowsapp.generated.resources.Res
import io.github.skydynamic.windowsapp.generated.resources.color_palette_24px

@Composable
fun SettingSliderItem(
    modifier: Modifier = Modifier,
    text: String,
    subtext: String,
    minValue: Float,
    maxValue: Float,
    initialValue: Float,
    onSettingChange: (Float) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth().height(80.dp).padding(horizontal = 15.dp)
    ) {
        SettingTextItem(text = text, subtext = subtext)

        Spacer(Modifier.weight(1f))

        if (maxValue < minValue) {
            throw IllegalArgumentException("maxValue must be greater than minValue")
        }

        Column {
            Slider(
                value = value,
                valueRange = minValue..maxValue,
                onValueChange = {
                    value = it
                    onSettingChange(it)
                },
                modifier = Modifier.width(200.dp),
            )
            Text(
                text = value.toInt().toString(),
                modifier = Modifier.width(190.dp).offset(x = (200 * ((value - 1) / maxValue)).dp)
            )
        }
    }
}

@Composable
fun SettingColorEditorItem(
    modifier: Modifier = Modifier,
    text: String,
    subtext: String,
    initialValue: String,
    onSettingChange: (String) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }
    var openColorPickerDialog by remember { mutableStateOf(false) }

    when {
        openColorPickerDialog -> colorPickerDialog(
            initialColor = value,
            onColorSelected = {
                value = it
                onSettingChange(it)
            },
            onDismiss = { openColorPickerDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth().height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth().height(80.dp).padding(horizontal = 15.dp)
        ) {
            SettingTextItem(text = text, subtext = subtext)

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = {
                    openColorPickerDialog = true
                },
                modifier = Modifier.padding(15.dp)
            ) {
                Icon(
                    Res.drawable.color_palette_24px.asIcon(),
                    null
                )
            }

            TextField(
                value = value,
                onValueChange = {
                    value = it
                    onSettingChange(it)
                },
                modifier = Modifier.width(200.dp),
            )
        }
    }
}

@Composable
fun SettingTextFiledItem(
    modifier: Modifier = Modifier,
    text: String,
    subtext: String,
    initialValue: String,
    onSettingChange: (String) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth().height(80.dp).padding(horizontal = 15.dp)
    ) {
        SettingTextItem(text = text, subtext = subtext)

        Spacer(Modifier.weight(1f))

        TextField(
            value = value,
            onValueChange = {
                value = it
                onSettingChange(it)
            },
            modifier = Modifier.width(200.dp),
        )
    }
}

@Composable
fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    text: String,
    subtext: String,
    initialValue: Boolean,
    onSettingChange: (Boolean) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth().height(80.dp).padding(horizontal = 15.dp)
    ) {
        SettingTextItem(text = text, subtext = subtext)

        Spacer(Modifier.weight(1f))

        Switch(
            checked = value,
            onCheckedChange = {
                value = it
                onSettingChange(it)
            }
        )
    }
}

@Composable
fun SettingTextItem(
    text: String,
    subtext: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(fontWeight = FontWeight.W900)
                ) {
                    append(text)
                }
            }
        )
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodySmall
        )
    }
}