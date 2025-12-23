package me.inory.rainyadmin.project.dto

import me.inory.rainyadmin.config.MapStructConfig
import me.inory.rainyadmin.project.entity.SysRole
import me.inory.rainyadmin.project.entity.SysUser
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

data class UserListQuery(
    val nickname: String? = null,
    val username: String? = null,
) : PageRequest()

data class UserListItem(
    val id: Long?,
    val nickname: String?,
    val username: String?,
    val role: List<Long>?
)

data class SysUserInsertInput(
    val nickname: String,
    val username: String,
    val password: String,
    val role: List<Long>? = null
)

data class SysUserUpdateInput(
    val id: Long,
    val nickname: String,
    val username: String,
    val password: String? = null,
    val role: List<Long>? = null
)

data class GetUserInfoResp(
    val id: Long?,
    val nickname: String?,
    val username: String?,
    val menus: List<MenuInfo>,
    val roles: List<RoleInfo>,
    val permissions: List<String>
) {
    companion object {
        fun from(user: SysUser): GetUserInfoResp {
            val roles = user.role
            val menus = roles.flatMap { it.menu }.distinctBy { it.id }
            val permissions = menus.mapNotNull { it.perms }.distinct()
            return GetUserInfoResp(
                user.id,
                user.nickname,
                user.username,
                SysMenuMapper.INSTANCE.toInfo(menus).filter { a->a.type != 3 },
                SysRoleMapper.INSTANCE.toInfo(roles),
                permissions
            )
        }
    }
}

@Mapper(config = MapStructConfig::class)
interface SysUserMapper {
    fun toEntity(input: SysUserInsertInput): SysUser
    fun toItem(user: SysUser): UserListItem
    fun copy(input: SysUserUpdateInput, @MappingTarget target: SysUser)

    fun mapIdsToRoles(ids: List<Long>?): MutableList<SysRole>? {
        return ids?.map { SysRole().apply { this.id = it } }?.toMutableList()
    }
    fun mapRolesToIds(roles: List<SysRole>?): MutableList<Long>? {
        return roles?.mapNotNull { it.id }?.toMutableList()
    }
}