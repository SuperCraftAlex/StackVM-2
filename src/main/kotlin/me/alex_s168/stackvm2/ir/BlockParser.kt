package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.token.Token
import me.alex_s168.stackvm2.ir.token.TokenType

fun parse(tokens: List<Token>, off: Int = 0, until: TokenType? = null): Pair<ASTBlockNode, Int> {
    var i = off

    if (i >= tokens.size)
        return ASTBlockNode(mutableListOf(), 0, 0, 0, null) to 0

    val (firstLine, firstColumn, firstLength) = Triple(tokens[i].line, tokens[i].column, tokens[i].value.length)

    val node = ASTBlockNode(mutableListOf(), firstLine, firstColumn, firstLength, null)

    while (i < tokens.size) {
        val token = tokens[i]
        if (token.type == until)
            return node to (i - off)

        when (token.type) {
            TokenType.IDENTIFIER, TokenType.STAR -> {
                val (left, usedTokens) = parseExpression(tokens, i)
                i += usedTokens
                if (i < tokens.size && tokens[i].type == TokenType.ASSIGNMENT) {
                    val (right, usedTokens2) = parseExpression(tokens, i + 1)
                    node.children += ASTAssignmentNode(left, right, token.line, token.column, token.value.length, node).also {
                        setParent(it, node)
                    }
                    i += usedTokens2 + 1
                } else if (i < tokens.size && tokens[i].type == TokenType.COLON) {
                    if (i + 1 < tokens.size && tokens[i + 1].type == TokenType.ASSIGNMENT) {
                        i += 2
                        val types = mutableListOf<ASTNode>()
                        while (i < tokens.size && tokens[i].type == TokenType.IDENTIFIER) {
                            val (type, usedTokens2) = parseExpression(tokens, i)
                            if (type !is ASTTypeNode) {
                                Language.exception("Expected type", tokens[i])
                            }
                            types.add(type)
                            i += usedTokens2
                        }

                        if (i < tokens.size && tokens[i].type == TokenType.ASSIGNMENT) {
                            val (right, usedTokens2) = parseExpression(tokens, i + 1)
                            node.children += ASTVariableCreationNode(
                                    left,
                                    types,
                                    default = right,
                                    line = token.line,
                                    column = token.column,
                                    length = token.value.length,
                                    parent = node
                                ).also {
                                setParent(it, node)
                            }
                            i += usedTokens2 + 1
                        } else {
                            node.children += ASTVariableCreationNode(
                                    left,
                                    types,
                                    line = token.line,
                                    column = token.column,
                                    length = token.value.length,
                                    parent = node
                                ).also {
                                setParent(it, node)
                            }
                        }

                        if (i < tokens.size && tokens[i].type != TokenType.END_OF_LINE) {
                            Language.exception("Unexpected token", tokens[i])
                        }
                    } else {
                        Language.exception("Unexpected token", tokens[i + 2])
                    }
                }
            }
            TokenType.END_OF_LINE -> {
                i++
            }
            TokenType.PARENTHESIS_OPEN -> {
                val (expr, usedTokens) = parseExpression(tokens, i)
                node.children += expr.also {
                    setParent(it, node)
                }
                i += usedTokens
            }
            else -> {
                Language.exception("Unexpected token", token)
            }
        }
    }

    return node to (i - off)
}
