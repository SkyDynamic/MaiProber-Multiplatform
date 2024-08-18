package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil


enum class ProberPlatform(val proberName: String, val factory: () -> ProberUtil) {
    @kotlinx.serialization.SerialName("diving-fish")
    DIVING_FISH("水鱼查分器", { DivingFishProberUtil() }),
}