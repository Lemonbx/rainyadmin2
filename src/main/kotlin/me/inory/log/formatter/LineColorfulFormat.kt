package me.inory.log.formatter

import me.inory.log.model.*
import me.inory.log.model.LogInfo
import me.inory.log.model.LogLevel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LineColorfulFormat : LogFormat {

    private val dateTimeFormatterWithMillis: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())

    // Define fixed widths for alignment
    private const val THREAD_WIDTH = 15
    private const val TAG_WIDTH = 20 // Adjusted if you have long tags
    private const val LOCATION_WIDTH = 60
    private const val MAX_CLASS_NAME_LENGTH = 30

    override fun format(logInfo: LogInfo): String {
        val time = dateTimeFormatterWithMillis.format(Instant.ofEpochMilli(logInfo.mTime))
        val headColor = logInfo.headColor()
        val levelInitial = logInfo.mLevel.toString().first()
        
        // Truncate or pad Tag and ThreadName
        val tag = logInfo.mTag.take(TAG_WIDTH)
        val threadName = logInfo.mThreadName.take(THREAD_WIDTH)
        
        // Format Header: [L|Tag|Thread]
        // We won't pad inside the brackets to keep it compact, but we could if needed.
        // The user complained about overall alignment. The biggest variance is usually the class name location.
        // Let's keep the header compact but consistent.
        
        val header = "[$levelInitial${if(tag.isNotBlank()) "|" else ""}$tag|$threadName]"

        val stackTrace = logInfo.mStackTrace
        val className = stackTrace?.className ?: "Unknown"
        val fileName = stackTrace?.fileName ?: "Unknown"
        val lineNumber = stackTrace?.lineNumber ?: 0
        
        val shortClassName = shortClassname(className)
        
        // Combine Class and Location: ShortClass(File:Line)
        val location = "$shortClassName($fileName:$lineNumber)"
        
        // Pad the location string to align the message start
        val paddedLocation = location.padEnd(LOCATION_WIDTH)

        // Using spaces instead of \t for reliable alignment
        return "$Cyan$time$Reset $headColor$header$Reset at $Blue$paddedLocation :$Reset ${logInfo.mContent}"
    }

    /**
     * Get the head color by [LogInfo.mLevel].
     *
     * @since 1.3.4
     */
    private fun LogInfo.headColor() = when (mLevel) {
        LogLevel.TRACE -> Gray
        LogLevel.DEBUG -> Blue
        LogLevel.INFO -> Green
        LogLevel.WARN -> Yellow
        LogLevel.ERROR -> Red
        LogLevel.ASSERT -> Purple
    }

    private fun shortClassname(name: String): String {
        if (name.length <= MAX_CLASS_NAME_LENGTH) {
            return name
        }
        val parts = name.split(".").toMutableList()
        var currentName = name

        // Iteratively shorten parts from left to right until length is acceptable
        for (i in 0 until parts.size - 1) { // Don't shorten the class name itself (last part)
            if (currentName.length <= MAX_CLASS_NAME_LENGTH) {
                return currentName
            }
            if (parts[i].isNotEmpty()) {
                parts[i] = parts[i].first().toString()
                currentName = parts.joinToString(".")
            }
        }
        
        // If still too long, truncate from the left
        if (currentName.length > MAX_CLASS_NAME_LENGTH) {
            return currentName.takeLast(MAX_CLASS_NAME_LENGTH)
        }
        return currentName
    }
}
