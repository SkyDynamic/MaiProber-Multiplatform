package io.github.skydynamic.maiprober.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class ClipDataUtil {
    companion object {
        @JvmStatic
        fun copyToClipboard(text: String) {
            val stringSelection = StringSelection(text)
            val clipboard = Toolkit.getDefaultToolkit()

            clipboard.systemClipboard.setContents(stringSelection, null)
        }
    }
}