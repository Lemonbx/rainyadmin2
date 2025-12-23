package me.inory.rainyadmin.project.dto

import me.inory.rainyadmin.config.MapStructConfig
import me.inory.rainyadmin.project.entity.SysMenu
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

data class MenuTreeQuery(
    val name: String? = null,
    val perms: String? = null,
)

data class MenuTreeItem(
    var id: Long? = null,
    var name: String? = null,
    var logo: String? = null,
    var path: String? = null,
    var perms: String? = null,
    var type: Int? = null,
    var component: String? = null,
    var sort: Int? = null,
    var parentId: Long? = null,
)

data class MenuInsertInput(
    val name: String,
    val logo: String? = null,
    val path: String? = null,
    val perms: String? = null,
    val type: Int,
    val component: String? = null,
    val sort: Int? = null,
    val parentId: Long? = null,
)

data class MenuUpdateInput(
    val id: Long,
    val name: String,
    val logo: String? = null,
    val path: String? = null,
    val perms: String? = null,
    val type: Int,
    val component: String? = null,
    val sort: Int? = null,
    val parentId: Long? = null,
)

data class MenuInfo(
    val id: Long,
    val logo: String?,
    val name: String?,
    val path: String?,
    val component: String?,
    val type: Int?,
    val sort: Int?,
    val parentId: Long?
)

@Mapper(config = MapStructConfig::class)
interface SysMenuMapper {
    @Mapping(source = "parent.id", target = "parentId")
    fun toInfo(menu: SysMenu): MenuInfo
    fun toInfo(menus: List<SysMenu>): List<MenuInfo>
    @Mapping(source = "parent.id", target = "parentId")
    fun toTreeItem(menu: SysMenu): MenuTreeItem
    fun toTreeItem(menus: List<SysMenu>): List<MenuTreeItem>
    @Mapping(source = "parentId", target = "parent")
    fun toEntity(input: MenuInsertInput): SysMenu
    @Mapping(source = "parentId", target = "parent")
    fun copy(input: MenuUpdateInput, @MappingTarget menu: SysMenu)
    fun parentIdToParentMenu(id: Long?): SysMenu? {
        return id?.let { SysMenu().apply { this.id = id } }
    }

    companion object {
        val INSTANCE: SysMenuMapper = Mappers.getMapper(SysMenuMapper::class.java)
    }
}