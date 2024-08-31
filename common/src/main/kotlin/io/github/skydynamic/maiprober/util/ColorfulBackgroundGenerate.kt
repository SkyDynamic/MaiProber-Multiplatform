package io.github.skydynamic.maiprober.util

import org.jetbrains.skia.*
import java.util.*

class ColorfulBackgroundGenerate {
    companion object {
        data class ColorGenerator(
            val hueStep: Int = 30,
            val lockSB: Boolean = true,
            val saturation: Float = 0.25f,
            val brightness: Float = 1f,
        )

        fun parseHexColor(hexColor: String): Int {
            val trimmedHexColor = hexColor.trim().substring(1)

            val red = Integer.parseInt(trimmedHexColor.substring(0, 2), 16)
            val green = Integer.parseInt(trimmedHexColor.substring(2, 4), 16)
            val blue = Integer.parseInt(trimmedHexColor.substring(4, 6), 16)

            return Color.makeRGB(red, green, blue)
        }

        fun makeCardBg(width: Int, height: Int, colors: List<Int>, linearLayerCount: Int): Image {
            val imageRect = Rect.makeXYWH(0f, 0f, width.toFloat(), height.toFloat())
            return Surface.makeRasterN32Premul(width, height).apply {
                canvas.apply {
                    drawRect(imageRect, Paint().apply {
                        shader = Shader.makeLinearGradient(
                            Point(imageRect.left, imageRect.top),
                            Point(imageRect.right, imageRect.bottom),
                            generateLinearGradient(colors, linearLayerCount)
                        )
                    })
                }
            }.makeImageSnapshot()
        }

        private fun generateLinearGradient(colors: List<Int>, linearLayerCount: Int): IntArray {
            val colorGenerator = ColorGenerator()
            return if (colors.size == 1) {
                val hsb = rgb2hsb(Color.getR(colors[0]), Color.getG(colors[0]), Color.getB(colors[0]))
                if (colorGenerator.lockSB) {
                    hsb[1] = colorGenerator.saturation
                    hsb[2] = colorGenerator.brightness
                }
                val linearLayerStep = colorGenerator.hueStep
                val llc = if (linearLayerCount % 2 == 0) linearLayerCount + 1 else linearLayerCount
                val ia = IntArray(llc)
                hsb[0] = (hsb[0] + linearLayerCount / 2 * linearLayerStep) % 360
                repeat(llc) {
                    val c = hsb2rgb(hsb[0], hsb[1], hsb[2])
                    ia[it] = Color.makeRGB(c[0], c[1], c[2])
                    hsb[0] = if (hsb[0] - linearLayerStep < 0) hsb[0] + 360 - linearLayerStep else hsb[0] - linearLayerStep
                }
                ia
            } else {
                val llc = colors.size
                val ia = IntArray(llc)
                repeat(llc) {
                    val hsb = rgb2hsb(Color.getR(colors[it]), Color.getG(colors[it]), Color.getB(colors[it]))
                    if (colorGenerator.lockSB) {
                        hsb[1] = colorGenerator.saturation
                        hsb[2] = colorGenerator.brightness
                    }
                    val c = hsb2rgb(hsb[0], hsb[1], hsb[2])
                    ia[it] = Color.makeRGB(c[0], c[1], c[2])
                }
                ia
            }
        }

        private fun rgb2hsb(rgbR: Int, rgbG: Int, rgbB: Int): FloatArray {

            val rgb = intArrayOf(rgbR, rgbG, rgbB)
            Arrays.sort(rgb)
            val max = rgb[2]
            val min = rgb[0]
            val hsbB = max / 255.0f
            val hsbS: Float = if (max == 0) 0f else (max - min) / max.toFloat()
            var hsbH = 0f
            if (max == rgbR && rgbG >= rgbB) {
                hsbH = (rgbG - rgbB) * 60f / (max - min) + 0
            } else if (max == rgbR && rgbG < rgbB) {
                hsbH = (rgbG - rgbB) * 60f / (max - min) + 360
            } else if (max == rgbG) {
                hsbH = (rgbB - rgbR) * 60f / (max - min) + 120
            } else if (max == rgbB) {
                hsbH = (rgbR - rgbG) * 60f / (max - min) + 240
            }
            return floatArrayOf(hsbH, hsbS, hsbB)
        }

        private fun hsb2rgb(h: Float, s: Float, v: Float): IntArray {
            var r = 0f
            var g = 0f
            var b = 0f
            val i = (h / 60 % 6).toInt()
            val f = h / 60 - i
            val p = v * (1 - s)
            val q = v * (1 - f * s)
            val t = v * (1 - (1 - f) * s)
            when (i) {
                0 -> {
                    r = v
                    g = t
                    b = p
                }

                1 -> {
                    r = q
                    g = v
                    b = p
                }

                2 -> {
                    r = p
                    g = v
                    b = t
                }

                3 -> {
                    r = p
                    g = q
                    b = v
                }

                4 -> {
                    r = t
                    g = p
                    b = v
                }

                5 -> {
                    r = v
                    g = p
                    b = q
                }

                else -> {}
            }
            return intArrayOf((r * 255.0).toInt(), (g * 255.0).toInt(), (b * 255.0).toInt())
        }

        fun Color.makeRGB(hex: String): Int {
            require(hex.startsWith("#")) { "Hex format error: $hex" }
            require(hex.length == 7 || hex.length == 9) { "Hex length error: $hex" }
            return when (hex.length) {
                7 -> {
                    makeRGB(
                        Integer.valueOf(hex.substring(1, 3), 16),
                        Integer.valueOf(hex.substring(3, 5), 16),
                        Integer.valueOf(hex.substring(5), 16)
                    )
                }

                9 -> {
                    makeARGB(
                        Integer.valueOf(hex.substring(1, 3), 16),
                        Integer.valueOf(hex.substring(3, 5), 16),
                        Integer.valueOf(hex.substring(5, 7), 16),
                        Integer.valueOf(hex.substring(7), 16)
                    )
                }

                else -> {
                    WHITE
                }
            }
        }
    }
}