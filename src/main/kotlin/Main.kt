import me.alex_s168.kth.ifNotEmpty
import me.alex_s168.stackvm2.asm.Assembler
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.vm.VirtualMachine
import kotlin.system.exitProcess

fun main() {
    val asm = Assembler(HashMap(), IntArray(1 shl 17), 512)

    asm.assemble("""
    ; TEST CODE
    
    jmp test
    
    int 33
    
    test:
        ldi 97
        int 1
        
        ldi 10
        int 1
        
        int 33

    ; alloc__init:
    ;     ldi 1024
    ;     ldi 0
    ;     alloc__init__l__code_0:
    ;         swp
    ;         dup
    ;         ldi 33
    ;         swp
    ;         sts
    ;         swp
    ;     
    ;         swp
    ;         ldi 33
    ;         add
    ;         swp
    ;         inc
    ;     
    ;         dup
    ;         ldi 10
    ;         sub
    ;         poc
    ;         jmc alloc__init__l__code_0
    ; 
    ; ;   ret
    ;     int 33
    """)

    asm.errors.ifNotEmpty {
        println("Errors:")
        it.forEach { err ->
            println(err)
        }
        exitProcess(-1)
    }

    val vm = VirtualMachine(asm.gen, StandardInterrupts.getInterruptTable())

    var ok = false
    for (tick in (0..<10000)) {
        if (!vm.running) {
            ok = true
            println("Took ${tick+1} ticks to execute!")
            break
        }

        vm.debug()
        vm.tick()
    }
    if (!ok)
        println("Exceeded tick limit!")

    //while (vm.running) {
    //    vm.tick()
    //}
}