package me.inory.rainyadmin.project.repo

import me.inory.rainyadmin.project.entity.SysRole
import me.inory.rainyadmin.project.entity.SysUser
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SysUserRepo: CrudRepository<SysUser, Long>, QuerydslPredicateExecutor<SysUser> {
    fun getByUsername(username: String): SysUser?
    fun existsByUsername(username: String): Boolean
    fun existsByUsernameAndIdNot(username: String, id: Long): Boolean
}