package me.alex_s168.stackvm2.target

import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine

object Targets {

    val SVM_2_0_STD = TargetRef.from("svm:2.0.*:std")

    private fun interruptTable(
        funtab: Map<Int, (VirtualMachine) -> Unit>,
        nametab: Map<String, Int>
    ): Map<Int, Pair<String, (VirtualMachine) -> Unit>> {
        val res = HashMap<Int, Pair<String, (VirtualMachine) -> Unit>>()
        for ((id, fu) in funtab) {
            val name = nametab.entries.find { it.value == id }?.key ?: "unknown"
            res[id] = name to fu
        }

        return res
    }

    fun getJumpInst(target: TargetRef): Int {
        if (target.matches(SVM_2_0_STD)) {
            return Instructions.JUMP.id
        }

        throw IllegalArgumentException("Unknown target: $target!")
    }

    fun getInterruptTable(target: TargetRef): Map<Int, Pair<String, (VirtualMachine) -> Unit>> {
        if (target.matches(SVM_2_0_STD)) {
            return interruptTable(
                StandardInterrupts.getInterruptTable(),
                StandardInterrupts.getInterruptIds()
            )
        }

        throw IllegalArgumentException("Unknown target: $target!")
    }

}