import me.alex_s168.stackvm2.ktcode.KTCode
import me.alex_s168.stackvm2.ktcode.`var`.eq
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SimpleMemory

object KTCodeTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val code = object : KTCode(offset = 512, elemSize = VirtualMachine.ELEMENT_SIZE) {
            override fun prog() {
                val addFunc = func(2) {
                    add()
                }

                var a = intVar(1)
                a ++

                addFunc(9, -a)

                getTop() eq 7

                interrupt(StandardInterrupts.EXIT)
            }
        }

        val vm = VirtualMachine(
            SimpleMemory(code.compile()),
            StandardInterrupts.getInterruptTable()
        )

        while (vm.running) {
            vm.tick()
            vm.debug()
        }

        println("Took ${vm.ticksElapsed()} ticks to execute!")
    }

}