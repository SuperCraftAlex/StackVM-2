package me.alex_s168.kth

interface Counter<E> {

    fun next()

    fun get(): E

}