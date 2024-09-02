package io.github.skydynamic.maiprober.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import java.nio.file.Path
import kotlin.math.absoluteValue

object DialogCompose {
    @Composable
    fun infoDialog(info: String, onRequest: () -> Unit) {
        Dialog(onDismissRequest = { onRequest() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = info,
                        modifier = Modifier.padding(16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { onRequest() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("确认")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun confirmDialog(info: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
        Dialog(onDismissRequest = { onCancel() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = info,
                        modifier = Modifier.padding(16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { onConfirm() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("确认")
                        }
                        TextButton(
                            onClick = { onCancel() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("取消")
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    fun imagePreviewDialog(image: Painter, onRequest: () -> Unit) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var imgSize: Size = Size.Unspecified
        val state = rememberTransformableState { zoomChange, _, _ ->
            scale = (zoomChange * scale) .coerceAtLeast(1f)
        }

        val minScale = 1f
        val maxScale = 5f

        Dialog(
            onDismissRequest = { onRequest() }
        ) {
            var cardSize by remember { mutableStateOf(Size.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        cardSize = Size(placeable.width.toFloat(), placeable.height.toFloat())
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                        .padding(5.dp)
                        .transformable(state = state)
                        .graphicsLayer {
                            imgSize = size
                            scaleX = scale.coerceIn(minScale, maxScale)
                            scaleY = scale.coerceIn(minScale, maxScale)
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            )
                        }
                        .onDrag { change ->
                            offset += change * scale
                            offset = calOffset(imgSize, scale, offset)
                        }
                        .onPointerEvent(PointerEventType.Scroll) { event ->
                            event.changes.forEach { change ->
                                val zoomFactor = 1.1f
                                if (change.scrollDelta.y > 0) {
                                    scale /= zoomFactor
                                } else {
                                    scale *= zoomFactor
                                }
                                scale = scale.coerceIn(minScale, maxScale)
                                offset = calOffset(imgSize, scale, offset)
                                change.consume()
                            }
                        }
                )
            }
        }
    }

    @Composable
    fun imageListChooseDialog(
        title: String,
        showImageNumOneLine: Int,
        imagePathList: List<Path>,
        initValue: Path,
        onRequest: (Path) -> Unit,
        onCancel: () -> Unit,
    ) {
        var selectedImageIndex by remember { mutableStateOf(imagePathList.indexOf(initValue)) }

        Dialog(
            onDismissRequest = { onCancel() },
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (selectedImageIndex != -1) onRequest(imagePathList[selectedImageIndex])
                                onCancel()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("确认")
                        }

                        Button(
                            onClick = { onCancel() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("取消")
                        }
                    }

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(),
                        columns = GridCells.Fixed(showImageNumOneLine),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(
                            imagePathList.size
                        ) {
                            val file = imagePathList[it].toFile()
                            val isChoose = imagePathList[selectedImageIndex] == imagePathList[it]
                            AsyncImage(
                                model = file,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        selectedImageIndex = it
                                    }.border(
                                        width = if (isChoose) 5.dp else 0.dp,
                                        color = if (isChoose) Color.Red else Color.Transparent
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun calOffset(
    imgSize: Size,
    scale: Float,
    offsetChanged: Offset,
    isInvalid: (Boolean) -> Unit = {}
): Offset {
    if (imgSize == Size.Unspecified) return Offset.Zero
    val px = imgSize.width * (scale - 1f) / 2f
    val py = imgSize.height * (scale - 1f) / 2f
    var np = offsetChanged
    val xDiff = np.x.absoluteValue - px
    val yDiff = np.y.absoluteValue - py
    if (xDiff > 0)
        np = np.copy(x = px * np.x.absoluteValue / np.x)
    if (yDiff > 0)
        np = np.copy(y = py * np.y.absoluteValue / np.y)
    isInvalid(xDiff > 0 && xDiff > yDiff)
    return np
}