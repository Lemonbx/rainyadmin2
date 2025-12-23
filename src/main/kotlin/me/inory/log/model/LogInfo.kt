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
  * [isLoggingClass] .
 *
 * @since 1.3.4
 */
internal fun isLoggingClass(className: String): Boolean {
    return className.startsWith("org.slf4j") ||
            className.startsWith("org.apache.commons.logging") ||
            className.startsWith("me.inory.log") ||
            className == "me.inory.rainyadmin.config.satoken.SaLogger"||
            className.contains("SaTokenListenerForLog")||
            className.contains("LogAdapter")
}
