package me.inory.log.util

import me.inory.log.formatter.LineColorfulFormat
import me.inory.log.formatter.LogFormat
import me.inory.log.model.LogInfo
import me.inory.log.model.LogLevel
import me.inory.log.model.isLoggingClass
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter

object LogUtil {
    private val groupLogs: ThreadLocal<MutableList<PendingLog>?> = ThreadLocal()
    private val groupState: ThreadLocal<Boolean?> = ThreadLocal()
    private val groupName: ThreadLocal<String?> = ThreadLocal()

    var defaultLogFormat: LogFormat = LineColorfulFormat
    private const val TAG = "LogUtil"

    fun startGroup(name: String?) {
        if (groupState.get() == null) {
            groupState.set(true)
            groupLogs.set(mutableListOf())
            groupName.set(name)
        }
    }

    fun printLog(
        level: Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?
    ) {
        val pendingLog = PendingLog(
            System.currentTimeMillis(),
            level,
            marker,
            messagePattern,
            arguments,
            throwable,
            getStack(0)
        )
        if (groupState.get() == true) {
            groupLogs.get()?.add(pendingLog)
        } else {
            println(buildStr(pendingLog))
        }
    }

    fun stopGroup() {
        if (groupState.get() == null) {
            return
        }
        println(buildString {
            groupLogs.get()?.forEach {
                append(buildStr(it))
                append("\n")
            }
        })
        groupState.remove()
        groupLogs.remove()
        groupName.remove()
    }

    private fun buildStr(entry: PendingLog): String {
        return defaultLogFormat.format(buildLogInfo(entry))
    }

    private fun buildLogInfo(entry: PendingLog): LogInfo {
        val currentThread = Thread.currentThread()
        return LogInfo(
            currentThread.name,
            entry.stack,
            toLogLevel(entry.level),
            markerToTag(entry.marker),
            entry.time,
            MessageFormatter.basicArrayFormat(entry.messagePattern, entry.arguments),
            entry.throwable
        )
    }

    data class PendingLog(
        val time: Long,
        val level: Level?,
        val marker: Marker?,
        val messagePattern: String?,
        val arguments: Array<out Any?>?,
        val throwable: Throwable?,
        val stack: StackTraceElement?
    )

    private fun getStack(offset: Int): StackTraceElement? {
        return StackWalker.getInstance().walk { stream ->
            stream.filter { !isLoggingClass(it.className) }
                .skip(offset.toLong())
                .findFirst()
                .map { it.toStackTraceElement() }
                .orElse(null)
        }
    }

    private fun markerToTag(marker: Marker?): String {
        val append = groupName.get() ?: ""
        return if (marker == null) append
        else if (append.isEmpty()) marker.name
        else "$append|${marker.name}"
    }

    private fun toLogLevel(level: Level?): LogLevel {
        return when (level?.toInt()) {
            Level.TRACE.toInt() -> LogLevel.TRACE
            Level.DEBUG.toInt() -> LogLevel.DEBUG
            Level.INFO.toInt() -> LogLevel.INFO
            Level.WARN.toInt() -> LogLevel.WARN
            Level.ERROR.toInt() -> LogLevel.ERROR
            else -> LogLevel.INFO
        }
    }
}
