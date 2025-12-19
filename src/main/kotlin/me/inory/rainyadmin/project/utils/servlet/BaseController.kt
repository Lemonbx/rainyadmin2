package me.inory.rainyadmin.project.utils.servlet

import me.inory.rainyadmin.project.dto.PageInfo
import org.springframework.data.domain.Page


abstract class BaseController {

    protected fun save(i: Int): R {
        return if (i > 0) R.ok() else R.error()
    }

    protected fun save(i: Boolean): R {
        return if (i) R.ok() else R.error()
    }

    protected fun save(i: Int, action: String): R {
        return if (i > 0) R.ok(action + "成功") else R.error(action + "失败")
    }

    protected fun save(i: Boolean, action: String): R {
        return if (i) R.ok(action + "成功") else R.error(action + "失败")
    }

    protected fun todo(b: Boolean, runnable: Runnable) {
        if (b) {
            runnable.run()
        }
    }

    protected fun String?.ok() = R.ok(this)
    protected fun String?.error() = R.error(this)
    protected fun Int.res(): R {
        return (this > 0).res()
    }

    protected fun Boolean.res(): R {
        return if (this) R.ok() else R.error()
    }

//    protected fun data(resultData: () -> Any) =
//        when (val res = resultData()) {
//            is Boolean -> res.res()
//            is Int -> res.res()
//            is R -> res
//            else -> res.data()
//        }

    protected fun Any?.data() = data("内容不存在")
    protected fun Any?.data(msg: String?): R {
        return if (this == null) {
            R.error(msg)
        } else {
            R.ok().data(this)
        }
    }

    fun <T : Any> data(fn: () -> T?): R {
        val data = fn()
        return if (data == null) {
            R.error()
        } else {
            R.ok().data(data)
        }
    }

    fun <T> Page<T>.data(): R {
        return PageInfo.from(this).data()
    }

    fun <T, A> Page<T>.data(act: (T) -> A): R {
        return PageInfo.from(this,act).data()
    }
}
