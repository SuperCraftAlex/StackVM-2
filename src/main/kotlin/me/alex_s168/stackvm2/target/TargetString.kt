package me.alex_s168.stackvm2.target

typealias TargetString = String

fun String.matchesDotSeperatedString(other: String): Boolean {
    val aVer = this.split(".")
    val bVer = other.split(".")

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

fun TargetString.matchesTargetString(other: TargetString): Boolean {
    val a = this.split(":")
    val b = other.split(":")

    if (a.size != b.size)
        return false

    for ((i, part) in a.withIndex()) {
        if (!part.matchesDotSeperatedString(b[i]))
            return false
    }

    return true
}