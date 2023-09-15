package me.alex_s168.stackvm2.ktcode

import me.alex_s168.ktlib.collection.LockableArrayList
import me.alex_s168.ktlib.collection.LockableStack
import me.alex_s168.stackvm2.common.MemoryLayout
import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.ktcode.`var`.*
import java.util.*
import kotlin.math.max

open class KTCode(
    offset: Int,
    val elemSize: Int,
    val memoryLayout: MemoryLayout
): KTCodeBaseInstructions(offset) {

    val random = Random()

    var prog: (KTCode) -> Unit = {}

    val romAllocs = LockableArrayList<MemoryAllocation>()
    val unresolvedReferences = LockableArrayList<Pair<Int, MemoryAllocation>>()
    val unresolvedReferences3 = LockableArrayList<Triple<Int, MemoryAllocation, Int>>()
    val funcs = LockableArrayList<VMFunction>()
    val callStack = LockableStack<MemoryAllocation>()
    val ramAllocs = LockableArrayList<RAMAllocation>()

    val TRUE = static(1)
    val FALSE = static(0)

    override fun lock() {
        super.lock()

        romAllocs.lock()
        unresolvedReferences.lock()
        unresolvedReferences3.lock()
        funcs.lock()
        callStack.lock()
    }

    fun static(value: Int): StaticValue =
        StaticValue(this, value)

    fun static(value: Char): StaticValue =
        static(value.code)

    fun const(
        size: Int,
        init: IntArray = IntArray(max(1, size / elemSize)) { 0 }
    ): MemoryAllocation {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return MutableMemoryAllocation(this, max(1, size / elemSize), initVal = init).also {
            romAllocs.add(it)
        }
    }

    fun alloc(size: Int): RAMAllocation {
        if (locked)
            throw IllegalStateException("Code is locked!")

        return RAMAllocation(this, max(1, size / elemSize)).also {
            ramAllocs.add(it)
        }
    }

    fun intVar(value: Int = 0): MutableMemoryAllocation<*> =
        alloc(elemSize).also {
            loadImm(value)
            store(it)
        }

    fun charVar(value: Char = ' '): MutableMemoryAllocation<*> =
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

    fun cond(cond: Stackable): Stackable =
        cond

    fun cond(cond: Stackable, block: () -> Unit): Stackable {
        cond.putOntoStack()
        popCf()
        not()

        mem += Instructions.JUMP_COND.id
        val old = mem.size
        mem += 0

        block()

        mem[old] = mem.size

        return cond
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

    fun call(alloc: MemoryAllocation) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
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

    fun func(args: Int, code: (VMFunction.FunctionCallData) -> Unit): VMFunction {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        return VMFunction(this, args, code).also {
            funcs.add(it)
        }
    }

    private fun getMemRegions(): MutableList<IntRange> =
        memoryLayout.ramRegions.filter {
            it.start != 0
        }.map {
            it.start..<it.end
        }.toMutableList()

    private fun MutableList<IntRange>.consumeMemory(amount: Int): IntRange? {
        this.forEachIndexed { i, it ->
            if (it.count() >= amount) {
                val ret = it.first..<it.first + amount
                set(i, (it.first + amount)..it.last)
                return ret
            }
        }
        return null
    }

    fun ret(value: Stackable) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        value.putOntoStack()

        ret()
    }

    fun compile(): IntArray {
        prog(this)

        val allocs = HashMap<MemoryAllocation, Int>()

        val ram = getMemRegions()

        for ((i, f) in funcs.withIndex()) {
            allocs[f.memAlloc] = mem.size

            val al = RAMAllocation(this, 1, (-i).toLong())
            ramAllocs.add(al)
            store(al)

            callStack.push(al)

            val fcd = VMFunction.FunctionCallData(f)
            f.code(fcd)
            fcd.getReturnValue()?.putOntoStack()

            if (callStack.isNotEmpty())
                ret()
        }

        for (alloc in romAllocs) {
            allocs[alloc] = mem.size
            mem.addAll(alloc.initVal.asIterable())
        }

        for (alloc in ramAllocs) {
            ram.consumeMemory(alloc.size)?.let {
                allocs[alloc] = it.first
            } ?: throw IllegalStateException("Not enough RAM!")
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