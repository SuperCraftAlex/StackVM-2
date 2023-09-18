package me.alex_s168.stackvm2.ir.ct

import me.alex_s168.stackvm2.ir.Language
import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.`var`.getType
import me.alex_s168.stackvm2.ir.`var`.getVariable

fun minimizeCT(node: ASTNode): ASTNode {
    if (node is ASTBlockNode) {
        node.children.forEach { minimizeCT(it) }
        return node
    }

    if (node is ASTVariableNode) {
        val v = node.getVariable(node.name)
            ?: Language.exception("Variable '${node.name}' not found!", node.line, node.column, node.length)

        if (v.defaultValue == null)
            return node

        minimizeCT(v.defaultValue!!)
        if (v.type.ct) {
            if (v.defaultValue!! is ASTVariableNode) {
                return minimizeCT(v.defaultValue!!)
            }
            if (node !in node.parent!!.children) {
                return v.defaultValue!!
            }
            node.parent!!.children[node.parent!!.children.indexOf(node)] = v.defaultValue!!
            node.parent!!.rebuildFromChildren()
            return v.defaultValue!!
        }
    }

    if (node is ASTFunctionCallNode) {
        if (node.func is ASTVariableNode) {
            SpecialFunctions[(node.func as ASTVariableNode).name]?.let { f ->
                if (node.arguments.size > f.args.size) {
                    val firstInvalidArg = node.arguments[f.args.size]
                    Language.exception(
                        "Too many arguments! Expected: ${f.args.size}, got: ${node.arguments.size}",
                        firstInvalidArg.line,
                        firstInvalidArg.column,
                        node.arguments.last().column - firstInvalidArg.column + node.arguments.last().length
                    )
                }
                if (node.arguments.size < f.args.size) {
                    Language.exception(
                        "Too few arguments! Expected: ${f.args.size}, got: ${node.arguments.size}",
                        node.line,
                        node.column,
                        node.length
                    )
                }
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
                val ret = f.run(node.arguments) ?: ASTNode(
                    mutableListOf(),
                    node.line,
                    node.column,
                    node.length,
                    node.parent
                )
                if (node.parent?.children?.contains(node) == true) {
                    node.parent?.children?.set(node.parent!!.children.indexOf(node), ret)
                    node.parent?.rebuildFromChildren()
                }
                return ret
            }
        }
        return node
    }

    return node
}