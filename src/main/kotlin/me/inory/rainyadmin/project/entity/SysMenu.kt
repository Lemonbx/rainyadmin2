package me.inory.rainyadmin.project.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
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
    //1目录 2路由 3按钮
    var type: Int? = null,
    var component: String? = null,
    var sort: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    var parent: SysMenu? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    var children: MutableList<SysMenu> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "menu")
    var role: MutableList<SysRole> = mutableListOf()
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SysMenu) return false

        if (id != null && id == other.id) return true

        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
