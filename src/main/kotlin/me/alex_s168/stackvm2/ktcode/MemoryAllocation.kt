package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.ktcode.exception.UnsupportedOperationException
import kotlin.math.max

class MemoryAllocation(
    val code: KTCode,
    val size: Int,
    val uuid: Long = 0
) {

    val eAm = max(1, size / code.elemSize)

    private fun forElems(block: (Int) -> Unit) {
        code.moveSpRel(1-eAm)

        for (i in 0..<eAm) {
            if (i != 0) code.incSp()
            block(i)
        }
    }

    operator fun inc(): MemoryAllocation {
        code.load(this)
        forElems { code.inc() }
        code.store(this)

        return this
    }

    operator fun dec(): MemoryAllocation {
        code.load(this)
        forElems { code.dec() }
        code.store(this)

        return this
    }

    operator fun plusAssign(value: Int) {
        code.load(this)
        forElems {
            code.loadImm(value)
            code.add()
        }
        code.store(this)
    }

    operator fun minusAssign(value: Int) {
        code.load(this)
        forElems {
            code.loadImm(value)
            code.sub()
        }
        code.store(this)
    }

    operator fun timesAssign(value: Int) {
        code.load(this)
        forElems {
            code.loadImm(value)
            code.mul()
        }
        code.store(this)
    }

    operator fun divAssign(value: Int) {
        code.load(this)
        forElems {
            code.loadImm(value)
            code.div()
        }
        code.store(this)
    }

    operator fun remAssign(value: Int) {
        code.load(this)
        forElems {
            code.loadImm(value)
            code.mod()
        }
        code.store(this)
    }

    operator fun plusAssign(value: MemoryAllocation) {
        code.load(this)
        if (value.eAm == 1) {
            code.load(value)
            forElems { code.add() }
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)
    }

    operator fun minusAssign(value: MemoryAllocation) {
        code.load(this)
        if (value.eAm == 1) {
            code.load(value)
            forElems { code.sub() }
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)
    }

    operator fun timesAssign(value: MemoryAllocation) {
        code.load(this)
        if (value.eAm == 1) {
            code.load(value)
            forElems { code.mul() }
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)
    }

    operator fun divAssign(value: MemoryAllocation) {
        code.load(this)
        if (value.eAm == 1) {
            code.load(value)
            forElems { code.div() }
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)
    }

    operator fun remAssign(value: MemoryAllocation) {
        code.load(this)
        if (value.eAm == 1) {
            code.load(value)
            forElems { code.mod() }
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)
    }

    operator fun unaryMinus(): MemoryAllocation {
        code.load(this)
        if (eAm == 1) {
            code.negate()
        } else {
            throw UnsupportedOperationException()
        }
        code.store(this)

        return this
    }

}