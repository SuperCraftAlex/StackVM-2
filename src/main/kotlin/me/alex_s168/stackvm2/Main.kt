package me.alex_s168.stackvm2

import me.alex_s168.stackvm2.asm.Assembler
import me.alex_s168.stackvm2.decomp.DeCompiler
import me.alex_s168.stackvm2.disasm.DisAssembler
import me.alex_s168.stackvm2.mem.MemoryLayout
import me.alex_s168.stackvm2.format.ExecutableFormant
import me.alex_s168.stackvm2.format.LinkableFormat
import me.alex_s168.stackvm2.inst.Instructions
import me.alex_s168.stackvm2.std.StandardInterrupts
import me.alex_s168.stackvm2.target.cfgfile.TargetConfigFiles
import me.alex_s168.stackvm2.target.matchesTargetString
import me.alex_s168.stackvm2.vm.VirtualMachine
import me.alex_s168.stackvm2.vm.mem.SegmentedMemory
import java.io.File
import java.nio.ByteBuffer
import kotlin.io.path.Path
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
        "link" -> {
            if (opArgs.size < 1) {
                System.err.println("Invalid number of arguments for operation: $op!")
                exitProcess(1)
            }

            val files = opArgs.map { str ->
                File(str).also {
                    if (!it.exists()) {
                        System.err.println("File does not exist: ${it.absolutePath}!")
                        exitProcess(1)
                    }
                }
            }.filter {
                it.extension == "o"
            }

            TargetConfigFiles.index(Path(""))

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
                val targetStr = args["-target"]?.getOrNull(0) ?: VirtualMachine.TARGET_STRING
                val isaVersion = args["-isa"]?.getOrNull(0) ?: VirtualMachine.ISA_VERSION

                val target = TargetConfigFiles.find(targetStr)
                    ?: (if (File(targetStr).isFile && File(targetStr).extension == "target") (TargetConfigFiles.read(File(targetStr))) else null)

                if (target == null) {
                    System.err.println("Target not found: $targetStr!")
                    exitProcess(1)
                }

                val layout = target.layout(entryPoint, linkable.code.size, args["-ram"]?.getOrNull(0)?.toIntOrNull() ?: 1024)

                val reg = layout.getRamRegionsSortedExceptZero().firstOrNull()

                if (reg == null) {
                    System.err.println("Target is configured incorrectly: No RAM region found!")
                    exitProcess(1)
                }

                val ramOff = reg.start

                val special = HashMap<String, Int>()

                special["_ENTRY_"] = entryPoint
                special["_TARGET_"] = targetStr.substringAfterLast('/').substringBeforeLast('.').hashCode()
                special["_ISA_"] = isaVersion.hashCode()
                special["_RAM_"] = ramOff

                target.interrupts.forEach { (k, v) ->
                    special["_INT_$k"] = v
                }

                target.labels.forEach { (k, v) ->
                    special[k] = v
                }

                linkable.linkWith(special)

                if (entry !in linkable.labels) {
                    System.err.println("Entry point label not found: $entry!")
                    exitProcess(1)
                }

                linkable.code = intArrayOf(
                    Instructions.JUMP.id,
                    linkable.labels[entry]!! + 2
                ) + linkable.code

                if (linkable.unresolved.size > 0) {
                    System.err.println("Unresolved labels:")
                    linkable.unresolved.forEach { (k, v) ->
                        System.err.println("- \"$k\" at $v")
                    }
                    exitProcess(1)
                }

                val out = File(args["-o"]?.getOrNull(0) ?: "a.svm")

                if (!out.isFile)
                    out.createNewFile()

                val exec = ExecutableFormant(
                    isaVersion,
                    targetStr,
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

            TargetConfigFiles.index(file.toPath().parent ?: Path(""))
            TargetConfigFiles.index(Path(""))

            val bytes = file.readBytes()
            val exec = ExecutableFormant.from(ByteBuffer.wrap(bytes))

            if (exec.isaVersion != VirtualMachine.ISA_VERSION)
                System.err.println("Warning: ISA version mismatch! Expected ${VirtualMachine.ISA_VERSION}, got ${exec.isaVersion}!")

            if (!exec.targetString.matchesTargetString(VirtualMachine.TARGET_STRING))
                System.err.println("Warning: Target mismatch! Expected ${VirtualMachine.TARGET_STRING}, got ${exec.targetString}!")

            val ramsize = args["-ram"]?.getOrNull(0)?.toInt() ?: 1024

            val layout = if ("-target" in args) (
                TargetConfigFiles.find(args["-target"]!![0]) ?: (
                    if (File(args["-target"]!![0]).isFile && File(args["-target"]!![0]).extension == "target") (
                        TargetConfigFiles.read(File(args["-target"]!![0]))
                    ) else null
                ) ?: throw Exception("Target not found: ${args["-target"]!![0]}!")
            ).layout(
                    exec.entryPoint,
                    exec.code.size,
                    ramsize
            ) else MemoryLayout.new()
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
        "disasm", "decomp" -> {
            if (opArgs.size != 1) {
                System.err.println("Invalid number of arguments for operation: $op!")
                exitProcess(1)
            }

            val file = File(opArgs[0])
            if (!file.exists()) {
                System.err.println("File does not exist: ${file.absolutePath}!")
                exitProcess(1)
            }

            val buf = ByteBuffer.wrap(file.readBytes())

            val (c, off) = when (file.extension) {
                "o" -> {
                    val f = LinkableFormat.from(buf)
                    f.code to f.offset
                }
                "svm" -> {
                    val f = ExecutableFormant.from(buf)
                    f.code to f.entryPoint
                }
                else -> {
                    System.err.println("Invalid file extension: ${file.extension}!")
                    exitProcess(1)
                }
            }

            if (op == "decomp") {
                println(DeCompiler.decomp(DisAssembler(c).disassemble(off), c))
            }
            else {
                println(DisAssembler(c).disassemble(off))
            }

            System.err.println("Done!")
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
    println("  link <files> -o <output file> [-entry <entry label name>] [-entryPos <entry pos>] [-target <target>] [-isa <isa version>] [-exec]")
    println("    links the given linkable files (.o) into one file")
    println("    (the \"-exec\" parameter generates an executable file (.svm) instead of a linkable file)")
    println("  asm <file> [-o <output file>]")
    println("    assembles the file and generates a linkable file")
    println("  run <file> [-ram <ram size>] [-target <target>]")
    println("    executes a SVM-Executable (.svm) file")
    println("    (\"-target <target>\" specifies the memory layout to use)")
    println("  disasm <file>")
    println("    disassembles the given linkable (.o) or executable (.svm) file")
    println("  decomp <file>")
    println("    \"decomp\" like \"disasm\" but shows a more human readable version of the code")
    println()
    println("Found any bugs or need help? Visit https://github.com/SuperCraftAlex/StackVM-2")
}