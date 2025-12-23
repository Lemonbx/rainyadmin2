package me.inory.rainyadmin.config.satoken

import cn.dev33.satoken.stp.StpInterface
import me.inory.rainyadmin.project.repo.SysUserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StpInterfaceImpl(private val sysUserRepo: SysUserRepo) : StpInterface {
    @Transactional(readOnly = true)
    override fun getPermissionList(
        loginId: Any,
        loginType: String?
    ): List<String> {
        if (loginType == "admin"){
            return sysUserRepo.findByIdOrNull((loginId as String).toLong())?.role?.flatMap { it.menu }?.mapNotNull { it.perms }
                ?: emptyList()
        }
        return emptyList()
    }

    @Transactional(readOnly = true)
    override fun getRoleList(
        loginId: Any,
        loginType: String?
    ): List<String> {
        if (loginType == "admin"){
            return sysUserRepo.findByIdOrNull((loginId as String).toLong())?.role?.mapNotNull { it.rolekey } ?: emptyList()
        }
        return emptyList()
    }
}