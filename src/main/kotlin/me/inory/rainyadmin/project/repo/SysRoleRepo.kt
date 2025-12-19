package me.inory.rainyadmin.project.repo

import me.inory.rainyadmin.project.entity.SysRole
import me.inory.rainyadmin.project.entity.SysUser
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SysRoleRepo: CrudRepository<SysRole, Long>, QuerydslPredicateExecutor<SysRole> {
    fun existsByRolekey(rolekey: String): Boolean
    fun existsByRolekeyAndIdNot(rolekey: String, id: Long): Boolean
}