package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.ProberPlatform

interface ProberContext {

    fun sendNotification(title: String, content: String)
    fun requireConfig(): ConfigStorage
    fun pasteToClipboard(content: String)

    fun getProberPlatform():ProberPlatform
}