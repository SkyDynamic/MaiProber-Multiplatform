package io.github.skydynamic.maiprober.util.config

class ConfigStorage(
    val webPort : Int,
    val proxyPort: Int,
    val useSsl: Boolean,
    val platform: String,
    val username: String,
    val password: String
) {
    companion object {
        @Ignore
        val DEFAULT: ConfigStorage =
            ConfigStorage(8081, 2560, false, "diving-fish", "", "")
    }
}
