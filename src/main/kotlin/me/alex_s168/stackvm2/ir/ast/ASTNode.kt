package me.alex_s168.stackvm2.ir.ast

import me.alex_s168.stackvm2.ir.`var`.Variable

open class ASTNode(
    var children: MutableList<ASTNode>,
    val line: Int,
    val column: Int,
    val length: Int,
    var parent: ASTNode?
) {

    val variables: MutableMap<String, Variable> = mutableMapOf()

    override fun toString(): String =
        toString(0)

    open fun toString(indent: Int): String {
        val indString4 = " ".repeat(indent * 4)
        val sb = StringBuilder()
        sb.append(" ".repeat(indent * 2))
        sb.append("- ")
        sb.append(this::class.simpleName)
        sb.append(" @")
        sb.append(line)
        sb.append(':')
        sb.append(column)
        sb.append('\n')
        toStringExtra().map {
            it.split("\n")
        }.flatten().forEach {
            sb.append(indString4)
            sb.append(it)
            sb.append('\n')
        }
        if (toStringShowChildren()) {
            children.forEach {
                sb.append(it.toString(indent + 1))
            }
        }
        return sb.toString()
    }

    open fun toStringShowChildren(): Boolean =
        true

    open fun toStringExtra(): List<String> =
        emptyList()

    open fun rebuildFromChildren() {

    }

    open fun rebuildChildren() {

    }

}