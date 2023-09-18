package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.`var`.Type
import me.alex_s168.stackvm2.ir.`var`.Variable
import me.alex_s168.stackvm2.ir.`var`.getTypeGenerics
import me.alex_s168.stackvm2.ir.`var`.getVariable

fun prepare(node: ASTNode) {
    node.children.forEach {
        prepare(it)
    }
    if (node is ASTVariableCreationNode) {
        if (node.left !is ASTVariableNode)
            Language.exception(
                "Left side of variable creation must be a valid identifier!",
                node.line,
                node.column,
                node.length
            )
        val name = (node.left as ASTVariableNode).name
        node.getVariable(name)?.let {
            Language.exception(
                "Variable $name already defined in the given context!",
                node.line,
                node.column,
                node.length
            )
        }

        var type: Type? = null
        var mutable = true
        var variable = false

        node.types.forEach {
            if (it !is ASTTypeNode)
                Language.exception(
                    "Invalid type!",
                    it.line,
                    it.column,
                    it.length
                )
            when (it.name) {
                "Val" -> {
                    mutable = false
                    variable = true
                }
                "Var" -> {
                    mutable = true
                    variable = true
                }
                else -> {
                    type?.let { t ->
                        Language.exception(
                            "Variable type already specified!",
                            it.line,
                            it.column,
                            it.length
                        )
                    }
                    val generics = getTypeGenerics(it)
                    if (it.arrSize != null) {
                        val arrSize = it.arrSize!!
                        if (arrSize is ASTIntLiteralNode) {
                            type = Type(it.name, arrSize = arrSize.value, generics = generics)
                        } else if (arrSize is ASTTypeNode) {
                            type = Type(it.name, retType = Type(arrSize.name, generics = getTypeGenerics(arrSize)), generics = generics)
                        }
                        else {
                            Language.exception(
                                "Specifier must be an Integer or Type!",
                                arrSize.line,
                                arrSize.column,
                                arrSize.length
                            )
                        }
                    }
                    else {
                        type = Type(it.name, generics = generics)
                    }
                }
            }
        }

        if (type == null) {
            Language.exception(
                "Variable type must be specified!",
                node.line,
                node.column,
                node.length
            )
        }

        val v = Variable(
            name,
            type!!,
            mutable,
            node.line,
            node.column,
            node.length
        )

        if (node.parent == null)
            throw Exception("Parent is null!")

        node.parent?.variables?.put(name, v)
    }
}