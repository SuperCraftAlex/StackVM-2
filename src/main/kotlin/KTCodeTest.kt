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
                var arr = alloc(10 * elemSize)
                arr[3] = static(69)
                arr[2] = static(69)

                load(arr[3] eq arr[2])

                /*
                val addFunc = func(2) {
                    add()
                }

                var a = intVar(1)
                a ++

                addFunc(9, -a)

                cond(getTop() eq 7) {
                    load(69)
                }
                 */

                interrupt(StandardInterrupts.EXIT)
            }
        }

        val comp = code.compile()

        println("Program size: ${comp.size - 512} elements")

        val vm = VirtualMachine(
            SimpleMemory(comp),
            StandardInterrupts.getInterruptTable()
        )

        while (vm.running) {
            vm.tick()
            vm.debug()
        }

        println("Took ${vm.ticksElapsed()} ticks to execute!")
    }

}