package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil


enum class ProberPlatform(val factory: () -> ProberUtil) {
    @kotlinx.serialization.SerialName("diving-fish")
    DIVING_FISH({ DivingFishProberUtil() })
}