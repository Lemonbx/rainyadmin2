package me.inory.rainyadmin.project.entity

import jakarta.persistence.Entity
import me.inory.rainyadmin.project.entity.base.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.mapstruct.Mapper

@DynamicInsert
@DynamicUpdate
@Entity
class SysMenu(
    var name: String? = null,
    var logo: String? = null,
    var path: String? = null,
    var perms: String? = null,
    var type: Int? = null,
    var component: String? = null,
    var sort: Int? = null,
    var parentId: Long? = null,
) : BaseEntity()
