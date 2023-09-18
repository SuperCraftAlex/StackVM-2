package me.alex_s168.stackvm2.ir.ast

class ASTAddressOfNode(
    val value: ASTNode,
    line: Int,
    column: Int
): ASTNode(listOf(value), line, column)