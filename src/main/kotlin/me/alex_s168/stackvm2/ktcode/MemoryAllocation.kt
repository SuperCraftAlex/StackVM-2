package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.exception.UnsupportedOperationException
import kotlin.math.max

class MemoryAllocation(
    val ktcode: KTCode,
    val size: Int,
    val uuid: Long = 0
) {

    val eAm = max(1, size / ktcode.elemSize)

    private fun forElems(block: (Int) -> Unit) {
        ktcode.moveSpRel(1-eAm)

        for (i in 0..<eAm) {
            if (i != 0) ktcode.incSp()
            block(i)
        }
    }

    operator fun inc(): MemoryAllocation {
        ktcode.load(this)
        forElems { ktcode.inc() }
        ktcode.store(this)

        return this
    }

    operator fun dec(): MemoryAllocation {
        ktcode.load(this)
        forElems { ktcode.dec() }
        ktcode.store(this)

        return this
    }

    operator fun plusAssign(value: Int) {
        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.add()
        }
        ktcode.store(this)
    }

    operator fun minusAssign(value: Int) {
        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.sub()
        }
        ktcode.store(this)
    }

    operator fun timesAssign(value: Int) {
        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.mul()
        }
        ktcode.store(this)
    }

    operator fun divAssign(value: Int) {
        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.div()
        }
        ktcode.store(this)
    }

    operator fun remAssign(value: Int) {
        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.mod()
        }
        ktcode.store(this)
    }

    operator fun plusAssign(value: MemoryAllocation) {
        ktcode.load(this)
        if (value.eAm == 1) {
            ktcode.load(value)
            forElems { ktcode.add() }
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)
    }

    operator fun minusAssign(value: MemoryAllocation) {
        ktcode.load(this)
        if (value.eAm == 1) {
            ktcode.load(value)
            forElems { ktcode.sub() }
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)
    }

    operator fun timesAssign(value: MemoryAllocation) {
        ktcode.load(this)
        if (value.eAm == 1) {
            ktcode.load(value)
            forElems { ktcode.mul() }
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)
    }

    operator fun divAssign(value: MemoryAllocation) {
        ktcode.load(this)
        if (value.eAm == 1) {
            ktcode.load(value)
            forElems { ktcode.div() }
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)
    }

    operator fun remAssign(value: MemoryAllocation) {
        ktcode.load(this)
        if (value.eAm == 1) {
            ktcode.load(value)
            forElems { ktcode.mod() }
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)
    }

    operator fun unaryMinus(): MemoryAllocation {
        ktcode.load(this)
        if (eAm == 1) {
            ktcode.negate()
        } else {
            throw UnsupportedOperationException()
        }
        ktcode.store(this)

        return this
    }

    fun load(off: Int) {
        ktcode.mem += Instructions.LD_ADDR.id
        ktcode.unresolvedReferences3.add(Triple(ktcode.mem.size, this, off))
        ktcode.mem += 0
    }

    infix fun eq(value: Any?): MemoryAllocation {
        if (value is MemoryAllocation) {
            if (value.eAm != eAm)
                throw UnsupportedOperationException("Cannot compare MemoryAllocations of different sizes!")

            ktcode.loadImm(1)

            forElems {
                this.load(it)
                value.load(it)

                ktcode.cmpEq()
                ktcode.and()

                ktcode.pushCf()
            }

            return ktcode.getCf()
        }

        if (value is Int) {
            ktcode.load(this)
            ktcode.loadImm(value)

            ktcode.cmpEq()

            return ktcode.getCf()
        }

        return ktcode.FALSE
    }

}