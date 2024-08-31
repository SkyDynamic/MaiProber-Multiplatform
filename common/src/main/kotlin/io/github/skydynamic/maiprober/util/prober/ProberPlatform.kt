package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.divingfish.DivingFishProberUtil
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProberPlatform(val proberName: String, val factory: ProberUtil) {
    @SerialName("diving-fish")
    DIVING_FISH("水鱼查分器", DivingFishProberUtil()),
}