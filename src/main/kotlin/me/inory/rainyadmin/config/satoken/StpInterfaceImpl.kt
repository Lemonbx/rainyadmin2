package me.inory.rainyadmin.config.satoken

import cn.dev33.satoken.stp.StpInterface
import me.inory.rainyadmin.project.repo.SysUserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class StpInterfaceImpl(private val sysUserRepo: SysUserRepo) : StpInterface {
    override fun getPermissionList(
        loginId: Any,
        loginType: String?
    ): List<String> {
        if (loginType == "admin"){
            return sysUserRepo.findByIdOrNull(loginId as Long)?.role?.flatMap { it.menu }?.mapNotNull { it.perms }
                ?: emptyList()
        }
        return emptyList()
    }

    override fun getRoleList(
        loginId: Any,
        loginType: String?
    ): List<String> {
        if (loginType == "admin"){
            return sysUserRepo.findByIdOrNull(loginId as Long)?.role?.mapNotNull { it.rolekey } ?: emptyList()
        }
        return emptyList()
    }
}