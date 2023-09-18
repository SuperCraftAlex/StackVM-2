package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.token.Token
import me.alex_s168.stackvm2.ir.token.TokenType

fun parse(tokens: List<Token>, off: Int = 0, until: TokenType? = null): Pair<ASTBlockNode, Int> {
    val nodes = mutableListOf<ASTNode>()

    var i = off
    if (i >= tokens.size) {
        return ASTBlockNode(nodes, 0, 0) to 0
    }
    val (firstLine, firstColumn) = tokens[i].line to tokens[i].column
    while (i < tokens.size) {
        val token = tokens[i]
        if (token.type == until) {
            return ASTBlockNode(nodes, firstLine, firstColumn) to i - off
        }
        when (token.type) {
            TokenType.IDENTIFIER, TokenType.STAR -> {
                val (left, usedTokens) = parseExpression(tokens, i)
                i += usedTokens
                if (i < tokens.size && tokens[i].type == TokenType.ASSIGNMENT) {
                    val (right, usedTokens2) = parseExpression(tokens, i + 1)
                    nodes.add(ASTAssignmentNode(left, right, token.line, token.column))
                    i += usedTokens2 + 1
                } else if (i < tokens.size && tokens[i].type == TokenType.COLON) {
                    if (i + 1 < tokens.size && tokens[i + 1].type == TokenType.ASSIGNMENT) {
                        i += 2
                        val types = mutableListOf<ASTNode>()
                        while (i < tokens.size && tokens[i].type == TokenType.IDENTIFIER) {
                            val (type, usedTokens2) = parseExpression(tokens, i)
                            if (type !is ASTTypeNode) {
                                Language.exeption("Expected type", tokens[i])
                            }
                            types.add(type)
                            i += usedTokens2
                        }

                        if (i < tokens.size && tokens[i].type == TokenType.ASSIGNMENT) {
                            val (right, usedTokens2) = parseExpression(tokens, i + 1)
                            nodes.add(ASTVariableCreationNode(left, types, default = right, line = token.line, column = token.column))
                            i += usedTokens2 + 1
                        }
                        else {
                            nodes.add(ASTVariableCreationNode(left, types, line = token.line, column = token.column))
                        }

                        if (i < tokens.size && tokens[i].type != TokenType.END_OF_LINE) {
                            Language.exeption("Unexpected token", tokens[i])
                        }
                    } else {
                        Language.exeption("Unexpected token", tokens[i + 2])
                    }
                }
            }
            TokenType.END_OF_LINE -> {
                i++
            }
            TokenType.PARENTHESIS_OPEN -> {
                val (expr, usedTokens) = parseExpression(tokens, i)
                nodes.add(expr)
                i += usedTokens
            }
            else -> {
                Language.exeption("Unexpected token", token)
            }
        }
    }

    return ASTBlockNode(nodes, firstLine, firstColumn) to i - off
}
