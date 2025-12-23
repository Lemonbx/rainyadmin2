package me.inory.rainyadmin.project.repo

import me.inory.rainyadmin.project.entity.SysMenu
import me.inory.rainyadmin.project.entity.SysUser
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SysMenuRepo: CrudRepository<SysMenu, Long>, QuerydslPredicateExecutor<SysMenu> {
}