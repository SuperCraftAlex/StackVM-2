package me.alex_s168.stackvm2.ir.token

data class Token(
    val type: TokenType,
    val value: String,
    val line: Int,
    val column: Int
)