package me.inory.rainyadmin

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RainyAdminApplication
val logger = LoggerFactory.getLogger(RainyAdminApplication::class.java)
fun main(args: Array<String>) {
    runApplication<RainyAdminApplication>(*args)
    logger.info("QAQ")
}
