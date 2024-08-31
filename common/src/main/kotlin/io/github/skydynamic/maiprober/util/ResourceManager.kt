package io.github.skydynamic.maiprober.util

import java.nio.file.Path
import kotlin.io.path.toPath

class ResourceManager {
    class MaimaiResources {
        companion object {
            // Rank
            val RANK_D = getResourceImage("maimai/pic/UI_GAM_Rank_D.png")
            val RANK_C = getResourceImage("maimai/pic/UI_GAM_Rank_C.png")
            val RANK_B = getResourceImage("maimai/pic/UI_GAM_Rank_B.png")
            val RANK_BB = getResourceImage("maimai/pic/UI_GAM_Rank_BB.png")
            val RANK_BBB = getResourceImage("maimai/pic/UI_GAM_Rank_BBB.png")
            val RANK_A = getResourceImage("maimai/pic/UI_GAM_Rank_A.png")
            val RANK_AA = getResourceImage("maimai/pic/UI_GAM_Rank_AA.png")
            val RANK_AAA = getResourceImage("maimai/pic/UI_GAM_Rank_AAA.png")
            val RANK_S = getResourceImage("maimai/pic/UI_GAM_Rank_S.png")
            val RANK_SP = getResourceImage("maimai/pic/UI_GAM_Rank_Sp.png")
            val RANK_SS = getResourceImage("maimai/pic/UI_GAM_Rank_SS.png")
            val RANK_SSP = getResourceImage("maimai/pic/UI_GAM_Rank_SSp.png")
            val RANK_SSS = getResourceImage("maimai/pic/UI_GAM_Rank_SSS.png")
            val RANK_SSSP = getResourceImage("maimai/pic/UI_GAM_Rank_SSSp.png")

            // Song Kind
            val DELUXE = getResourceImage("maimai/pic/UI_UPE_Infoicon_DeluxeMode.png")
            val STANDARD = getResourceImage("maimai/pic/UI_UPE_Infoicon_StandardMode.png")

            val BLAND = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_Blank.png")

            // Sync Play
            val SYNC = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_SYNC.png")
            val FS = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FS.png")
            val FSP = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FSp.png")
            val FDX = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FDX.png")
            val FXDP = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FDXp.png")

            // Special Clear
            val FC = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FC.png")
            val FCP = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_FCp.png")
            val AP = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_AP.png")
            val APP = getResourceImage("maimai/pic/UI_MSS_MBase_Icon_APp.png")

            // Number
            val ZERO = getResourceImage("maimai/pic/UI_NUM_Drating_0.png")
            val ONE = getResourceImage("maimai/pic/UI_NUM_Drating_1.png")
            val TWO = getResourceImage("maimai/pic/UI_NUM_Drating_2.png")
            val THREE = getResourceImage("maimai/pic/UI_NUM_Drating_3.png")
            val FOUR = getResourceImage("maimai/pic/UI_NUM_Drating_4.png")
            val FIVE = getResourceImage("maimai/pic/UI_NUM_Drating_5.png")
            val SIX = getResourceImage("maimai/pic/UI_NUM_Drating_6.png")
            val SEVEN = getResourceImage("maimai/pic/UI_NUM_Drating_7.png")
            val EIGHT = getResourceImage("maimai/pic/UI_NUM_Drating_8.png")
            val NINE = getResourceImage("maimai/pic/UI_NUM_Drating_9.png")

            // Bests Background
            val BESTS_BACKGROUND = getResourceImage("maimai/pic/UI_CMN_Shougou_Rainbow.png")
        }
    }

    companion object {
        @JvmStatic
        fun getResourceImage(path: String): Path {
            val res: Path? = this::class.java.classLoader.getResource(path)?.toURI()?.toPath()
            if (res != null) {
                return res
            }
            throw Exception("Resource not found: $path")
        }
    }
}