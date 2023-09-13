package me.alex_s168.ktlib.counter

interface Counter<E> {

    fun next()

    fun get(): E

}