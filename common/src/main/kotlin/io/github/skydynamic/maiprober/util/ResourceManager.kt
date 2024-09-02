package io.github.skydynamic.maiprober.util

import org.jetbrains.skia.Typeface
import org.jetbrains.skia.makeFromFile
import org.jetbrains.skia.paragraph.TypefaceFontProvider
import java.nio.file.Path
import kotlin.io.path.toPath

object ResourceManager {
    object MaimaiResources {
        object Fonts {
            private val fontProvider = TypefaceFontProvider()

            val ADOBE_SIMHEI = fontProvider.registerTypeface(
                Typeface.makeFromFile(getResource("maimai/fonts/adobe_simhei.otf").toString(), 0)
            )

            val MSYH = fontProvider.registerTypeface(
                Typeface.makeFromFile(getResource("maimai/fonts/msyh.ttc").toString(), 0)
            )
        }
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

        // Bests Background
        val BESTS_BACKGROUND = getResource("maimai/pic/UI_CMN_Shougou_Rainbow.png")

        // Number
        fun getNumberIconWihmNumber(number: Int) : Path {
            if (number in 0..9) {
                return getResource("maimai/pic/UI_NUM_Drating_$number.png")
            } else {
                throw Exception("Number must be in 0..9")
            }
        }

        // Dan icon
        fun getDanIconWithIndex(index: Int) : Path {
            return getResource("maimai/pic/dan/$index.png")
        }

        // Dx Rating icon
        fun getDXRatingIconWithRating(score: Int) : Path {
            val index = when (score) {
                in 0..999 -> "01"
                in 1000..1999 -> "02"
                in 2000..3999 -> "03"
                in 4000..6999 -> "04"
                in 7000..9999 -> "05"
                in 10000..11999 -> "06"
                in 12000..12999 -> "07"
                in 13000..13999 -> "08"
                in 14000..14999 -> "09"
                in 15000..16999 -> "10"
                else -> "01"
            }
            return getResource("maimai/pic/UI_CMN_DXRating_S_$index.png")
        }
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