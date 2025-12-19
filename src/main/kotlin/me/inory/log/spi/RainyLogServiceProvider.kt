package me.inory.log.spi

import me.inory.log.core.RainyLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

class RainyLogServiceProvider: SLF4JServiceProvider {
    private val aloggerFactory: ILoggerFactory = RainyLoggerFactory()
    private val amdcAdapter: MDCAdapter = NOPMDCAdapter()
    private val amarkerFactory: IMarkerFactory = BasicMarkerFactory()


    override fun getLoggerFactory(): ILoggerFactory {
        return aloggerFactory
    }

    override fun getMarkerFactory(): IMarkerFactory {
        return amarkerFactory
    }

    override fun getMDCAdapter(): MDCAdapter {
        return amdcAdapter
    }

    override fun getRequestedApiVersion(): String {
        return "2.0.16"
    }

    override fun initialize() {
        // Initialization is done during property declaration
    }
}
