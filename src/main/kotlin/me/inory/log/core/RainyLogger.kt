package me.inory.log.core

import me.inory.log.util.LogUtil
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.LegacyAbstractLogger
import org.slf4j.spi.LocationAwareLogger

class RainyLogger(val logName: String, val currentLogLevel: Int) : LegacyAbstractLogger() {

    companion object {
        const val LOG_LEVEL_TRACE: Int = LocationAwareLogger.TRACE_INT
        const val LOG_LEVEL_DEBUG: Int = LocationAwareLogger.DEBUG_INT
        const val LOG_LEVEL_INFO: Int = LocationAwareLogger.INFO_INT
        const val LOG_LEVEL_WARN: Int = LocationAwareLogger.WARN_INT
        const val LOG_LEVEL_ERROR: Int = LocationAwareLogger.ERROR_INT
    }

    protected fun isLevelEnabled(logLevel: Int): Boolean {
        // log level are numerically ordered so can use simple numeric comparison
        return (logLevel >= currentLogLevel)
    }

    override fun isTraceEnabled(): Boolean = isLevelEnabled(LOG_LEVEL_TRACE)

    override fun isDebugEnabled(): Boolean = isLevelEnabled(LOG_LEVEL_DEBUG)

    override fun isInfoEnabled(): Boolean = isLevelEnabled(LOG_LEVEL_INFO)

    override fun isWarnEnabled(): Boolean = isLevelEnabled(LOG_LEVEL_WARN)

    override fun isErrorEnabled(): Boolean = isLevelEnabled(LOG_LEVEL_ERROR)

    override fun getFullyQualifiedCallerName(): String = this.javaClass.name

    override fun handleNormalizedLoggingCall(
        level: Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?
    ) {
        LogUtil.printLog(level, marker, messagePattern, arguments, throwable)
    }
}
