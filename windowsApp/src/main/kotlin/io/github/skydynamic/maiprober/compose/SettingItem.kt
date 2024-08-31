package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun SettingItem(
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
        Column(
            modifier = modifier.padding(horizontal = 15.dp)
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