package me.alex_s168.stackvm2

import me.alex_s168.stackvm2.asm.Assembler
import me.alex_s168.stackvm2.common.MemoryLayout
import me.alex_s168.stackvm2.format.ExecutableFormant
import me.alex_s168.stackvm2.format.LinkableFormat
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.target.TargetRef
import me.alex_s168.stackvm2.target.Targets
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SegmentedMemory
import java.io.File
import java.nio.ByteBuffer
import kotlin.system.exitProcess

val HELP_ARG_OPS = arrayOf("help", "h")
val HELP_ARGS = arrayOf("-h", "--help", "-help", "--h")

fun main(argsIn: Array<String>) {
    val args = HashMap<String, ArrayList<String>>()

    var currentKey = ""
    args[currentKey] = ArrayList()
    for (arg in argsIn) {
        if (arg.startsWith("-")) {
            currentKey = arg
            args[currentKey] = ArrayList()
            continue
        }
        args[currentKey]!!.add(arg)
    }

    val op = args[""]!!.getOrNull(0)

    if (op == null) {
        System.err.println("No operation specified!")
        exitProcess(1)
    }

    val opArgs = args[""]!!.subList(1, args[""]!!.size)

    if (op in HELP_ARG_OPS) {
        showHelp()
        return
    }

    if (HELP_ARGS.any { it in args.keys }) {
        showHelp()
        return
    }

    when (op) {
        //link test.o -exec -o test.svm
        "link" -> {
            if (opArgs.size < 1) {
                System.err.println("Invalid number of arguments for operation: $op!")
                exitProcess(1)
            }

            val files = opArgs.map {
                File(it).also {
                    if (!it.exists()) {
                        System.err.println("File does not exist: ${it.absolutePath}!")
                        exitProcess(1)
                    }
                }
            }

            val entryPoint = args["-entryPos"]?.getOrNull(0)?.toIntOrNull() ?: 512

            val formats = files.map {
                val bytes = it.readBytes()
                LinkableFormat.from(ByteBuffer.wrap(bytes))
            }

            val linkable = LinkableFormat.empty(off = entryPoint)
            formats.forEach {
                try {
                    linkable.linkWith(it)
                } catch (e: Exception) {
                    System.err.println("Error while linking: ${e.message}!")
                    exitProcess(1)
                }
            }

            if ("-exec" in args) {
                val entry = args["-entry"]?.getOrNull(0) ?: "_start"
                val target = args["-target"]?.getOrNull(0) ?: VirtualMachine.TARGET_STRING
                val isaVersion = args["-isa"]?.getOrNull(0) ?: VirtualMachine.ISA_VERSION
                val ramOff = args["-ram"]?.getOrNull(0)?.toIntOrNull() ?: (entryPoint + linkable.code.size)

                val special = HashMap<String, Int>()

                special["_ENTRY_"] = entryPoint
                special["_TARGET_"] = target.hashCode()
                special["_ISA_"] = isaVersion.hashCode()
                special["_RAM_"] = ramOff

                val targetRef = TargetRef.from(target)

                val interrupts = Targets.getInterruptTable(targetRef)

                interrupts.forEach { (k, v) ->
                    special["_INT_${v.first}"] = k
                }

                linkable.linkWith(special)

                if (entry !in linkable.labels) {
                    System.err.println("Entry point label not found: $entry!")
                    exitProcess(1)
                }

                linkable.code = intArrayOf(
                    Targets.getJumpInst(targetRef),
                    linkable.labels[entry]!! + 2
                ) + linkable.code

                val out = File(args["-o"]?.getOrNull(0) ?: "a.svm")

                if (!out.isFile)
                    out.createNewFile()

                val exec = ExecutableFormant(
                    isaVersion,
                    target,
                    entryPoint,
                    linkable.code
                )

                val buf = ByteBuffer.allocate(exec.size())
                exec.save(buf)
                out.writeBytes(buf.array())

                System.err.println("Done!")
                return
            }

            val out = File(args["-o"]?.getOrNull(0)
                ?: ((if (files[0].parent == null) "" else (files[0].parent + '/')) +
                        files[0].nameWithoutExtension + ".o"))

            if (!out.isFile)
                out.createNewFile()

            val buf = ByteBuffer.allocate(linkable.size())
            linkable.save(buf)
            out.writeBytes(buf.array())

            System.err.println("Done!")
        }
        "asm" -> {
            if (opArgs.size != 1) {
                System.err.println("Invalid number of arguments for operation: $op!")
                exitProcess(1)
            }

            val file = File(opArgs[0])
            if (!file.exists()) {
                System.err.println("File does not exist: ${file.absolutePath}!")
                exitProcess(1)
            }
            val code = file.readText()

            val asm = Assembler(gen = mutableListOf())
                .addSource(code)
                .resolveLocalLabels()
                .complete()

            if (asm.errors.isNotEmpty()) {
                System.err.println("Errors:")
                asm.errors.forEach { err ->
                    System.err.println(err)
                }
                exitProcess(1)
            }

            val globals = asm.getGlobalLabels()
            val unresolved = asm.getUnresolvedLabels().toMutableList()

            val format = LinkableFormat(
                globals,
                unresolved,
                asm.gen.toIntArray()
            )

            val out = File(args["-o"]?.getOrNull(0)
                ?: ((if (file.parent == null) "" else (file.parent + '/')) +
                        file.nameWithoutExtension + ".o"))

            if (!out.isFile)
                out.createNewFile()

            val buf = ByteBuffer.allocate(format.size())
            format.save(buf)
            out.writeBytes(buf.array())

            System.err.println("Done!")
        }
        "run" -> {
            if (opArgs.size != 1) {
                System.err.println("Invalid number of arguments for operation: $op!")
                exitProcess(1)
            }

            val file = File(opArgs[0])
            if (!file.exists()) {
                System.err.println("File does not exist: ${file.absolutePath}!")
                exitProcess(1)
            }
            val bytes = file.readBytes()
            val exec = ExecutableFormant.from(ByteBuffer.wrap(bytes))

            if (exec.isaVersion != VirtualMachine.ISA_VERSION)
                System.err.println("Warning: ISA version mismatch! Expected ${VirtualMachine.ISA_VERSION}, got ${exec.isaVersion}!")

            if (exec.targetString != VirtualMachine.TARGET_STRING)
                System.err.println("Warning: Target mismatch! Expected ${VirtualMachine.TARGET_STRING}, got ${exec.targetString}!")

            val ramsize = args["-ram"]?.getOrNull(0)?.toInt() ?: 1024

            val layout = MemoryLayout.new()
                .ram(0..<exec.entryPoint)
                .rom(exec.entryPoint..<exec.entryPoint+exec.code.size)
                .ram(exec.entryPoint+exec.code.size..<exec.entryPoint+exec.code.size+ramsize)
                .build()

            val mem = SegmentedMemory.of(layout).also {
                it.set(exec.entryPoint, exec.code)
            }.finalize()

            val vm = VirtualMachine(
                mem,
                exec.entryPoint,
                StandardInterrupts.getInterruptTable()
            )

            while (vm.running) {
                vm.tick()
            }

            System.err.println("Took ${vm.ticksElapsed()} ticks to execute!")
            System.err.println(vm)
        }
        else -> {
            System.err.println("Invalid operation: $op!")
            exitProcess(1)
        }
    }
}

private fun showHelp() {
    println("StackVM2 CLI")
    println("Usage: svm <operation> [args]")
    println()
    println("Operations:")
    println("  link <files> -o <output file> [-entry <entry label name>] [-entryPos <entry pos>] [-target <target>] [-isa <isa version>] [-ram <ram size>] [-exec]")
    println("    (\"-exec\" generates an executable file instead of a linkable file)")
    println("  asm <file> [-o <output file>]")
    println("  run <file> [-ram <ram size>]")
    println()
    println("Found any bugs or need help? Go to https://github.com/SuperCraftAlex/StackVM-2")
}