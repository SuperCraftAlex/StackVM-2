package me.alex_s168.stackvm2.ir.types

import me.alex_s168.stackvm2.ir.Language
import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.ct.minimizeCT
import me.alex_s168.stackvm2.ir.`var`.getVariable


fun getType(node: ASTNode): Type? {
    if (node is ASTStringLiteralNode) {
        return Type("Str!")
    }
    if (node is ASTBlockNode) {
        return Type("Label!")
    }
    if (node is ASTArrayLiteralNode) {
        if (node.value.isEmpty())
            return Type("FlatArr!")
        val type = getType(node.value.first())
        var ct = type?.ct ?: false
        node.value.forEach {
            minimizeCT(it)
            val t = getType(it)
            if (t != type)
                Language.exception(
                    "Every element in an array needs to have the same type!",
                    node.line,
                    node.column,
                    node.length
                )
            ct = ct || (t?.ct ?: false)
        }
        if (type == null)
            return null
        if (type.nameEq("Int"))
            return Type("FlatArr", ct = ct)

        return Type("RefArr", ct = ct, generics = listOf(type))
    }
    if (node is ASTIntLiteralNode) {
        return Type("Int!")
    }
    if (node is ASTFunctionCallNode) {
        if (node.func !is ASTVariableNode) {
            Language.exception(
                "Function name must be a variable!",
                node.line,
                node.column,
                node.length
            )
        }

        val v = node.getVariable((node.func as ASTVariableNode).name) ?: Language.exception(
            "Function ${node.func} not found!",
            node.line,
            node.column,
            node.length
        )

        if (!v.type.nameEq("Fun"))
            Language.exception(
                "Variable ${node.func} is not a function!",
                node.line,
                node.column,
                node.length
            )

        return v.type.retType
    }
    if (node is ASTVariableNode) {
        return node.getVariable(node.name)?.type ?: Language.exception(
            "Variable ${node.name} not found!",
            node.line,
            node.column,
            node.length
        )
    }
    if (node is ASTArrayIndexNode) {
        minimizeCT(node.index)
        minimizeCT(node.array)

        val indexType = getType(node.index) ?: return null
        if (!indexType.nameEq("Int"))
            Language.exception(
                "Array index must be an integer!",
                node.line,
                node.column,
                node.length
            )
        val type = getType(node.array) ?: return null
        if (type.nameEq("FlatArr"))
            return Type("Int!")
        if (type.nameEq("RefArr"))
            return type.generics.first()
        Language.exception(
            "Can only perform array indexing on arrays!",
            node.line,
            node.column,
            node.length
        )
    }
    return null
}