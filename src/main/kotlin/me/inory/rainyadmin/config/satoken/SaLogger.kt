package me.inory.rainyadmin.config.satoken

import cn.dev33.satoken.log.SaLog
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SaLogger: SaLog {
    val log = LoggerFactory.getLogger(SaLogger::class.java)
    override fun trace(str: String?, vararg args: Any?) {
        log.trace(str, *args)
    }

    override fun debug(str: String?, vararg args: Any?) {
        log.debug(str, *args)
    }

    override fun info(str: String?, vararg args: Any?) {
        log.info(str, *args)
    }

    override fun warn(str: String?, vararg args: Any?) {
        log.warn(str, *args)
    }

    override fun error(str: String?, vararg args: Any?) {
        log.error(str, *args)
    }

    override fun fatal(str: String?, vararg args: Any?) {
        log.error(str, *args)
    }
}