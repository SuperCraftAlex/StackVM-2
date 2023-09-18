package me.alex_s168.stackvm2.ir.ast

class ASTArrayLiteralNode(
    val value: List<ASTNode>,
    line: Int,
    column: Int
): ASTNode(value, line, column)