package me.alex_s168.stackvm2.ir.ast

class ASTDerefNode(
    val value: ASTNode,
    line: Int,
    column: Int
): ASTNode(listOf(value), line, column)