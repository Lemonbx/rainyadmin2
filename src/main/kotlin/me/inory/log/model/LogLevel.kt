package me.inory.log.model

sealed class LogLevel(val priority: Int) : Comparable<LogLevel> {
    object TRACE : LogLevel(2)
    object DEBUG : LogLevel(3)
    object INFO : LogLevel(4)
    object WARN : LogLevel(5)
    object ERROR : LogLevel(6)
    object ASSERT : LogLevel(7)

    override fun compareTo(other: LogLevel): Int = this.priority - other.priority

    override fun toString(): String = this::class.java.simpleName
}
