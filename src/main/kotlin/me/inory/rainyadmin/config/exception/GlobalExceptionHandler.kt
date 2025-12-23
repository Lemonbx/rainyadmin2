package me.inory.rainyadmin.config.exception

import cn.dev33.satoken.exception.NotLoginException
import me.inory.rainyadmin.project.utils.servlet.R
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [NotLoginException::class])
    fun notLoginException(e: NotLoginException): R {
        e.printStackTrace()
        return R.r(R.NOAUTH, "登录失效")
    }

    @ExceptionHandler(value = [BusinessException::class])
    fun businessException(e: BusinessException): R {
        return R.r(e.code, e.msg)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): R {
        e.printStackTrace()
        return R.r(R.EXCEPTION, e.message)
    }
}