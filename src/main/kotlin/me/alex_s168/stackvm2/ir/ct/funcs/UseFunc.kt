package me.alex_s168.stackvm2.ir.ct.funcs

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.ct.SpecialFunc
import me.alex_s168.stackvm2.ir.`var`.Type

class UseFunc: SpecialFunc(
    name = "use!",
    args = listOf(Type("Str!"))
) {

    override fun run(args: List<ASTNode>): ASTNode? {
        TODO()
        return null
    }

}