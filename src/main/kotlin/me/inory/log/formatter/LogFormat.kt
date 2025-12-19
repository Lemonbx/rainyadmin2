package me.inory.log.formatter

import me.inory.log.model.LogInfo
import java.time.format.DateTimeFormatter
import java.time.ZoneId

interface LogFormat {
    /**
     * Convert [logInfo] to [String] according to a certain format.
     *
     * @since 1.3.4
     */
    fun format(logInfo: LogInfo): String

    companion object {
        /** @since 1.3.4 */
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
    }
}
