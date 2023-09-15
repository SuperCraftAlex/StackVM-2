package me.alex_s168.stackvm2.ktcode.`var`

import me.alex_s168.stackvm2.ktcode.KTCode

interface Stackable {

    fun getKTCode(): KTCode

    fun getElemSize(): Int

    fun putOntoStack(offset: Int)

    fun putOntoStack(offset: Stackable)

    fun putOntoStack()

    fun forElems(block: (Int) -> Unit) {
        getKTCode().moveSpRel(1-getElemSize())

        for (i in 0..<getElemSize()) {
            if (i != 0) getKTCode().incSp()
            block(i)
        }
    }

    operator fun get(index: Int): Stackable {
        val ktcode = getKTCode()

        if (index >= getElemSize())
            throw IndexOutOfBoundsException("Index $index is out of bounds for $this!")

        putOntoStack(index)

        return ktcode.getTop()
    }

    operator fun get(index: Stackable): Stackable {
        if (index.getElemSize() != 1)
            throw UnsupportedOperationException("Index needs to be Int!")

        return get(index)
    }

    operator fun not(): Stackable {
        val ktcode = getKTCode()

        val new = ktcode.alloc(getElemSize())

        forElems {
            putOntoStack(it)

            ktcode.popCf()
            ktcode.not()

            ktcode.pushCf()

            new.loadFromStack(it)
        }

        return new
    }

}