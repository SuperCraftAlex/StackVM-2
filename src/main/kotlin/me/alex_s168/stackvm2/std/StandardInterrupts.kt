package me.alex_s168.stackvm2.std

import me.alex_s168.stackvm2.vm.VirtualMachine

object StandardInterrupts {

    private val interrupts = HashMap<Int, (VirtualMachine) -> Unit>()

    const val PUT_CHAR          = 1      // put(char)
    const val GET_CHAR          = 2      // get() -> char
    const val EXIT              = 33     // exit()

    init {

        interrupts[PUT_CHAR] = { vm ->
            print(vm.pop().toChar())
        }

        interrupts[GET_CHAR] = { vm ->
            vm.push(readln().single().code)
        }

        interrupts[EXIT] = { vm ->
            vm.running = false
        }

    }

    fun getInterruptTable(): Map<Int, (VirtualMachine) -> Unit> =
        interrupts.toMap()

}