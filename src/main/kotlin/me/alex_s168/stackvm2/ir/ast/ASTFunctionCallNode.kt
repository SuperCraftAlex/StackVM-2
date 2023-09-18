package me.alex_s168.stackvm2.ir.ast

class ASTFunctionCallNode(
    val func: ASTNode,
    val arguments: List<ASTNode>,
    line: Int,
    column: Int
): ASTNode(listOf(func) + arguments, line, column) {

    override fun toStringShowChildren(): Boolean =
        false

    override fun toStringExtra(): List<String> =
        listOf(
            "  Func:",
            func.toString(1).trimEnd(),
            "  Args:"
        ) + arguments.map { it.toString(1).trimEnd() }

}