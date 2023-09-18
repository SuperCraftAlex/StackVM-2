package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.token.Token

fun main() {
    val code = """
        a := Static Val Int = 3
        b := Static Val Int = a
        c := Val Int = (add a b)
        
        d := Val RefArr<Int>'10 = ::1 2; :3 4; :5 6;;
        
        incFun := Fun<Int>'1 = {
            (return (add A[0] 1))
        }
        
        d[1] = (incFun d[1])
    """.trimIndent()
    val tokens = tokenize(code)
    val (ast, _) = parse(tokens)
    println(ast)
}

class ParseException(message: String): Exception(message)

object Language {

    val TYPES = arrayOf(
        "Int",
        "Ref",
        "FlatArr",
        "RefArr",
        "Static",
        "Val",
        "Var",
        "Fun",
        "Any",
        "Type"
    )

    fun exeption(message: String, where: Token): Nothing {
        throw ParseException("$message at ${where.line}:${where.column} (token: $where))")
    }

}