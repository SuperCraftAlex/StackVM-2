import me.alex_s168.ktlib.collection.ifNotEmpty
import me.alex_s168.stackvm2.asm.Assembler
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SimpleMemory
import kotlin.system.exitProcess

object AssemblerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val asm = Assembler(
            gen = mutableListOf(),
            index = 512
        )

        asm.addSource("""
            ; TEST CODE
            
            jmp test
            
            int 33
            
            test:
                ldi 97
                int 1
                
                ldi 10
                int 1
                
                int 33
        """)

        asm.resolveLabels()
            .complete()

        asm.errors.ifNotEmpty {
            println("Errors:")
            it.forEach { err ->
                println(err)
            }
            exitProcess(-1)
        }

        val vm = VirtualMachine(
            SimpleMemory(asm.gen.toIntArray()),
            512,
            StandardInterrupts.getInterruptTable()
        )

        while (vm.running) {
            vm.tick()
            vm.debug()
        }

        println("Took ${vm.ticksElapsed()} ticks to execute!")
    }

}