package io.github.skydynamic.maiprober.util

import java.nio.file.Path
import kotlin.io.path.toPath

object ResourceManager {
    object MaimaiResources {
        // Rank
        val RANK_D = getResource("maimai/pic/UI_GAM_Rank_D.png")
        val RANK_C = getResource("maimai/pic/UI_GAM_Rank_C.png")
        val RANK_B = getResource("maimai/pic/UI_GAM_Rank_B.png")
        val RANK_BB = getResource("maimai/pic/UI_GAM_Rank_BB.png")
        val RANK_BBB = getResource("maimai/pic/UI_GAM_Rank_BBB.png")
        val RANK_A = getResource("maimai/pic/UI_GAM_Rank_A.png")
        val RANK_AA = getResource("maimai/pic/UI_GAM_Rank_AA.png")
        val RANK_AAA = getResource("maimai/pic/UI_GAM_Rank_AAA.png")
        val RANK_S = getResource("maimai/pic/UI_GAM_Rank_S.png")
        val RANK_SP = getResource("maimai/pic/UI_GAM_Rank_Sp.png")
        val RANK_SS = getResource("maimai/pic/UI_GAM_Rank_SS.png")
        val RANK_SSP = getResource("maimai/pic/UI_GAM_Rank_SSp.png")
        val RANK_SSS = getResource("maimai/pic/UI_GAM_Rank_SSS.png")
        val RANK_SSSP = getResource("maimai/pic/UI_GAM_Rank_SSSp.png")

        // Song Kind
        val DELUXE = getResource("maimai/pic/UI_UPE_Infoicon_DeluxeMode.png")
        val STANDARD = getResource("maimai/pic/UI_UPE_Infoicon_StandardMode.png")

        val BLAND = getResource("maimai/pic/UI_MSS_MBase_Icon_Blank.png")

        // Sync Play
        val SYNC = getResource("maimai/pic/UI_MSS_MBase_Icon_SYNC.png")
        val FS = getResource("maimai/pic/UI_MSS_MBase_Icon_FS.png")
        val FSP = getResource("maimai/pic/UI_MSS_MBase_Icon_FSp.png")
        val FDX = getResource("maimai/pic/UI_MSS_MBase_Icon_FDX.png")
        val FXDP = getResource("maimai/pic/UI_MSS_MBase_Icon_FDXp.png")

        // Special Clear
        val FC = getResource("maimai/pic/UI_MSS_MBase_Icon_FC.png")
        val FCP = getResource("maimai/pic/UI_MSS_MBase_Icon_FCp.png")
        val AP = getResource("maimai/pic/UI_MSS_MBase_Icon_AP.png")
        val APP = getResource("maimai/pic/UI_MSS_MBase_Icon_APp.png")

        // Number
        val ZERO = getResource("maimai/pic/UI_NUM_Drating_0.png")
        val ONE = getResource("maimai/pic/UI_NUM_Drating_1.png")
        val TWO = getResource("maimai/pic/UI_NUM_Drating_2.png")
        val THREE = getResource("maimai/pic/UI_NUM_Drating_3.png")
        val FOUR = getResource("maimai/pic/UI_NUM_Drating_4.png")
        val FIVE = getResource("maimai/pic/UI_NUM_Drating_5.png")
        val SIX = getResource("maimai/pic/UI_NUM_Drating_6.png")
        val SEVEN = getResource("maimai/pic/UI_NUM_Drating_7.png")
        val EIGHT = getResource("maimai/pic/UI_NUM_Drating_8.png")
        val NINE = getResource("maimai/pic/UI_NUM_Drating_9.png")

        // Bests Background
        val BESTS_BACKGROUND = getResource("maimai/pic/UI_CMN_Shougou_Rainbow.png")
    }

    @JvmStatic
    fun getResource(path: String): Path {
        val res: Path? = this::class.java.classLoader.getResource(path)?.toURI()?.toPath()
        if (res != null) {
            return res
        }
        throw Exception("Resource not found: $path")
    }
}