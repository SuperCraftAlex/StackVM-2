package me.alex_s168.stackvm2.ir.`var`

class Type(
    val name: String,
    val arrSize: Int = 0,
    val retType: Type? = null,
    val generics: List<Type> = listOf(),
    val ct: Boolean = name.lastOrNull()?.equals('!') ?: false
) {

    override fun toString(): String =
        "Type(name=${name.removeSuffix("!") + if (ct) "!" else ""}, genericTypes=$generics)"

    companion object {
        val ANY_HASH = Type("Any").hashCode()
    }

    fun nameEq(other: String): Boolean {
        if (other.hashCode() == ANY_HASH)
            return true
        if (hashCode() == ANY_HASH)
            return true
        return name.removeSuffix("!") == other.removeSuffix("!")
    }

    override fun equals(other: Any?): Boolean =
        hashCode() == other.hashCode()

    override fun hashCode(): Int {
        var result = name.removeSuffix("!").hashCode()
        result = 31 * result + arrSize
        result = 31 * result + (retType?.hashCode() ?: 0)
        result = 31 * result + generics.hashCode()
        return result
    }

}