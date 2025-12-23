package me.inory.rainyadmin.config

import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.stp.StpUtil
import me.inory.log.interceptor.RainyLoggerInterceptor
import me.inory.rainyadmin.project.utils.STPADMIN
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MvcConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RainyLoggerInterceptor()).addPathPatterns("/**")
        registry.addInterceptor(SaInterceptor {
            STPADMIN.checkLogin()
        }).addPathPatterns("/a/**").excludePathPatterns("/a/auth/login")
    }
}