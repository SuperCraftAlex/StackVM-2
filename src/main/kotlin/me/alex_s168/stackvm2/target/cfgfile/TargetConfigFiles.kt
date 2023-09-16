package me.alex_s168.stackvm2.target.cfgfile

import me.alex_s168.stackvm2.common.MemoryLayout
import me.alex_s168.stackvm2.target.TargetString
import me.alex_s168.stackvm2.target.matchesTargetString
import java.io.File
import java.nio.file.Path
import java.util.HashMap
import kotlin.io.path.extension
import kotlin.io.path.listDirectoryEntries

object TargetConfigFiles {

    private val targets = mutableListOf<Pair<TargetString, TargetConfig>>()

    fun index(path: Path) {
        path.listDirectoryEntries().forEach {
            if (it.extension == "target") {
                val t = read(it.toFile())
                targets.add(t.name to t)
            }
        }
    }

    fun find(name: TargetString): TargetConfig? =
        targets.find {
            it.first.matchesTargetString(name)
        }?.second

    fun read(file: File): TargetConfig {
        if (!file.exists())
            throw IllegalArgumentException("File does not exist")

        val lines = file.readLines()

        var name: String? = null
        val interrupts = HashMap<String, Int>()

        val ramRanges = mutableListOf<(prgEntryPoint: Int, progSize: Int, ramAmount: Int) -> IntRange>()
        var romRange: ((prgEntryPoint: Int, progSize: Int, ramAmount: Int) -> IntRange)? = null

        val labels = HashMap<String, Int>()

        for (lineF in lines) {
            val lineX = lineF.trim().split("#")
            if (lineX.isEmpty())
                continue

            val line = lineX[0].trim()

            if (line.isEmpty())
                continue

            val sp = line.split(":")
            val cmd = sp.first()
            val args = sp.subList(1, sp.size).joinToString(":").trim()

            when (cmd) {
                "for" -> {
                    if (name != null)
                        throw IllegalArgumentException("Name already set!")

                    name = args
                }
                "int" -> {
                    val intSp = args.split("=")
                    val intName = intSp.first().trim()
                    val intNum = intSp.last().trim().toIntOrNull()

                    if (intName in interrupts)
                        throw IllegalArgumentException("Interrupt already defined!")

                    if (intNum == null)
                        throw IllegalArgumentException("Not a number!")

                    interrupts[intName] = intNum
                }
                "rom" -> {
                    val rsp = args.split("->")
                    val start = rsp.first().trim()
                    val end = rsp.last().trim()

                    romRange = { E, S, R ->
                        val starX = start.replace("E", ";$E;").replace("S", ";$S;").replace("R", ";$R;")
                        val endX = end.replace("E", ";$E;").replace("S", ";$S;").replace("R", ";$R;")

                        val startV = starX.split(";")
                            .filter { it.trim().isNotEmpty() }
                            .sumOf { it.toIntOrNull() ?: throw IllegalArgumentException("Not a number or constant: \"$it\"!") }
                        val endV = endX.split(";")
                            .filter { it.trim().isNotEmpty() }
                            .sumOf { it.toIntOrNull() ?: throw IllegalArgumentException("Not a number or constant: \"$it\"!") }

                        startV..<endV
                    }
                }
                "ram" -> {
                    val rsp = args.split("->")
                    val start = rsp.first().trim()
                    val end = rsp.last().trim()

                    ramRanges += { E, S, R ->
                        val starX = start.replace("E", ";$E;").replace("S", ";$S;").replace("R", ";$R;")
                        val endX = end.replace("E", ";$E;").replace("S", ";$S;").replace("R", ";$R;")

                        val startV = starX.split(";")
                            .filter { it.trim().isNotEmpty() }
                            .sumOf { it.toIntOrNull() ?: throw IllegalArgumentException("Not a number or constant: \"$it\"!") }
                        val endV = endX.split(";")
                            .filter { it.trim().isNotEmpty() }
                            .sumOf { it.toIntOrNull() ?: throw IllegalArgumentException("Not a number or constant: \"$it\"!") }

                        startV..<endV
                    }
                }
                "def" -> {
                    val defSp = args.split("=")
                    val defName = defSp.first().trim()
                    val defNum = defSp.last().trim().toIntOrNull()

                    if (defName in labels)
                        throw IllegalArgumentException("Label already defined!")

                    if (defNum == null)
                        throw IllegalArgumentException("Not a number!")

                    labels[defName] = defNum
                }
                else -> {
                    throw IllegalArgumentException("Unknown command: $cmd")
                }
            }
        }

        if (name == null)
            throw IllegalArgumentException("Name not set!")

        if (romRange == null)
            throw IllegalArgumentException("ROM range not set!")

        if (ramRanges.isEmpty())
            throw IllegalArgumentException("RAM ranges not set!")

        return TargetConfig(
            name,
            { E, S, R ->
                val lRomRange = romRange(E, S, R)
                val lRamRanges = ramRanges.map { it(E, S, R) }

                val layout = MemoryLayout.new()
                layout.rom(lRomRange.first, lRomRange.last)
                lRamRanges.forEach { layout.ram(it.first, it.last) }

                layout.build()
            },
            interrupts,
            labels.toList()
        )
    }

}