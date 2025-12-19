package me.inory.log.core

import org.slf4j.ILoggerFactory
import org.slf4j.Logger

class RainyLoggerFactory : ILoggerFactory {
    override fun getLogger(name: String): Logger {
        val logLevel = LogConfig.getLevel(name)
        return RainyLogger(name, logLevel)
    }
}
