package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.divingfish.DivingFishProberUtil
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import io.github.skydynamic.maiprober.util.prober.lxns.LxnsProberUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProberPlatform(val proberName: String, val factory: ProberUtil) {
    @SerialName("diving-fish")
    DIVING_FISH("水鱼查分器", DivingFishProberUtil()),
    @SerialName("Lxns")
    LXNS("落雪查分器", LxnsProberUtil())
}