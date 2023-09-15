package me.alex_s168.stackvm2.ktcode.`var`

fun Stackable.asMutable(): MutableStackable<*> {
    if (this is MutableStackable<*>) return this

    return getKTCode().alloc(getElemSize() * getKTCode().elemSize).also {
        this.putOntoStack()
        it.loadFromStack()
    }
}