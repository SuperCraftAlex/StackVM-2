package me.alex_s168.stackvm2.ir

import me.alex_s168.stackvm2.ir.token.Token
import me.alex_s168.stackvm2.ir.token.TokenType

fun tokenize(code: String): List<Token> {
    val tokens = mutableListOf<Token>()
    var line = 1
    var column = 1
    var i = 0
    while (i < code.length) {
        val c = code[i]
        when (c) {
            ' ', '\t' -> {
                column++
            }
            '\n' -> {
                tokens.add(Token(TokenType.END_OF_LINE, "", line, column))
                line++
                column = 1
            }
            '(' -> {
                tokens.add(Token(TokenType.PARENTHESIS_OPEN, "(", line, column))
                column++
            }
            ')' -> {
                tokens.add(Token(TokenType.PARENTHESIS_CLOSE, ")", line, column))
                column++
            }
            '[' -> {
                tokens.add(Token(TokenType.SQUARE_BRACKET_OPEN, "[", line, column))
                column++
            }
            ']' -> {
                tokens.add(Token(TokenType.SQUARE_BRACKET_CLOSE, "]", line, column))
                column++
            }
            '{' -> {
                tokens.add(Token(TokenType.CURLY_BRACKET_OPEN, "{", line, column))
                column++
            }
            '}' -> {
                tokens.add(Token(TokenType.CURLY_BRACKET_CLOSE, "}", line, column))
                column++
            }
            '<' -> {
                tokens.add(Token(TokenType.ANGLE_BRACKET_OPEN, "<", line, column))
                column++
            }
            '>' -> {
                tokens.add(Token(TokenType.ANGLE_BRACKET_CLOSE, ">", line, column))
                column++
            }
            '\'' -> {
                tokens.add(Token(TokenType.APOSTROPHE, "'", line, column))
                column++
            }
            ',' -> {
                tokens.add(Token(TokenType.COMMA, ",", line, column))
                column++
            }
            ':' -> {
                tokens.add(Token(TokenType.COLON, ":", line, column))
                column++
            }
            '=' -> {
                tokens.add(Token(TokenType.ASSIGNMENT, "=", line, column))
                column++
            }
            ';' -> {
                tokens.add(Token(TokenType.END_OF_STATEMENT, ";", line, column))
                column++
            }
            '#' -> {
                while (i < code.length && code[i] != '\n') {
                    i++
                }
                i--
            }
            '*' -> {
                tokens.add(Token(TokenType.STAR, "*", line, column))
                column++
            }
            '&' -> {
                tokens.add(Token(TokenType.AND, "&", line, column))
                column++
            }
            '%' -> {
                tokens.add(Token(TokenType.MODULO, "%", line, column))
                column++
            }
            else -> {
                val start = i
                while (i < code.length
                    && code[i] != ' '
                    && code[i] != '\t'
                    && code[i] != '\n'
                    && code[i] != '('
                    && code[i] != ')'
                    && code[i] != '['
                    && code[i] != ']'
                    && code[i] != '{'
                    && code[i] != '}'
                    && code[i] != '<'
                    && code[i] != '>'
                    && code[i] != '\''
                    && code[i] != ','
                    && code[i] != ':'
                    && code[i] != '='
                    && code[i] != ';'
                    && code[i] != '#'
                    && code[i] != '*'
                    && code[i] != '&'
                    && code[i] != '%'
                ) {
                    i++
                }
                val end = i
                tokens.add(Token(TokenType.IDENTIFIER, code.substring(start, end), line, column))
                column += end - start
                i--
            }
        }
        i++
    }
    return tokens
}