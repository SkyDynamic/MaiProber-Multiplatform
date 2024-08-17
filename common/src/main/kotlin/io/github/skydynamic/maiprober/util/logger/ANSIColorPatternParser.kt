package io.github.skydynamic.maiprober.util.logger

import org.apache.log4j.helpers.PatternConverter
import org.apache.log4j.helpers.PatternParser
import org.apache.log4j.spi.LoggingEvent

class ANSIColorPatternParser(pattern: String?) : PatternParser(pattern) {
    override fun finalizeConverter(c: Char) {
        var pc: PatternConverter? = null
        when (c) {
            'p' -> pc = ANSIColorLevelPatternConverter()
            else -> super.finalizeConverter(c)
        }
        if (pc != null) {
            addConverter(pc)
        }
    }

    private inner class ANSIColorLevelPatternConverter : PatternConverter() {
        override fun convert(event: LoggingEvent): String {
            val level = event.getLevel().toString()
            var color = ""
            when (level) {
                "DEBUG" -> color = PREFIX + "34" + SUFFIX // Blue
                "INFO" -> color = PREFIX + "32" + SUFFIX // Green
                "WARN" -> color = PREFIX + "33" + SUFFIX // Yellow
                "ERROR" -> color = PREFIX + "31" + SUFFIX // Red
                "FATAL" -> color = PREFIX + "35" + SUFFIX // Magenta
            }
            return color + level + END_COLOR
        }
    }

    companion object {
        private const val PREFIX = "\u001b["
        private const val SUFFIX = "m"
        private const val END_COLOR = PREFIX + "0" + SUFFIX
    }
}
