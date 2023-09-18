package me.alex_s168.stackvm2.ir.ast

class ASTBlockNode(
    children: MutableList<ASTNode>,
    line: Int,
    column: Int,
    length: Int,
    parent: ASTNode?
): ASTNode(
    children,
    line,
    column,
    length,
    parent
)