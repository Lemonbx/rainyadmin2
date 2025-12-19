package me.inory.rainyadmin.config.exception

class BusinessException : Exception {
    var code: Int? = null
    var msg: String? = null

    constructor(msg: String, code: Int?) : super(msg) {
        this.code = code ?: 500
        this.msg = msg
    }
}