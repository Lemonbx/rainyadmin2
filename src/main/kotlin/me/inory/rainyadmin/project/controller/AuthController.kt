package me.inory.rainyadmin.project.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.crypto.digest.BCrypt
import me.inory.rainyadmin.project.dto.GetUserInfoResp
import me.inory.rainyadmin.project.repo.SysUserRepo
import me.inory.rainyadmin.project.utils.STPADMIN
import me.inory.rainyadmin.project.utils.servlet.BaseController
import me.inory.rainyadmin.project.utils.servlet.R
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/a/auth")
class AuthController(private val sysUserRepo: SysUserRepo) : BaseController() {
    @PostMapping("/login")
    fun login(@RequestBody body: LoginBody): R {
        val user = sysUserRepo.getByUsername(body.username) ?: return "用户名或密码错误".error()
        if (!BCrypt.checkpw(body.password, user.password)) {
            return "用户名或密码错误".error()
        }
        STPADMIN.login(user.id)
        return STPADMIN.tokenValue.data()
    }

    @GetMapping("userInfo")
    @SaCheckLogin
    fun userInfo() = sysUserRepo.findByIdOrNull(StpUtil.getLoginIdAsLong())?.let { GetUserInfoResp.from(it) }.data()

    @GetMapping("logout")
    @SaCheckLogin
    fun logout(): R {
        STPADMIN.logout()
        return R.ok()
    }
}

data class LoginBody(val username: String, val password: String)



