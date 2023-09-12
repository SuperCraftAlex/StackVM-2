package me.alex_s168.kth

fun Boolean.then(block: () -> Unit) {
    if (this) {
        block()
    }
}

fun Boolean.otherwise(block: () -> Unit) {
    if (!this) {
        block()
    }
}

fun Boolean.then(block: () -> Unit, otherwise: () -> Unit) {
    if (this) {
        block()
    } else {
        otherwise()
    }
}