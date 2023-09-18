package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.ast.*
import me.alex_s168.stackvm2.ir.token.Token
import me.alex_s168.stackvm2.ir.token.TokenType

fun parseExpression(tokens: List<Token>, off: Int): Pair<ASTNode, Int> {
    var i = off
    val token = tokens[i]
    when (token.type) {
        TokenType.END_OF_LINE -> {}
        TokenType.COLON -> {
            val elems = mutableListOf<ASTNode>()
            while (i + 1 < tokens.size && tokens[i + 1].type != TokenType.SEMICOLON) {
                val (elem, used) = parseExpression(tokens, i + 1)
                i += used
                elems += elem
                if (i >= tokens.size) {
                    Language.exeption("Expected semicolon at end of list", tokens[i - 1])
                }
            }
            i += 1
            return Pair(ASTArrayLiteralNode(elems, token.line, token.column), i - off + 1)
        }
        TokenType.STAR -> {
            if (i + 1 >= tokens.size) {
                Language.exeption("Expected expression", token)
            }
            val (right, usedTokens) = parseExpression(tokens, i + 1)
            return Pair(ASTDerefNode(right, token.line, token.column), 1 + usedTokens)
        }
        TokenType.AND -> {
            if (i + 1 >= tokens.size) {
                Language.exeption("Expected expression", token)
            }
            val (right, usedTokens) = parseExpression(tokens, i + 1)
            return Pair(ASTAddressOfNode(right, token.line, token.column), 1 + usedTokens)
        }
        TokenType.IDENTIFIER -> {
            token.value.toIntOrNull()?.let {
                return Pair(ASTIntLiteralNode(it, token.line, token.column), 1)
            }

            if ((token.value.lastOrNull() == 'B' && token.value.dropLast(1).toIntOrNull() != null)
                || token.value in Language.TYPES
            ) {
                val genericTypes = mutableListOf<ASTTypeNode>()
                if (i + 1 < tokens.size && tokens[i + 1].type == TokenType.ANGLE_BRACKET_OPEN) {
                    i += 1
                    while (tokens[i + 1].type != TokenType.ANGLE_BRACKET_CLOSE) {
                        val (type, usedTokens) = parseExpression(tokens, i + 1)
                        if (type !is ASTTypeNode) {
                            Language.exeption("Expected type", tokens[i + 2])
                        }
                        genericTypes.add(type)
                        i += usedTokens
                        if (i >= tokens.size) {
                            Language.exeption("Expected closing angle bracket", tokens[i - 1])
                        }
                    }
                    i += 1
                }
                var arrSize: ASTNode? = null
                if (i + 1 < tokens.size && tokens[i + 1].type == TokenType.APOSTROPHE) {
                    val (size, usedTokens) = parseExpression(tokens, i + 2)
                    arrSize = size
                    i += usedTokens + 1
                }
                return Pair(ASTTypeNode(token.value, genericTypes, arrSize, token.line, token.column), i - off + 1)
            }

            if (i + 1 < tokens.size && tokens[i + 1].type == TokenType.SQUARE_BRACKET_OPEN) {
                val (index, usedTokens) = parseExpression(tokens, i + 2)
                i += usedTokens
                if (i + 2 >= tokens.size || tokens[i + 2].type != TokenType.SQUARE_BRACKET_CLOSE) {
                    Language.exeption("Expected closing square bracket", tokens[i + 2])
                }
                return Pair(ASTArrayIndexNode(ASTVariableNode(token.value, token.line, token.column), index, token.line, token.column), usedTokens + 3)
            }

            return Pair(ASTVariableNode(token.value, token.line, token.column), 1)
        }
        TokenType.PARENTHESIS_OPEN -> {
            if (i + 1 >= tokens.size) {
                Language.exeption("Expected function name", token)
            }
            val args = mutableListOf<ASTNode>()
            while (tokens[i+1].type != TokenType.PARENTHESIS_CLOSE) {
                val (arg, usedTokens) = parseExpression(tokens, i + 1)
                args.add(arg)
                i += usedTokens
                if (i >= tokens.size) {
                    Language.exeption("Expected closing parenthesis", tokens[i - 1])
                }
            }
            if (args.isEmpty()) {
                Language.exeption("Expected function name", tokens[i + 1])
            }
            return Pair(ASTFunctionCallNode(args.first(), args.subList(1, args.size), token.line, token.column), 2 + i - off)
        }
        TokenType.CURLY_BRACKET_OPEN -> {
            val (block, used) = parse(tokens, i + 1, TokenType.CURLY_BRACKET_CLOSE)
            return Pair(block, 2 + used)
        }
        else -> {
            Language.exeption("Unexpected token", token)
        }
    }
    return Pair(ASTNode(emptyList(), 0, 0), i)
}
