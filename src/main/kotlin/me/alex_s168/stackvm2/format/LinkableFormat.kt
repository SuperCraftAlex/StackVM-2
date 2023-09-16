package me.alex_s168.stackvm2.format

import me.alex_s168.stackvm2.format.exception.GlobalLabelAlreadyDefinedException
import java.nio.ByteBuffer

class LinkableFormat(
    val labels: HashMap<String, Int>,
    val unresolved: MutableList<Pair<Int, String>>,

    var code: IntArray,

    val offset: Int = 0
) {

    fun linkWith(labels: HashMap<String, Int>) =
        linkWith(LinkableFormat(labels, mutableListOf(), IntArray(0)))

    fun linkWith(other: LinkableFormat) {
        val off = code.size
        code += other.code

        other.labels.toList().forEach { (k, v) ->
            val addr = offset + v + off

            if (k in labels)
                throw GlobalLabelAlreadyDefinedException("Label $k already defined!")

            labels[k] = addr
        }

        for ((where, name) in unresolved.toList()) {
            val addr = offset + (labels[name] ?: continue)
            code[where] = addr
            unresolved.remove(where to name)
        }

        for ((where, name) in other.unresolved) {
            val addr = labels[name]
            val whereReal = where + off
            if (addr == null) {
                unresolved.add(whereReal to name)
                continue
            }
            code[whereReal] = offset + addr
        }
    }

    fun size(): Int {
        var size = 0

        size += 4
        for ((k, _) in labels) {
            size += 8
            size += k.length
        }

        size += 4
        for ((_, name) in unresolved) {
            size += 8
            size += name.length
        }

        size += code.size * 4

        return size
    }

    fun save(buf: ByteBuffer) {
        buf.putInt(labels.size)
        for ((key, value) in labels) {
            buf.putInt(key.length)
            buf.put(key.toByteArray())
            buf.putInt(value)
        }

        buf.putInt(unresolved.size)
        for ((where, name) in unresolved) {
            buf.putInt(where)
            buf.putInt(name.length)
            buf.put(name.toByteArray())
        }

        buf.asIntBuffer().put(code)
    }

    companion object {

        fun empty(off: Int = 0): LinkableFormat =
            LinkableFormat(HashMap(), mutableListOf(), IntArray(0), off)

        fun from(buf: ByteBuffer): LinkableFormat {
            val labels = HashMap<String, Int>()
            val unresolved = mutableListOf<Pair<Int, String>>()

            repeat(buf.getInt()) {
                val key = ByteArray(buf.getInt())
                buf.get(key)

                labels[String(key)] = buf.getInt()
            }

            repeat(buf.getInt()) {
                val where = buf.getInt()

                val name = ByteArray(buf.getInt())
                buf.get(name)

                unresolved.add(where to String(name))
            }

            val code = IntArray(buf.remaining() / 4)
            buf.asIntBuffer().get(code)

            return LinkableFormat(labels, unresolved, code)
        }

    }

}