package me.inory.rainyadmin.project.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import me.inory.rainyadmin.project.entity.base.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@DynamicInsert
@DynamicUpdate
@Entity
class SysUser(
    var username: String? = null,
    var password: String? = null,
    var nickname: String? = null,
    @ManyToMany(cascade = [(CascadeType.MERGE)])
    var role: MutableList<SysRole> = mutableListOf(),
) : BaseEntity()

