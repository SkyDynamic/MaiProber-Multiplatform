package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun colorPickerDialog(
    initialColor: String = "#000000",
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initColor = hexToRgb(initialColor)
    var red by remember { mutableStateOf(initColor.r) }
    var green by remember { mutableStateOf(initColor.g) }
    var blue by remember { mutableStateOf(initColor.b) }

    val hexColor = remember { mutableStateOf(initialColor) }

    val colorPreview = remember { mutableStateOf(Color(red, green, blue)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "颜色选择器")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SliderRow("Red", red.toFloat(), 0f, 255f) { red = it.toInt() }
                SliderRow("Green", green.toFloat(), 0f, 255f) { green = it.toInt() }
                SliderRow("Blue", blue.toFloat(), 0f, 255f) { blue = it.toInt() }

                Text(
                    text = "Hex: ${hexColor.value}",
                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(colorPreview.value)
                        .align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(hexColor.value)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("选择")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    LaunchedEffect(red, green, blue) {
        val r = red
        val g = green
        val b = blue

        val hexR = "%02X".format(r)
        val hexG = "%02X".format(g)
        val hexB = "%02X".format(b)

        hexColor.value = "#$hexR$hexG$hexB"
        colorPreview.value = Color(r, g, b)
    }
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "%.0f".format(value),
        )

        Slider(
            value = value,
            valueRange = minValue..maxValue,
            onValueChange = onValueChange,
            modifier = Modifier.width(200.dp).padding(start = 8.dp)
        )
    }
}

object ColorUtil {
    data class Color(val r: Int, val g: Int, val b: Int, val a: Int = 1)
}

fun hexToRgb(hex: String): ColorUtil.Color {
    return when (hex.length) {
        7 -> {
            ColorUtil.Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5), 16)
            )
        }

        9 -> {
            ColorUtil.Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16),
                Integer.valueOf(hex.substring(7), 16)
            )
        }

        else -> {
            ColorUtil.Color(0, 0, 0, 1)
        }
    }
}
