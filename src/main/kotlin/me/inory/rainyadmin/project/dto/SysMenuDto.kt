package me.inory.rainyadmin.project.dto

import me.inory.rainyadmin.config.MapStructConfig
import me.inory.rainyadmin.project.entity.SysMenu
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers


data class MenuInfo(
    val id: Long,
    val logo: String?,
    val path: String?,
    val type: Int?,
    val sort: Int?,
    val parentId: Long
)

@Mapper(config = MapStructConfig::class)
interface SysMenuMapper {
    fun toInfo(menu: SysMenu): MenuInfo
    fun toInfo(menus: List<SysMenu>): List<MenuInfo>
    companion object{
        val INSTANCE : SysMenuMapper = Mappers.getMapper(SysMenuMapper::class.java)
    }
}