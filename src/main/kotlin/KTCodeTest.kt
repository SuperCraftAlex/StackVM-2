import me.alex_s168.stackvm2.mem.MemoryLayout
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
            .rom(512..<1024)
            .ram(1024..<2048)
            .build()

        val comp = compile(VirtualMachine.ELEMENT_SIZE, layout, 512) { c ->

            val arr = c.alloc(10 * c.elemSize)

            val index1 = c.intVar(3)
            arr[index1] = c.static(69)

            val index2 = c.intVar(2)
            arr[index2] = c.static(58)

            c.load(arr[index1] eq arr[2])




            val addFunc = c.func(2) { args ->
                c.ret(args[0] + args[1])
            }

            var a1 = c.intVar(1)
            a1 ++

            addFunc(9, -a1)

            c.cond(c.getTop() eq 7) {
                c.load(69)
            }



            var a2 = c.intVar(20)
            val b2 = c.intVar(20)

            val cond = c.intVar()
            cond.value = a2 eq b2

            c.doWhile(cond) {
                c.loadImm('a'.code)
                c.interrupt(StandardInterrupts.PUT_CHAR)
                a2--
                cond.value = a2 eq b2
            }


            c.interrupt(StandardInterrupts.EXIT)
        }

        val mem = SegmentedMemory.of(layout).also {
            it.set(512, comp, skip = 512)
        }.finalize()

        println("Program size: ${comp.size - 512} elements")

        val vm = VirtualMachine(
            mem,
            512,
            StandardInterrupts.getInterruptTable()
        )

        println("Decompiled:")
        //println(DisAssembler(comp, 512).disassemble(512))
        println(DeCompiler.decomp(DisAssembler(comp, 512).disassemble(512), comp.toList().stream().skip(512).toList().toIntArray()))
        println()

        println("Started VM!")
        for (i in 0..300) {
            if (!vm.running) {
                println("VM stopped!")
                break
            }
            vm.tick()
        }
        //while (vm.running) {
        //    vm.tick()
        //    vm.debug()
        //}

        println("Took ${vm.ticksElapsed()} ticks to execute!")
        println(vm)
    }

}