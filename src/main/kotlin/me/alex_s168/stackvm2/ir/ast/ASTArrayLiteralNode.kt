package me.alex_s168.stackvm2.ir.ast

class ASTArrayLiteralNode(
    val value: MutableList<ASTNode>,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children = value,
    line,
    column,
    length,
    parent
)