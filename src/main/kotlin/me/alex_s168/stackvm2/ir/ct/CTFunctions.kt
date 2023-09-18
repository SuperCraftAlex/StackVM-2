package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.ast.ASTIntLiteralNode
import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.exception.InvalidCTArgumentsException
import me.alex_s168.stackvm2.ir.`var`.Type

object CTFunctions {

    private val FUNCS = HashMap<String, CTFunc>()

    init {
        reg("add!", listOf(Type("Int!"), Type("Int!"))) { (aIn, bIn) ->
            val a = minimizeCT(aIn)
            val b = minimizeCT(bIn)

            if (a !is ASTIntLiteralNode)
                throw InvalidCTArgumentsException()

            if (b !is ASTIntLiteralNode)
                throw InvalidCTArgumentsException()

            ASTIntLiteralNode(a.value + b.value, a.line, a.column, a.length, a.parent)
        }
    }

    fun reg(
       name: String,
       args: List<Type>,
       run: (List<ASTNode>) -> ASTNode?
    ) {
       FUNCS[name] = CTFunc(name, args, run)
    }

    operator fun get(name: String): CTFunc? =
        FUNCS[name]

}