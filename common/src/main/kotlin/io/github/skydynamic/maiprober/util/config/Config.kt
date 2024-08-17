package io.github.skydynamic.maiprober.util.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.*

object Config {

    lateinit var configStorage: ConfigStorage
    private val configPath = Path("./config.json")
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun read() {
        try{
            if (configPath.exists()) {
                configStorage = json.decodeFromString(configPath.readText())
            } else {
                configStorage = ConfigStorage()
                write()
            }
        }catch (e:Exception){
            e.printStackTrace()
            write()
        }
    }

    fun write(){
        configPath.deleteIfExists()
        configPath.createFile()
        configPath.writeText(json.encodeToString(configStorage))
    }
}
