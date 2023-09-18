package me.alex_s168.stackvm2.ir.ast

open class ASTNode(
    val children: List<ASTNode>,
    val line: Int,
    val column: Int
) {
    override fun toString(): String =
        toString(0)

    open fun toString(indent: Int): String {
        val indString4 = " ".repeat(indent * 4)
        val sb = StringBuilder()
        sb.append(" ".repeat(indent * 2))
        sb.append("- ")
        sb.append(this::class.simpleName)
        sb.append(" at ")
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
}