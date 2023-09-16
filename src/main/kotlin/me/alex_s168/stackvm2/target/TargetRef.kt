package me.alex_s168.stackvm2.target

data class TargetRef(
    val name: String,
    val isaVersion: String,
    val other: Collection<String>
) {

    fun matches(b: TargetRef): Boolean {
        if (name != b.name)
            return false

        if (other != b.other)
            return false

        val aVer = isaVersion.split(".")
        val bVer = b.isaVersion.split(".")

        for ((i, verPart) in aVer.withIndex()) {
            if (i >= bVer.size)
                return true

            val bVerPart = bVer[i]

            if (verPart == "*")
                continue

            if (bVerPart == "*")
                continue

            if (verPart != bVerPart)
                return false
        }

        return true
    }

    companion object {
        fun from(str: String): TargetRef {
            val arr = str.split(":")
            if (arr.size < 2)
                throw IllegalArgumentException("Invalid target reference: $str!")
            return TargetRef(arr[0], arr[1], arr.subList(2, arr.size))
        }
    }

}