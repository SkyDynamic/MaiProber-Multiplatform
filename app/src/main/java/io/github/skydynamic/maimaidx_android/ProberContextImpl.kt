package io.github.skydynamic.maimaidx_android

import android.content.Context
import io.github.skydynamic.maiprober.ProberContext
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.ProberPlatform

class ProberContextImpl : ProberContext {

    fun load(context: Context) {

    }

    fun updateConfig(context: Context, config: ConfigStorage) {

    }

    override fun sendNotification(title: String, content: String) {

    }

    override fun readConfig(): ConfigStorage {
        TODO("Not yet implemented")
    }

    override fun pasteToClipboard(content: String) {
        TODO("Not yet implemented")
    }

    override fun getProberPlatform(): ProberPlatform {
        TODO("Not yet implemented")
    }
}