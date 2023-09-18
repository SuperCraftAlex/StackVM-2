package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.Language
import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.`var`.getType

fun minimizeCT(node: ASTNode) {
    if (node is ASTFunctionCallNode) {
        if (node.func is ASTVariableNode) {
            CTFunctions[(node.func as ASTVariableNode).name]?.let { f ->
                node.arguments.forEachIndexed { i, it ->
                    val arg = getType(it)
                    val farg = f.args[i]
                    if (arg != farg)
                        Language.exception(
                            "Invalid argument! Expected: $farg, got: $arg",
                            it.line,
                            it.column,
                            it.length
                        )
                }
                node.children = f.run(node.arguments)?.children ?: mutableListOf()
                node.rebuildFromChildren()
            }
        }
        return
    }

    node.children.forEach {
        minimizeCT(it)
    }
}