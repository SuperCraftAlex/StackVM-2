package me.alex_s168.stackvm2.ktcode

import me.alex_s168.ktlib.collection.LockableArrayList
import me.alex_s168.ktlib.collection.LockableStack
import me.alex_s168.stackvm2.inst.Instructions
import java.util.*
import kotlin.math.max

abstract class KTCode(
    offset: Int,
    val elemSize: Int
): KTCodeBaseInstructions(offset) {

    abstract fun prog()

    private val memAllocs = LockableArrayList<MemoryAllocation>()
    val unresolvedReferences = LockableArrayList<Pair<Int, MemoryAllocation>>()
    private val funcs = LockableArrayList<VMFunction>()
    private val callStack = LockableStack<MemoryAllocation>()

    override fun lock() {
        super.lock()

        memAllocs.lock()
        unresolvedReferences.lock()
        funcs.lock()
        callStack.lock()
    }

    operator fun VMFunction.invoke(vararg args: Any) {
        this.call(*args)
    }

    fun alloc(size: Int): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return MemoryAllocation(this, size).also {
            memAllocs.add(it)
        }
    }

    fun intVar(value: Int = 0): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return alloc(elemSize).also {
            loadImm(value)
            store(it)
        }
    }

    fun charVar(value: Char = 'a'): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return alloc(1).also {
            loadImm(value.toInt())
            store(it)
        }
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

    fun load(alloc: MemoryAllocation) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (!memAllocs.contains(alloc)) {
            throw IllegalArgumentException("Cannot load from unallocated memory!")
        }

        val am = max(1, alloc.size / elemSize)

        repeat(am) {
            mem += Instructions.LD_ADDR.id
            unresolvedReferences.add(mem.size to alloc)
            mem += 0
        }
    }

    fun store(alloc: MemoryAllocation) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (!memAllocs.contains(alloc)) {
            throw IllegalArgumentException("Cannot store to unallocated memory!")
        }

        val am = max(1, alloc.size / elemSize)

        repeat(am) {
            mem += Instructions.ST_ADDR.id
            unresolvedReferences.add(mem.size to alloc)
            mem += 0
        }

        if (am > 1)
            rotateTop(am - 1)
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
            val al = MemoryAllocation(this, elemSize, (-i).toLong())
            memAllocs.add(al)

            store(al)

            callStack.push(al)
            f.code()
            ret()
        }

        for (alloc in memAllocs) {
            allocs[alloc] = mem.size
            repeat(alloc.size) { mem += 0 }
        }

        for ((where, alloc) in unresolvedReferences) {
            mem[where] = allocs[alloc]!!
        }

        lock()
        return mem.toIntArray()
    }

}