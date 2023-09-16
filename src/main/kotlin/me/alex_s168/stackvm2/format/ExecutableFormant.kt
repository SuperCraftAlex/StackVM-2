package me.alex_s168.stackvm2.format

import java.nio.ByteBuffer

class ExecutableFormant(
    val isaVersion: String,
    val targetString: String,
    val entryPoint: Int,

    val code: IntArray
) {

    fun save(buf: ByteBuffer) {
        buf.putInt(isaVersion.length)
        isaVersion.forEach { buf.put(it.code.toByte()) }

        buf.putInt(targetString.length)
        targetString.forEach { buf.put(it.code.toByte()) }

        buf.putInt(entryPoint)

        buf.asIntBuffer().put(code)
    }

    fun size(): Int =
        4 + isaVersion.length * 2 + 4 + targetString.length * 2 + 4 + code.size * 4

    companion object {
        fun from(buf: ByteBuffer): ExecutableFormant {
            val isaVersion = StringBuilder()
            repeat(buf.getInt()) {
                isaVersion.append(buf.get().toInt().toChar())
            }

            val targetString = StringBuilder()
            repeat(buf.getInt()) {
                targetString.append(buf.get().toInt().toChar())
            }

            val entryPoint = buf.getInt()

            val code = IntArray(buf.remaining() / 4)
            buf.asIntBuffer().get(code)

            return ExecutableFormant(
                isaVersion.toString(),
                targetString.toString(),
                entryPoint,
                code
            )
        }
    }

}