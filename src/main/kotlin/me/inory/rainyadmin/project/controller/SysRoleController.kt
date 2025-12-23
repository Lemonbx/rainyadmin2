package me.inory.rainyadmin.project.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import cn.hutool.crypto.digest.BCrypt
import com.querydsl.core.BooleanBuilder
import me.inory.rainyadmin.anno.SaAdminCheckPermission
import me.inory.rainyadmin.project.dto.RoleListQuery
import me.inory.rainyadmin.project.dto.SysRoleInsertInput
import me.inory.rainyadmin.project.dto.SysRoleMapper
import me.inory.rainyadmin.project.dto.SysRoleUpdateInput
import me.inory.rainyadmin.project.entity.QSysRole
import me.inory.rainyadmin.project.repo.SysRoleRepo
import me.inory.rainyadmin.project.utils.servlet.BaseController
import me.inory.rainyadmin.project.utils.servlet.R
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
@RequestMapping("/a/sysrole")
class SysRoleController(private val sysRoleRepo: SysRoleRepo, private val sysRoleMapper: SysRoleMapper) :
    BaseController() {
    @SaAdminCheckPermission("sys:role:query", "sys:user:save", mode = SaMode.OR)
    @GetMapping("/")
    fun listUsers(roleListQuery: RoleListQuery): R {
        val sysRole = QSysRole.sysRole
        val predicate = BooleanBuilder().also { builder ->
            roleListQuery.rolekey?.let { builder.and(sysRole.rolekey.like("%$it%")) }
            roleListQuery.name?.let { builder.and(sysRole.name.like("%$it%")) }
        }
        val r = sysRoleRepo.findAll(predicate, roleListQuery.getPage(Sort.by("id").descending()))
        return r.data {
            sysRoleMapper.toItem(it)
        }
    }

    @SaAdminCheckPermission("sys:role:query")
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): R {
        return sysRoleRepo.findByIdOrNull(id)?.let { sysRoleMapper.toItem(it) }.data()
    }

    @PostMapping("/")
    @SaAdminCheckPermission("sys:role:save")
    @Transactional(rollbackFor = [Exception::class])
    fun insert(@RequestBody role: SysRoleInsertInput): R {
        if (sysRoleRepo.existsByRolekey(role.rolekey)) {
            return "角色key已存在".error()
        }
        val toEntity = sysRoleMapper.toEntity(role)
        sysRoleRepo.save(toEntity)
        return R.ok()
    }

    @PutMapping("/")
    @SaAdminCheckPermission("sys:user:save")
    @Transactional(rollbackFor = [Exception::class])
    fun update(@RequestBody role: SysRoleUpdateInput): R {
        if (sysRoleRepo.existsByRolekeyAndIdNot(role.rolekey, role.id)) {
            return "角色key已存在".error()
        }
        val roleData = sysRoleRepo.findByIdOrNull(role.id) ?: return R.error("角色不存在")
        sysRoleMapper.copy(role, roleData)
        sysRoleRepo.save(roleData)
        return R.ok()
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = [Exception::class])
    fun delete(@PathVariable id: Long): R {
        sysRoleRepo.deleteById(id)
        return R.ok()
    }
}