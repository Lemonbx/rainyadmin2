package me.inory.rainyadmin.project.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import com.querydsl.core.BooleanBuilder
import me.inory.rainyadmin.anno.SaAdminCheckPermission
import me.inory.rainyadmin.project.dto.MenuInsertInput
import me.inory.rainyadmin.project.dto.MenuTreeQuery
import me.inory.rainyadmin.project.dto.MenuUpdateInput
import me.inory.rainyadmin.project.dto.RoleListItem
import me.inory.rainyadmin.project.dto.SysMenuMapper
import me.inory.rainyadmin.project.entity.QSysMenu
import me.inory.rainyadmin.project.entity.SysMenu
import me.inory.rainyadmin.project.repo.SysMenuRepo
import me.inory.rainyadmin.project.utils.servlet.BaseController
import me.inory.rainyadmin.project.utils.servlet.R
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
@RequestMapping("/a/sysmenu")
class SysMenuController(private val sysMenuRepo: SysMenuRepo, private val sysMenuMapper: SysMenuMapper) :
    BaseController() {
    @SaAdminCheckPermission("sys:menu:query","sys:role:save", mode = SaMode.OR)
    @GetMapping("/")
    fun listMenu(menuTreeQuery: MenuTreeQuery): R {
        val sysMenu = QSysMenu.sysMenu
        val predicate = BooleanBuilder().also { builder ->
            menuTreeQuery.name?.let { builder.and(sysMenu.name.like("%$it%")) }
            menuTreeQuery.perms?.let { builder.and(sysMenu.perms.like("%$it%")) }
        }
        val r = sysMenuRepo.findAll(predicate)
        val tmpList = mutableSetOf<SysMenu>()
        tmpList.addAll(r)
        r.forEach { a->
            var p = a.parent
            while (p != null) {
                val tmpP = p
                if (tmpList.firstOrNull { it.id == tmpP.id } == null) {
                    tmpList.add(tmpP)
                    p = tmpP.parent
                }else{
                    p = null
                }
            }
        }
        return sysMenuMapper.toTreeItem(tmpList.toList()).data()
    }
    @SaAdminCheckPermission("sys:menu:query")
    @GetMapping("/{id}")
    fun getMenu(@PathVariable id: Long): R  {
        return sysMenuRepo.findByIdOrNull(id)?.let { sysMenuMapper.toTreeItem(it) }.data()
    }

    @PostMapping("/")
    @SaAdminCheckPermission("sys:menu:save")
    @Transactional(rollbackFor = [Exception::class])
    fun insert(@RequestBody menu: MenuInsertInput): R{
        val toEntity = sysMenuMapper.toEntity(menu)
        sysMenuRepo.save(toEntity)
        return R.ok()
    }

    @PutMapping("/")
    @SaAdminCheckPermission("sys:menu:save")
    @Transactional(rollbackFor = [Exception::class])
    fun update(@RequestBody menu: MenuUpdateInput): R{
        val menuData = sysMenuRepo.findByIdOrNull(menu.id)?:return R.error("菜单不存在")
        sysMenuMapper.copy(menu,menuData)
        sysMenuRepo.save(menuData)
        return R.ok()
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = [Exception::class])
    fun deleteById(@PathVariable id: Long): R{
        val menu = sysMenuRepo.findByIdOrNull(id) ?: return R.error("菜单不存在")
        if (menu.children.isNotEmpty()) {
            return R.error("存在子级菜单，不允许删除")
        }
        // 删除关联的中间表
        menu.role.forEach { role ->
            role.menu.remove(menu)
        }
        sysMenuRepo.deleteById(id)
        return R.ok()
    }

}