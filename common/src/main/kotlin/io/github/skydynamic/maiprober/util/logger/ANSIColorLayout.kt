package io.github.skydynamic.maiprober.util.logger

import org.apache.log4j.PatternLayout
import org.apache.log4j.helpers.PatternParser

class ANSIColorLayout : PatternLayout() {
    override fun createPatternParser(pattern: String?): PatternParser {
        return ANSIColorPatternParser(pattern)
    }
}