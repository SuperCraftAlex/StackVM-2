package me.alex_s168.stackvm2.ir.ast

class ASTBlockNode(
    children: List<ASTNode>,
    line: Int,
    column: Int
): ASTNode(children, line, column)

