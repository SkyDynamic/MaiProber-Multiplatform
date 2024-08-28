package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.util.config.Config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

class SettingManager {
    private val _useCache = MutableStateFlow(Config.configStorage.settings.useCache)
    val useCache: StateFlow<Boolean> get() = _useCache

    fun setUseCache(value: Boolean) {
        _useCache.value = value
        Config.configStorage.settings.useCache = value
    }

    companion object {
        @JvmStatic
        val instance = SettingManager()
    }
}

@Serializable
data class Settings(var useCache: Boolean = false)