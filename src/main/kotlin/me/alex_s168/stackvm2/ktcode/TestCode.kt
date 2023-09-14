package me.alex_s168.stackvm2.ktcode

import me.alex_s168.stackvm2.std.StandardInterrupts

class TestCode(
    offset: Int,
    elemSize: Int
): KTCode(offset, elemSize) {

    override fun prog() {
        val addFunc = func(2) {
            add()
        }

        var a = intVar(1)
        a ++

        addFunc(9, -a)

        interrupt(StandardInterrupts.EXIT)
    }

}