package me.inory.rainyadmin.project.utils.servlet


class R : HashMap<String?, Any?> {
    constructor(result: Int?, msg: String?, data: Any?) {
        put("code", result)
        put("msg", msg)
        put("data", data)
    }

    constructor()

    fun putkv(key: String?, value: Any?): R {
        super.put(key, value)
        return this
    }

    fun addAll(map: Map<String?, Any?>?): R {
        super.putAll(map!!)
        return this
    }

    fun result(result: Int?): R {
        return putkv("code", result)
    }

    fun msg(msg: String?): R {
        return putkv("msg", msg)
    }

    fun data(data: Any?): R {
        return putkv("data", data)
    }
    operator fun plus(pair: Pair<String?, Any?>): R {
        put(pair.first, pair.second)
        return this
    }

    companion object {
        const val SUCCESS: Int = 1
        const val ERROR: Int = 400
        const val NOAUTH: Int = 401
        const val EXCEPTION: Int = 500

        fun ok(): R {
            return r().result(SUCCESS)
        }

        fun ok(msg: String?): R {
            return ok().msg(msg)
        }

        fun ok(msg: String?, data: Any?): R {
            return r(SUCCESS, msg, data)
        }

        fun error(): R {
            return r().msg("error").result(ERROR)
        }

        fun error(msg: String?): R {
            return error().msg(msg)
        }

        fun exception(msg: String?, e: Exception): R {
            return r().msg(msg).result(EXCEPTION).putkv("exception", e.message)
        }

        fun error(msg: String?, data: Any?): R {
            return r(ERROR, msg, data)
        }

        fun r(code: Int?, msg: String?, data: Any? = null): R {
            return R(code, msg, data)
        }

        fun r(): R {
            val r = R()
            return r.msg("成功").result(SUCCESS).data(emptyMap<String, Any>())
        }
    }
}
