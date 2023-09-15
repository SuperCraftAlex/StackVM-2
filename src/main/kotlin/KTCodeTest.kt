import me.alex_s168.stackvm2.common.MemoryLayout
import me.alex_s168.stackvm2.decomp.DeCompiler
import me.alex_s168.stackvm2.disasm.DisAssembler
import me.alex_s168.stackvm2.ktcode.compile
import me.alex_s168.stackvm2.ktcode.`var`.eq
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SegmentedMemory

object KTCodeTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val layout = MemoryLayout.new()
            .ram(0..<512)
            .rom(512..<1024)    // ram is used here for the program
            .ram(1024..<2048)
            .build()

        val comp = compile(VirtualMachine.ELEMENT_SIZE, layout, 512) { c ->
           /*
            val arr = c.ram(10 * c.elemSize)

            val index1 = c.intVar(3)
            arr[index1 + 1] = c.static(69)

            val index2 = c.intVar(2)
            arr[index2] = c.static(58)

            c.load(arr[index1] eq arr[2])
*/

            val addFunc = c.func(2) { args ->
                c.ret(args[0] + args[1])
            }

            var a = c.ram(1)
            a ++

            addFunc(9, -a)

            c.cond(c.getTop() eq 7) {
                c.load(69)
            }


            c.interrupt(StandardInterrupts.EXIT)
        }

        val mem = SegmentedMemory.of(layout)

        mem.set(512, comp, skip = 512)

        println("Program size: ${comp.size - 512} elements")

        val vm = VirtualMachine(
            mem,
            512,
            StandardInterrupts.getInterruptTable()
        )

        val disasm = DisAssembler(comp, 512)
        //println("Decompiled:")
        println(DeCompiler.decomp(disasm.disassemble(), comp.toList().stream().skip(512).toList().toIntArray()))

        println("Started VM!")
        while (vm.running) {
            vm.tick()
            //vm.debug()
        }

        println("Took ${vm.ticksElapsed()} ticks to execute!")
    }

}