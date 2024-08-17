package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil


enum class ProberPlatform(val factory: () -> ProberUtil) {
    DIVING_FISH({ DivingFishProberUtil() })
}