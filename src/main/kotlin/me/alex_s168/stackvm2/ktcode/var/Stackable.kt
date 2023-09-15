package me.alex_s168.stackvm2.ktcode.`var`

import me.alex_s168.stackvm2.inst.Instructions
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

        putOntoStack(index)

        return getKTCode().getTop()
    }

    operator fun not(): Stackable {
        val ktcode = getKTCode()

        val new = ktcode.alloc(getElemSize() * ktcode.elemSize)

        forElems {
            putOntoStack(it)

            ktcode.popCf()
            ktcode.not()

            ktcode.pushCf()

            new.loadFromStack(it)
        }

        return new
    }

    fun otherwise(block: () -> Unit) {
        val ktcode = getKTCode()

        putOntoStack()
        ktcode.popCf()

        ktcode.mem += Instructions.JUMP_COND.id
        val old = ktcode.mem.size
        ktcode.mem += 0

        block()

        ktcode.mem[old] = ktcode.mem.size
    }


    operator fun plus(other: Stackable): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        if (other.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack()
                ktcode.add()
                alloc.loadFromStack(it)
            }
        }
        else if (other.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack(it)
                ktcode.add()
                alloc.loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("Incompatible element sizes!")
        }

        return alloc
    }

    operator fun minus(other: Stackable): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        if (other.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack()
                ktcode.sub()
                alloc.loadFromStack(it)
            }
        }
        else if (other.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack(it)
                ktcode.sub()
                alloc.loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("Incompatible element sizes!")
        }

        return alloc
    }

    operator fun times(other: Stackable): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        if (other.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack()
                ktcode.mul()
                alloc.loadFromStack(it)
            }
        }
        else if (other.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack(it)
                ktcode.mul()
                alloc.loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("Incompatible element sizes!")
        }

        return alloc
    }

    operator fun div(other: Stackable): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        if (other.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack()
                ktcode.div()
                alloc.loadFromStack(it)
            }
        }
        else if (other.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack(it)
                ktcode.div()
                alloc.loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("Incompatible element sizes!")
        }

        return alloc
    }

    operator fun rem(other: Stackable): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        if (other.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack()
                ktcode.mod()
                alloc.loadFromStack(it)
            }
        }
        else if (other.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                other.putOntoStack(it)
                ktcode.mod()
                alloc.loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("Incompatible element sizes!")
        }

        return alloc
    }

    operator fun plus(other: Int): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.loadImm(other)
            ktcode.add()
            alloc.loadFromStack(it)
        }

        return alloc
    }

    operator fun minus(other: Int): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.loadImm(other)
            ktcode.sub()
            alloc.loadFromStack(it)
        }

        return alloc
    }

    operator fun times(other: Int): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.loadImm(other)
            ktcode.mul()
            alloc.loadFromStack(it)
        }

        return alloc
    }

    operator fun div(other: Int): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.loadImm(other)
            ktcode.div()
            alloc.loadFromStack(it)
        }

        return alloc
    }

    operator fun rem(other: Int): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.loadImm(other)
            ktcode.mod()
            alloc.loadFromStack(it)
        }

        return alloc
    }

    operator fun unaryMinus(): Stackable {
        val ktcode = getKTCode()

        val alloc = ktcode.alloc(getElemSize() * ktcode.elemSize)

        repeat(getElemSize()) {
            putOntoStack(it)
            ktcode.negate()
            alloc.loadFromStack(it)
        }

        return alloc
    }

}