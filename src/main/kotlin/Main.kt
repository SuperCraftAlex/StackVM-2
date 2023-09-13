import me.alex_s168.kth.ifNotEmpty
import me.alex_s168.stackvm2.asm.Assembler
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SimpleMemory
import kotlin.system.exitProcess

fun main() {
    val asm = Assembler(
        gen = IntArray(1 shl 17),
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
        SimpleMemory(asm.gen),
        StandardInterrupts.getInterruptTable()
    )

    while (vm.running) {
        vm.tick()
        vm.debug()
    }

    println("Took ${vm.ticksElapsed()} ticks to execute!")
}