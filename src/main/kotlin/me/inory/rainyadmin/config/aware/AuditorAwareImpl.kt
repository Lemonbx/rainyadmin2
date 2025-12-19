package me.inory.rainyadmin.config.aware

import cn.dev33.satoken.stp.StpUtil
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class AuditorAwareImpl : AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> {
        if (StpUtil.isLogin()) {
            return Optional.of(StpUtil.getLoginIdAsLong())
        }
        return Optional.empty()
    }
}