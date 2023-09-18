package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.*

fun setParent(node: ASTNode, parent: ASTNode) {
    node.parent = parent
    node.children.forEach { setParent(it, node) }
    try {
        node.rebuildFromChildren()
    } catch (e: Exception) {
        // ignore
    }
}
