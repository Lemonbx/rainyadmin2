package me.inory.log.model

data class LogInfo(
    val mThreadName: String,
    val mStackTrace: StackTraceElement?,
    val mLevel: LogLevel,
    val mTag: String,
    val mTime: Long,
    val mContent: String,
    val mThrowable: Throwable? = null
) {
    val mTraceLength = mStackTrace.toString().length

    /** @since 0.5.3 */
    override fun toString(): String {
        return """
            (Level:$mLevel 
            Tag:$mTag
            Time:$mTime
            ThreadName:$mThreadName
            StackTrace:$mStackTrace
            Content:$mContent
            Exception:${mThrowable?.stackTraceToString()})
            """.trimIndent()
    }

}

/**
 * [getStackOffset] .
 *
 * @since 1.3.4
 */
internal fun getStackOffset(trace: Array<StackTraceElement>): Int {
    var index = -1
    for (i in trace.indices) {
        val className = trace[i].className
        // Check if the class is a known logging framework class
        if (isLoggingClass(className)) {
            index = i
        } else if (index != -1) {
            // If we found a logging class previously, and this one isn't, 
            // then this (or a previous one) was the last logging frame.
            // But we want the last *contiguous* block or just the last occurrence?
            // Usually we want the last occurrence of any logging infrastructure.
        }
    }
    return index.coerceAtLeast(0)
}

private fun isLoggingClass(className: String): Boolean {
    return className.startsWith("org.slf4j") ||
            className.startsWith("org.apache.commons.logging") ||
            className.startsWith("me.inory.log") ||
            className == "me.inory.rainyadmin.config.satoken.SaLogger"||
            className.contains("SaTokenListenerForLog")||
            className.contains("LogAdapter")
}
