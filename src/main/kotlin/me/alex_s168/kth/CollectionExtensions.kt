package me.alex_s168.kth

fun <T> Collection<T>.ifNotEmpty(block: (Collection<T>) -> Unit) {
    if (this.isNotEmpty()) {
        block(this)
    }
}