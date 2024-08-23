package io.github.skydynamic.maiprober.terminalconsole.util

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.*
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.layout.PatternMatch
import org.apache.logging.log4j.core.layout.PatternSelector
import org.apache.logging.log4j.core.pattern.PatternFormatter
import org.apache.logging.log4j.util.PerformanceSensitive

@Plugin(name = "LoggerNamePatternSelector", category = Node.CATEGORY, elementType = PatternSelector.ELEMENT_TYPE)
@PerformanceSensitive("allocation")
open class LoggerNamePatternSelector protected constructor(
    defaultPattern: String, properties: Array<PatternMatch>,
    alwaysWriteExceptions: Boolean, disableAnsi: Boolean, noConsoleNoAnsi: Boolean, config: Configuration?
) :
    PatternSelector {
    private class LoggerNameSelector(private val name: String, private val formatters: Array<PatternFormatter>) {
        private val isPackage = name.endsWith(".")

        fun get(): Array<PatternFormatter> {
            return this.formatters
        }

        fun test(s: String): Boolean {
            return if (this.isPackage) s.startsWith(this.name) else (s == this.name)
        }
    }

    private val defaultFormatters: Array<PatternFormatter>
    private val formatters: MutableList<LoggerNameSelector> = ArrayList()

    init {
        val parser = PatternLayout.createPatternParser(config)
        this.defaultFormatters = parser.parse(defaultPattern, alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi)
            .toTypedArray()
        for (property in properties) {
            val formatters: Array<PatternFormatter> =
                parser.parse(property.pattern, alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi)
                    .toTypedArray()
            for (name in property.key.split(",").filter { it.isNotBlank() }) {
                this.formatters.add(LoggerNameSelector(name, formatters))
            }
        }
    }

    override fun getFormatters(event: LogEvent): Array<PatternFormatter> {
        val loggerName = event.loggerName
        if (loggerName != null) {
            for (selector in formatters) {
                if (selector.test(loggerName)) {
                    return selector.get()
                }
            }
        }

        return this.defaultFormatters
    }

    companion object {
        @JvmStatic
        @PluginFactory
        fun createSelector(
            @Required(message = "Default pattern is required") @PluginAttribute(value = "defaultPattern") defaultPattern: String,
            @PluginElement("PatternMatch") properties: Array<PatternMatch>,
            @PluginAttribute(value = "alwaysWriteExceptions", defaultBoolean = true) alwaysWriteExceptions: Boolean,
            @PluginAttribute("disableAnsi") disableAnsi: Boolean,
            @PluginAttribute("noConsoleNoAnsi") noConsoleNoAnsi: Boolean,
            @PluginConfiguration config: Configuration?
        ): LoggerNamePatternSelector {
            return LoggerNamePatternSelector(
                defaultPattern,
                properties,
                alwaysWriteExceptions,
                disableAnsi,
                noConsoleNoAnsi,
                config
            )
        }
    }
}
