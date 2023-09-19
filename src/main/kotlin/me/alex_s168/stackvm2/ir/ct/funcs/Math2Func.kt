package me.alex_s168.stackvm2.ir.ct.funcs

import me.alex_s168.stackvm2.ir.Language
import me.alex_s168.stackvm2.ir.ast.ASTIntLiteralNode
import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.ct.SpecialFunc
import me.alex_s168.stackvm2.ir.ct.minimizeCT
import me.alex_s168.stackvm2.ir.types.Type

class Math2Func(
    name: String,
    val op: (Int, Int) -> Int
): SpecialFunc(
    name = name,
    args = listOf(Type("Int!"), Type("Int!"))
) {

    override fun run(args: List<ASTNode>): ASTNode {
        val a = minimizeCT(args[0])
        val b = minimizeCT(args[1])

        if (a !is ASTIntLiteralNode)
            Language.exception(
                "Expected value of type \"Int!\"!",
                a.line,
                a.column,
                a.length
            )

        if (b !is ASTIntLiteralNode)
            Language.exception(
                "Expected value of type \"Int!\"!",
                a.line,
                a.column,
                a.length
            )

        return ASTIntLiteralNode(
            value = op(a.value, b.value),
            a.line,
            a.column,
            a.length,
            a.parent
        )
    }

}