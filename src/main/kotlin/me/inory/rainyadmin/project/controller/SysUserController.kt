package me.inory.rainyadmin.project.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.crypto.digest.BCrypt
import com.querydsl.core.BooleanBuilder
import me.inory.rainyadmin.project.dto.SysUserInsertInput
import me.inory.rainyadmin.project.dto.SysUserMapper
import me.inory.rainyadmin.project.dto.SysUserUpdateInput
import me.inory.rainyadmin.project.dto.UserListQuery
import me.inory.rainyadmin.project.entity.QSysUser
import me.inory.rainyadmin.project.entity.SysUser
import me.inory.rainyadmin.project.repo.SysUserRepo
import me.inory.rainyadmin.project.utils.servlet.BaseController
import me.inory.rainyadmin.project.utils.servlet.R
import org.hibernate.Hibernate
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/a/sysusers")
class SysUserController(private val sysUserRepo: SysUserRepo, private val sysUserMapper: SysUserMapper) :
    BaseController() {
    @SaCheckPermission("sys:user:query")
    @GetMapping("/")
    fun listUsers(userListQuery: UserListQuery): R {
        val sysUser = QSysUser.sysUser
        val predicate = BooleanBuilder().also { builder ->
            userListQuery.username?.let { builder.and(sysUser.username.like("%$it%")) }
            userListQuery.nickname?.let { builder.and(sysUser.nickname.like("%$it%")) }
        }
        val r = sysUserRepo.findAll(predicate, userListQuery.getPage(Sort.by("id").descending()))
        return r.data {
            sysUserMapper.toItem(it)
        }
    }

    @SaCheckPermission("sys:user:query")
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): R {
        return sysUserRepo.findByIdOrNull(id)?.let { sysUserMapper.toItem(it) }.data()
    }

    @PostMapping("/")
    @SaCheckPermission("sys:user:save")
    @Transactional(rollbackFor = [Exception::class])
    fun insert(@RequestBody user: SysUserInsertInput): R {
        if (sysUserRepo.existsByUsername(user.username)) {
            return "用户名已存在".error()
        }
        val toEntity = sysUserMapper.toEntity(user)
        toEntity.password = BCrypt.hashpw(user.password)
        sysUserRepo.save(toEntity)
        return R.ok()
    }

    @PutMapping("/")
    @SaCheckPermission("sys:user:save")
    @Transactional(rollbackFor = [Exception::class])
    fun update(@RequestBody user: SysUserUpdateInput): R {
        if (sysUserRepo.existsByUsernameAndIdNot(user.username, user.id)) {
            return "用户名已存在".error()
        }
        val userData = sysUserRepo.findByIdOrNull(user.id) ?: return R.error("用户不存在")
        sysUserMapper.copy(user, userData)
        if (user.password?.isNotBlank() == true) {
            userData.password = BCrypt.hashpw(user.password)
        }
        sysUserRepo.save(userData)
        return R.ok()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): R {
        sysUserRepo.deleteById(id)
        return R.ok()
    }
}