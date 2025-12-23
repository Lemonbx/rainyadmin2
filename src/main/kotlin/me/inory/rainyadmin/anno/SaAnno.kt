package me.inory.rainyadmin.anno

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import me.inory.rainyadmin.project.utils.STPADMIN
import org.springframework.core.annotation.AliasFor;



@SaCheckLogin(type = "admin")
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class SaAdminCheckLogin()

@SaCheckPermission(type = "admin")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class SaAdminCheckPermission(

    /**
     * 需要校验的权限码
     * @return 需要校验的权限码
     */
    @get:AliasFor(annotation = SaCheckPermission::class)
    vararg val value: String = [],

    /**
     * 验证模式：AND | OR，默认AND
     * @return 验证模式
     */
    @get:AliasFor(annotation = SaCheckPermission::class)
    val mode: SaMode = SaMode.AND,

    /**
     * 在权限校验不通过时的次要选择，两者只要其一校验成功即可通过校验
     *
     * 例1：@SaCheckPermission(value="user-add", orRole="admin")，
     * 代表本次请求只要具有 user-add权限 或 admin角色 其一即可通过校验。
     *
     * 例2： orRole = {"admin", "manager", "staff"}，具有三个角色其一即可。
     * 例3： orRole = {"admin, manager, staff"}，必须三个角色同时具备。
     *
     * @return /
     */
    @get:AliasFor(annotation = SaCheckPermission::class)
    val orRole: Array<String> = []
)