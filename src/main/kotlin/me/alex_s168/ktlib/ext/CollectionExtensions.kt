package me.alex_s168.ktlib.ext

fun <T> Collection<T>.ifNotEmpty(block: (Collection<T>) -> Unit) {
    if (this.isNotEmpty()) {
        block(this)
    }
}

fun <T> Collection<T>.ifEmpty(block: (Collection<T>) -> Unit) {
    if (this.isEmpty()) {
        block(this)
    }
}