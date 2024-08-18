package io.github.skydynamic.maiprober.util

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.PatternLayout
import org.apache.log4j.spi.LoggingEvent

class MemoryAppender(val loggingCallback: (String) -> Unit) : AppenderSkeleton() {
    override fun append(loggingEvent: LoggingEvent) {
        val formatted = LAYOUT.format(loggingEvent)
        loggingCallback(formatted)
    }

    override fun close() {
    }

    override fun requiresLayout(): Boolean {
        return false
    }

    companion object {
        private val LAYOUT = PatternLayout("[%d{HH:mm:ss}] [%t/%p] (%c{1}) %m")
    }
}
