package me.inory.rainyadmin.config

import cn.dev33.satoken.`fun`.strategy.SaGetAnnotationFunction
import cn.dev33.satoken.strategy.SaAnnotationStrategy
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.AnnotatedElement

@Configuration
class SaAnnotationConfiguration {
    @PostConstruct
    fun rewriteSaStrategy() {
        SaAnnotationStrategy.instance.getAnnotation =
            SaGetAnnotationFunction { element, annotationClass ->
                AnnotatedElementUtils.getMergedAnnotation(
                    element!!,
                    annotationClass
                )
            }
    }
}