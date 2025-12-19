package me.inory.log.core

import me.inory.log.model.LogLevel
import org.slf4j.spi.LocationAwareLogger
import java.util.Properties

object LogConfig {
    private const val CONFIG_FILE = "rainy-log.properties"
    private const val PREFIX = "log.level."
    private const val ROOT = "root"

    private val properties = Properties()
    private val configMap = HashMap<String, Int>()
    
    init {
        loadConfig()
    }

    private fun loadConfig() {
        try {
            val loader = Thread.currentThread().contextClassLoader
            val stream = loader.getResourceAsStream(CONFIG_FILE)
            if (stream != null) {
                properties.load(stream)
                stream.close()
                parseProperties()
            } else {
                System.err.println("RainyLog: Could not find $CONFIG_FILE in classpath. Using defaults.")
            }
        } catch (e: Exception) {
            System.err.println("RainyLog: Error loading $CONFIG_FILE: ${e.message}")
        }
    }

    private fun parseProperties() {
        properties.stringPropertyNames().forEach { key ->
            if (key.startsWith(PREFIX)) {
                val loggerName = key.substring(PREFIX.length)
                val levelStr = properties.getProperty(key)
                val level = parseLevel(levelStr)
                configMap[loggerName] = level
            }
        }
    }

    private fun parseLevel(levelStr: String?): Int {
        return when (levelStr?.uppercase()) {
            "TRACE" -> LocationAwareLogger.TRACE_INT
            "DEBUG" -> LocationAwareLogger.DEBUG_INT
            "INFO" -> LocationAwareLogger.INFO_INT
            "WARN" -> LocationAwareLogger.WARN_INT
            "ERROR" -> LocationAwareLogger.ERROR_INT
            else -> LocationAwareLogger.INFO_INT
        }
    }

    fun getLevel(loggerName: String): Int {
        // 1. Check exact match
        if (configMap.containsKey(loggerName)) {
            return configMap[loggerName]!!
        }

        // 2. Check parents
        var tempName = loggerName
        while (tempName.contains(".")) {
            val index = tempName.lastIndexOf(".")
            tempName = tempName.substring(0, index)
            if (configMap.containsKey(tempName)) {
                return configMap[tempName]!!
            }
        }

        // 3. Return root or default
        return configMap[ROOT] ?: LocationAwareLogger.INFO_INT
    }
}
