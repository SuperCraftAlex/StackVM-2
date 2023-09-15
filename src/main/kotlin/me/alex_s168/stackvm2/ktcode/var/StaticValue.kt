package me.alex_s168.stackvm2.ktcode.`var`

import me.alex_s168.stackvm2.ktcode.KTCode

class StaticValue(
    val ktCode: KTCode,
    val v: Int
): Stackable {

    override fun getKTCode(): KTCode =
        ktCode

    override fun getElemSize(): Int =
        1

    override fun putOntoStack(offset: Int) {
        if (offset != 0)
            throw UnsupportedOperationException("Int is not an array!")

        ktCode.loadImm(v)
    }

    override fun putOntoStack(offset: Stackable) =
        ktCode.loadImm(v)

    override fun putOntoStack() =
        ktCode.loadImm(v)

}