package me.alex_s168.stackvm2.inst

enum class InstructionProperties {

    ONLY_PUSH,
    ONLY_POP,

    JUMPING,
    CALLING,
    RETURNING,

    CONDITIONAL,

    OP_2_1,
    OP_1_1,

    SP_MODIFYING

}