package me.inory.rainyadmin.project.dto

import me.inory.rainyadmin.config.MapStructConfig
import me.inory.rainyadmin.project.entity.SysMenu
import me.inory.rainyadmin.project.entity.SysRole
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.factory.Mappers

data class RoleListQuery(
    val name: String? = null,
    val rolekey: String? = null,
) : PageRequest()

data class RoleListItem(
    val id: Long?,
    val name: String?,
    val rolekey: String?,
    val menu: List<Long>?
)

data class SysRoleInsertInput(
    val name: String,
    val rolekey: String,
    val menu: List<Long>? = null
)

data class SysRoleUpdateInput(
    val id: Long,
    val name: String,
    val rolekey: String,
    val menu: List<Long>? = null
)


data class RoleInfo(
    val id: Long,
    val rolekey: String,
    val name: String
)

@Mapper(config = MapStructConfig::class)
interface SysRoleMapper {
    fun toInfo(menu: SysRole): RoleInfo
    fun toInfo(menus: List<SysRole>): List<RoleInfo>
    fun toItem(role: SysRole): RoleListItem
    fun toEntity(role: SysRoleInsertInput): SysRole
    fun menuToIds(menu: List<SysMenu>?): MutableList<Long>? {
        return menu?.mapNotNull { it.id }?.toMutableList()
    }

    fun idToMenu(ids: List<Long>?): MutableList<SysMenu>? {
        return ids?.map { a -> SysMenu().also { it.id = a} }?.toMutableList()
    }
    fun copy(role: SysRoleUpdateInput, @MappingTarget roleData: SysRole)

    companion object {
        val INSTANCE: SysRoleMapper = Mappers.getMapper(SysRoleMapper::class.java)
    }
}