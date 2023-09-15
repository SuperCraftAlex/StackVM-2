package me.alex_s168.stackvm2.ktcode

import me.alex_s168.ktlib.Lockable
import me.alex_s168.stackvm2.inst.Instructions
import kotlin.collections.ArrayList

open class KTCodeBaseInstructions(
    offset: Int
): Lockable {

    var locked = false
        private set

    override fun isLocked(): Boolean =
        locked

    override fun lock() {
        locked = true
    }

    val mem = ArrayList<Int>(offset).also {
        for (i in 0..<offset) {
            it.add(Instructions.NOOP.id)
        }
    }

    fun noop() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.NOOP.id
    }


    fun incSp() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.INC_SP.id
    }

    fun decSp() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.DEC_SP.id
    }


    fun moveSp(position: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.MSP.id
        mem += position
    }

    fun moveSpRel(offset: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        if (offset == 0) return

        mem += Instructions.REL_MSP.id
        mem += offset
    }

    fun setZero() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.SET_Z.id
    }

    fun jump(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.JUMP.id
        mem += address
    }

    fun jumpCond(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.JUMP_COND.id
        mem += address
    }

    fun call(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.CALL.id
        mem += address
    }

    fun callCond(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.CALL_COND.id
        mem += address
    }

    internal fun retInstBase() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.RET.id
    }

    internal fun retCondInstBase() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.RET_COND.id
    }

    fun loadImm(value: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.LD_IMM.id
        mem += value
    }

    fun loadAddr(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.LD_ADDR.id
        mem += address
    }

    fun storeAddr(address: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.ST_ADDR.id
        mem += address
    }


    fun stackLoad() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.LD_ST_ADDR.id
    }

    fun stackStore() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.ST_ST_ADDR.id
    }


    fun inc() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.INC.id
    }

    fun dec() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.DEC.id
    }

    fun interrupt(id: Int) {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.INT.id
        mem += id
    }

    fun not() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.NOT.id
    }

    fun and() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.AND.id
    }

    fun or() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.OR.id
    }

    fun pushCf() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.PH_COND.id
    }

    fun popCf() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.PO_COND.id
    }

    fun abs() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.ABS.id
    }

    fun add() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.ADD.id
    }

    fun sub() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.SUB.id
    }

    fun mul() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.MUL.id
    }

    fun div() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.DIV.id
    }

    fun mod() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.MOD.id
    }

    fun dup() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.DUP.id
    }

    fun swp() {
        if (locked) {
            throw IllegalStateException("Code is locked!")
        }

        mem += Instructions.SWP.id
    }

}