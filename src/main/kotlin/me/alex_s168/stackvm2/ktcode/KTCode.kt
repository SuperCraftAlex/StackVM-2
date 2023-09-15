package me.alex_s168.stackvm2.ktcode

import me.alex_s168.ktlib.collection.LockableArrayList
import me.alex_s168.ktlib.collection.LockableStack
import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.`var`.*
import java.util.*
import kotlin.math.max

abstract class KTCode(
    offset: Int,
    val elemSize: Int
): KTCodeBaseInstructions(offset) {

    val random = Random()

    abstract fun prog()

    val memAllocs = LockableArrayList<MemoryAllocation>()
    val unresolvedReferences = LockableArrayList<Pair<Int, MemoryAllocation>>()
    val unresolvedReferences3 = LockableArrayList<Triple<Int, MemoryAllocation, Int>>()
    val funcs = LockableArrayList<VMFunction>()
    val callStack = LockableStack<MemoryAllocation>()

    val TRUE = intVar(1)
    val FALSE = intVar(0)

    override fun lock() {
        super.lock()

        memAllocs.lock()
        unresolvedReferences.lock()
        unresolvedReferences3.lock()
        funcs.lock()
        callStack.lock()
    }

    operator fun VMFunction.invoke(vararg args: Any) {
        this.call(*args)
    }

    fun static(
        value: Int,
    ): StaticValue =
        StaticValue(this, value)

    fun alloc(
        size: Int,
        init: IntArray = IntArray(max(1, size / elemSize)) { 0 }
    ): MutableMemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return MutableMemoryAllocation(this, max(1, size / elemSize), initVal = init).also {
            memAllocs.add(it)
        }
    }

    fun intVar(value: Int = 0): MutableMemoryAllocation =
        alloc(elemSize, intArrayOf(value))

    fun charVar(value: Char = ' '): MutableMemoryAllocation =
        intVar(value.code)

    fun getTop(): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return alloc(elemSize).also {
            it.loadFromStack()
        }
    }

    fun getCf(): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return alloc(elemSize).also {
            pushCf()
            store(it)
        }
    }

    fun cmpEq() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        sub()
        popCf()
        not()
    }

    fun negate() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        loadImm(-1)
        mem += Instructions.MUL.id
    }

    fun negate(amount: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        moveSpRel(-amount)

        repeat(amount) {
            loadImm(-1)
            mul()

            incSp()
        }
    }

    fun call(alloc: MemoryAllocation) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (!memAllocs.contains(alloc)) {
            throw IllegalArgumentException("Cannot call unallocated memory!")
        }

        mem += Instructions.CALL.id
        unresolvedReferences.add(mem.size to alloc)
        mem += 0
    }

    fun call(func: VMFunction) =
        call(func.memAlloc)

    fun callCond(alloc: MemoryAllocation) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (!memAllocs.contains(alloc)) {
            throw IllegalArgumentException("Cannot call unallocated memory!")
        }

        mem += Instructions.CALL_COND.id
        unresolvedReferences.add(mem.size to alloc)
        mem += 0
    }

    fun callCond(func: VMFunction) =
        callCond(func.memAlloc)

    fun ret() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (callStack.isEmpty()) {
            throw IllegalStateException("Cannot return from main!")
        }

        load(callStack.pop())

        retInstBase()
    }

    fun retCond() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (callStack.isEmpty()) {
            throw IllegalStateException("Cannot return from main!")
        }

        load(callStack.pop())

        retCondInstBase()
    }

    fun load(value: Int) =
        loadImm(value)

    fun load(alloc: Stackable) {
        if (locked)
            throw IllegalStateException("Code is locked!")

        alloc.putOntoStack()
    }

    fun store(alloc: MutableStackable<*>) {
        if (locked)
            throw IllegalStateException("Code is locked!")

        alloc.loadFromStack()
    }

    fun rotateTop(amount: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        moveSpRel(2-amount)

        repeat(amount-1) {
            if (it != 0) {
                moveSpRel(1)
            }

            swp()
        }
    }

    fun func(args: Int, code: () -> Unit): VMFunction {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return VMFunction(this, args, code).also {
            funcs.add(it)
        }
    }

    fun compile(): IntArray {
        prog()

        val allocs = HashMap<MemoryAllocation, Int>()

        for ((i, f) in funcs.withIndex()) {
            allocs[f.memAlloc] = mem.size

            val al = MutableMemoryAllocation(this, 1, (-i).toLong())
            memAllocs.add(al)
            store(al)

            callStack.push(al)
            f.code()
            ret()
        }

        for (alloc in memAllocs) {
            allocs[alloc] = mem.size
            mem.addAll(alloc.initVal.asIterable())
        }

        for ((where, alloc) in unresolvedReferences) {
            mem[where] = allocs[alloc]!!
        }

        for ((where, alloc, offset) in unresolvedReferences3) {
            mem[where] = allocs[alloc]!! + offset
        }

        lock()

        return mem.toIntArray()
    }

}