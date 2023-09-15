package me.alex_s168.stackvm2.ktcode.`var`

infix fun Stackable.eq(value: Any?): Stackable {
    val ktcode = getKTCode()

    if (value is Stackable) {
        if (value.getElemSize() != getElemSize())
            throw UnsupportedOperationException("Cannot compare MemoryAllocations of different sizes!")

        ktcode.loadImm(1)

        forElems {
            this.putOntoStack(it)
            value.putOntoStack(it)

            ktcode.cmpEq()
            ktcode.and()

            ktcode.pushCf()
        }

        return ktcode.getTop()
    }

    if (value is Int) {
        ktcode.load(this)
        ktcode.loadImm(value)

        ktcode.cmpEq()

        return ktcode.getCf()
    }

    return ktcode.FALSE
}

infix fun Stackable.neq(other: Any?) = !(this eq other)