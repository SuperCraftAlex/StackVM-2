package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.ast.ASTNode
import me.alex_s168.stackvm2.ir.ct.funcs.*
import me.alex_s168.stackvm2.ir.types.Type
import me.alex_s168.stackvm2.ir.`var`.Variable

object SpecialFunctions {

    private val FUNCS = HashMap<String, SpecialFunc>()

    init {
        FUNCS["add!"] = Math2Func("add!") { a, b -> a + b }
        FUNCS["sub!"] = Math2Func("sub!") { a, b -> a - b }
        FUNCS["mul!"] = Math2Func("mul!") { a, b -> a * b }
        FUNCS["div!"] = Math2Func("div!") { a, b -> a / b }
        FUNCS["mod!"] = Math2Func("mod!") { a, b -> a % b }

        FUNCS["use!"] = UseFunc()
    }

    fun addTo(node: ASTNode) {
        FUNCS.forEach {
            node.variables[it.key] = Variable(
                it.key,
                Type("Fun!"),
                false,
                1,
                1,
                1
            )
        }
    }

    operator fun get(name: String): SpecialFunc? =
        FUNCS[name]

}