package me.alex_s168.stackvm2.ir

import com.github.ajalt.mordant.rendering.TextColors
import me.alex_s168.stackvm2.ir.token.Token
import kotlin.math.max
import kotlin.system.exitProcess
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import me.alex_s168.stackvm2.ir.ct.SpecialFunctions
import me.alex_s168.stackvm2.ir.ct.minimizeCT
import me.alex_s168.stackvm2.ir.exception.HandledException
import me.alex_s168.stackvm2.ir.types.Types

fun main() {
    val code = """
        (use! "std.asm")
        
        a := val Int! = 3
        b := val Int! = a
        c := val Int! = (add! a b)
        
        d := val RefArr<FlatArr>'10 = ::1 2; :3 4; :5 6;;
        
        incFun := val Fun<Int>'Int = {
            (return (add A0 1))
        }
        
        d[1] = (incFun d[1])
    """.trimIndent()
    Language.compile(code)
}

object Language {

    val TYPES = listOf(
        "val",
        "var"
    ) + Types.getTypeNames()

    var code: String = ""

    fun compile(code: String): Boolean {
        try {
            val tokens = tokenize(code)
            val (ast, _) = parse(tokens)
            SpecialFunctions.addTo(ast)
            prepare(ast)
            minimizeCT(ast)
            check(ast)

            println(ast)
            TODO()
        } catch (_: HandledException) {
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun msg(message: String, line: Int, column: Int, length: Int, mainColour: TextColors) {
        val t = Terminal()
        val lstr = "$line | "
        val lineStr = code.split('\n').getOrNull(line-1) ?: ""
        val before = lineStr.substring(0..<(column - 1))
        val mid = lineStr.substring((column - 1)..<(column - 1 + length))
        val after = lineStr.substring(column - 1 + length)
        t.print(gray(lstr))
        t.print(before)
        t.print(TextStyles.bold(mid))
        t.println(after)
        val l2str = " ".repeat(lstr.length + column - 1)
        t.print(l2str)
        t.println(mainColour('^' + "~".repeat(max(0, length - 1))))
        t.print(" ".repeat(max(0, l2str.length - message.length / 2)))
        t.print(mainColour(message))
        t.println(" (${gray(line.toString())}:${gray(column.toString())})")
    }

    fun warn(message: String, line: Int, column: Int, length: Int) {
        msg(message, line, column, length, yellow)
    }

    fun warn(message: String, where: Token) =
        warn(message, where.line, where.column, where.value.length)

    fun exception(message: String, line: Int, column: Int, length: Int): Nothing {
        msg(message, line, column, length, red)
        throw HandledException("$message at ($line:$column)")
    }

    fun exception(message: String, where: Token): Nothing =
        exception(message, where.line, where.column, where.value.length)

}