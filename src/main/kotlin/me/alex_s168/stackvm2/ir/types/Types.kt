package me.alex_s168.stackvm2.ir.types

import me.alex_s168.stackvm2.ir.Language

object Types {

    private val types = listOf(
        "Int!->Int",
        "Any~>Int",
        "Int~>Ref",
        "Ref~>Int",
        "Int->Any",

        "Str->Ref",
        "Str!->Str",
        "Str!->Ref!",
        "Any~>Str",
        "Str->Any",

        "FlatArr->Any",
        "Any~>FlatArr",
        "FlatArr~>Int",
        "FlatArr!~>Int!",

        "RefArr->Any",
        "Any~>RefArr",
        "RefArr~>Ref",

        "Any!->Any",

        "Type!->Any!",

        "Label!->Label",
        "Label->Any",

        "Fun!->Any!",
        "Fun->Any",
    )

    private enum class ConvertingOp {
        VALUE_TO_VALUE,
        VALUE_TO_VALUE_WARN
    }

    private val convertingMap = mutableMapOf<Type, MutableMap<Type, ConvertingOp>>()

    private val typeSet = mutableSetOf<Type>()

    private var initialized = false

    fun getTypes(): Collection<Type> {
        init()
        return typeSet
    }

    fun getTypeNames(): Collection<String> =
        getTypes().map { it.getNameFix() }

    private fun init() {
        if (initialized)
            return

        types.forEach { tc ->
            val sp = tc.split(">")
            val op = sp[0].last()
            val from = sp[0].dropLast(1)
            val to = sp[1]

            val fromType = Type(from)
            val toType = Type(to)

            val opOp = when (op) {
                '-' -> ConvertingOp.VALUE_TO_VALUE
                '~' -> ConvertingOp.VALUE_TO_VALUE_WARN
                else -> throw Exception("Invalid converting op: $op")
            }

            convertingMap.getOrPut(fromType) { mutableMapOf() }[toType] = opOp
            typeSet += fromType
            typeSet += toType
        }
        initialized = true
    }

    private fun valConvFromToLoop(from: Type, to: Type): ConvertingOp? {
        if (from.nameEq(to.name))
            return ConvertingOp.VALUE_TO_VALUE
        val map = convertingMap[from] ?: return null
        val op = map[to]
        if (op != null)
            return op
        map.forEach { (k, v) ->
            val o = valConvFromToLoop(k, to)
            if (o != null)
                return v
        }
        return null
    }

    fun valueConvertFromTo(from: Type, to: Type, line: Int, column: Int, length: Int) {
        val op = valConvFromToLoop(from, to)
            ?: Language.exception(
                "Cannot convert from $from to $to!",
                line,
                column,
                length
            )
        if (op == ConvertingOp.VALUE_TO_VALUE_WARN)
            Language.warn(
                "Please use the \"cast!\" function to convert from $from to $to!",
                line,
                column,
                length
            )
    }

}