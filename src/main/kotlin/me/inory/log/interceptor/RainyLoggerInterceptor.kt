package me.inory.log.interceptor

import jakarta.annotation.Nullable
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.inory.log.util.LogUtil
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

class RainyLoggerInterceptor : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestId = UUID.randomUUID().toString()
        LogUtil.startGroup(requestId)
        return true
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable ex: java.lang.Exception?,
    ) {
        LogUtil.stopGroup()
    }
}
