package me.inory.rainyadmin.project.dto

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

open class PageRequest(val pageNum: Int = 1, val pageSize: Int = 10) {
    fun getPage(): PageRequest = PageRequest.of(pageNum - 1, pageSize)
    fun getPage(sort: Sort):PageRequest = PageRequest.of(pageNum - 1, pageSize, sort)
}

class PageInfo<T>(val total: Long? = 0, val data: List<T>) {
    companion object {
        fun <T> from(total: Long, data: List<T>) = PageInfo(total, data)
        fun <T> from(i: Page<T>) = PageInfo(i.totalElements,i.toList())
        fun <T,A> from(i: Page<T>,act:(T)->A) = PageInfo(i.totalElements,i.toList().mapNotNull { a->act(a) }.toList())
    }
}