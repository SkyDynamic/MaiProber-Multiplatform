package io.github.skydynamic.maiprober.util.config

import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import kotlin.io.path.*
import kotlin.reflect.KProperty

object Config {

    lateinit var configStorage: ConfigStorage
    val settings
        get() = configStorage.settings
    val personalInfo
        get() = configStorage.personalInfo
    private val configPath = Path("./config.yaml")
    private val yaml = Yaml()

    fun read() {
        try{
            if (configPath.exists()) {
                configStorage = yaml.decodeFromString(configPath.readText())
            } else {
                configStorage = ConfigStorage()
                write()
            }
        }catch (e:Exception){
            e.printStackTrace()
            configStorage = ConfigStorage()
            write()
        }
    }

    fun write(){
        configPath.deleteIfExists()
        configPath.createFile()
        configPath.writeText(yaml.encodeToString(configStorage))
    }

    fun getValue(){

    }

    operator fun getValue(nothing: Any?, property: KProperty<*>): ConfigStorage {
        return configStorage
    }

}
