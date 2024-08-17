package io.github.skydynamic.maiprober.util.config

import io.github.skydynamic.maiprober.Constant
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class MPConfig {
    private val lock = Any()
    private val configPath: Path = Paths.get(".")
    private val gson = Constant.gson
    private var configStorage: ConfigStorage? = null
    private var path: File = configPath.toFile()
    private var config: File = configPath.resolve("config.json").toFile()

    fun load() {
        synchronized(lock) {
            try {
                if (!path.exists() || !path.isDirectory) {
                    path.mkdirs()
                }
                if (!config.exists()) {
                    saveModifiedConfig(ConfigStorage.DEFAULT)
                }
                val reader = FileReader(config)
                val result = gson.fromJson(reader, ConfigStorage::class.java)
                this.configStorage = fixFields(result, ConfigStorage.DEFAULT)
                saveModifiedConfig(this.configStorage)
                reader.close()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    private fun saveModifiedConfig(c: ConfigStorage?) {
        synchronized(lock) {
            try {
                if (config.exists()) config.delete()
                if (!config.exists()) config.createNewFile()
                val writer = FileWriter(config)
                val fixConfig = fixFields(c, ConfigStorage.DEFAULT)
                gson.toJson(fixConfig, writer)
                writer.close()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    private fun fixFields(t: ConfigStorage?, defaultVal: ConfigStorage): ConfigStorage {
        if (t == null) {
            throw NullPointerException()
        }
        if (t == defaultVal) {
            return t
        }
        try {
            val clazz: Class<*> = t.javaClass
            for (declaredField in clazz.declaredFields) {
                if (Arrays.stream(declaredField.declaredAnnotations)
                        .anyMatch { it: Annotation -> it.annotationClass == Ignore::class.java }
                ) continue
                declaredField.isAccessible = true
                val value = declaredField[t]
                val dv = declaredField[defaultVal]
                if (value == null) {
                    declaredField[t] = dv
                }
            }
            return t
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val webPort: Int
        get() {
            synchronized(lock) {
                return configStorage?.webPort ?: 8081
            }
        }

    val proxyPort: Int
        get() {
            synchronized(lock) {
                return configStorage?.proxyPort ?: 2560
            }
        }

    val useSsl: Boolean
        get() {
            synchronized(lock) {
                return configStorage?.useSsl ?: false
            }
        }

    val platform: String?
        get() {
            synchronized(lock) {
                return configStorage?.platform
            }
        }

    val userName: String
        get() {
            synchronized(lock) {
                return configStorage?.username ?: ""
            }
        }

    val password: String
        get() {
            synchronized(lock) {
                return configStorage?.password ?: ""
            }
        }
}
