package me.alex_s168.stackvm2.ktcode.`var`

@Suppress("UNCHECKED_CAST")
interface MutableStackable<T: MutableStackable<T>>: Stackable {

    fun loadFromStack()

    fun loadFromStack(offset: Int)

    fun loadFromStack(offset: Stackable)

    operator fun inc(): T {
        putOntoStack()
        forElems { getKTCode().inc() }
        loadFromStack()

        return this as T
    }

    operator fun dec(): T {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems { ktcode.dec() }
        ktcode.store(this)

        return this as T
    }

    operator fun plusAssign(value: Int) {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.add()
        }
        ktcode.store(this)
    }

    operator fun minusAssign(value: Int) {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.sub()
        }
        ktcode.store(this)
    }

    operator fun timesAssign(value: Int) {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.mul()
        }
        ktcode.store(this)
    }

    operator fun divAssign(value: Int) {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.div()
        }
        ktcode.store(this)
    }

    operator fun remAssign(value: Int) {
        val ktcode = getKTCode()

        ktcode.load(this)
        forElems {
            ktcode.loadImm(value)
            ktcode.mod()
        }
        ktcode.store(this)
    }

    operator fun plusAssign(value: Stackable) {
        val ktcode = getKTCode()

        if (value.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack()
                ktcode.add()
                loadFromStack(it)
            }
        }
        else if (value.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack(it)
                ktcode.add()
                loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("incompatible element sizes!")
        }
    }

    operator fun minusAssign(value: Stackable) {
        val ktcode = getKTCode()

        if (value.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack()
                ktcode.sub()
                loadFromStack(it)
            }
        }
        else if (value.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack(it)
                ktcode.add()
                loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("incompatible element sizes!")
        }
    }

    operator fun timesAssign(value: Stackable) {
        val ktcode = getKTCode()

        if (value.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack()
                ktcode.mul()
                loadFromStack(it)
            }
        }
        else if (value.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack(it)
                ktcode.add()
                loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("incompatible element sizes!")
        }
    }

    operator fun divAssign(value: Stackable) {
        val ktcode = getKTCode()

        if (value.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack()
                ktcode.div()
                loadFromStack(it)
            }
        }
        else if (value.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack(it)
                ktcode.add()
                loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("incompatible element sizes!")
        }
    }

    operator fun remAssign(value: Stackable) {
        val ktcode = getKTCode()

        if (value.getElemSize() == 1) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack()
                ktcode.mod()
                loadFromStack(it)
            }
        }
        else if (value.getElemSize() == getElemSize()) {
            repeat(getElemSize()) {
                putOntoStack(it)
                value.putOntoStack(it)
                ktcode.add()
                loadFromStack(it)
            }
        }
        else {
            throw UnsupportedOperationException("incompatible element sizes!")
        }
    }

    operator fun set(index: Int, value: Int) {
        val ktcode = getKTCode()

        if (index >= getElemSize()) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for array!")
        }

        ktcode.loadImm(value)
        loadFromStack()
    }

    operator fun set(index: Int, value: Stackable) {
        if (index >= getElemSize()) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for array!")
        }

        value.putOntoStack()
        loadFromStack(index)
    }

    operator fun set(index: Stackable, value: Int) {
        val ktcode = getKTCode()

        if (index.getElemSize() != 1) {
            throw UnsupportedOperationException("Cannot index $this with $index!")
        }

        ktcode.loadImm(value)
        loadFromStack(index)
    }

    operator fun set(index: Stackable, value: Stackable) {
        if (index.getElemSize() != 1) {
            throw UnsupportedOperationException("Cannot index $this with $index!")
        }

        value.putOntoStack()
        loadFromStack(index)
    }

}