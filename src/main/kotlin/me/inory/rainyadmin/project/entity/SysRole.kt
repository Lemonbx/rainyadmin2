package me.inory.rainyadmin.project.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import me.inory.rainyadmin.project.entity.base.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
class SysRole(
    var rolekey: String? = null,
    var name: String? = null,
    @ManyToMany(cascade = [(CascadeType.MERGE)])
    var menu: MutableList<SysMenu> = mutableListOf(),
) : BaseEntity()

