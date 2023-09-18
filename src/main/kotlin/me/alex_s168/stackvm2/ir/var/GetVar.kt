package me.alex_s168.stackvm2.ir.`var`

import me.alex_s168.stackvm2.ir.ast.ASTNode

fun getVariablesAvailableIn(node: ASTNode): Map<String, Variable> {
    val vars = mutableMapOf<String, Variable>()
    vars.putAll(node.variables)
    node.parent?.let { vars.putAll(getVariablesAvailableIn(it)) }
    return vars
}

fun ASTNode.getVariable(name: String): Variable? =
    getVariablesAvailableIn(this)[name]