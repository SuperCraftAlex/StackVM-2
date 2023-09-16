package me.alex_s168.stackvm2.mem

class MemoryLayout(
    val romRegion : MemoryRegion?,
    val ramRegions: List<MemoryRegion>
) {

    fun getRamRegionsSorted(): List<MemoryRegion> =
        ramRegions.sortedBy { it.start }

    fun getRamRegionsSortedExceptZero(): List<MemoryRegion> =
        getRamRegionsSorted().filter { it.start != 0 }

    class Builder {

        private val ramRegions = ArrayList<MemoryRegion>()
        private var romRegion: MemoryRegion? = null

        fun ram(start: Int, end: Int): Builder {
            ramRegions.add(MemoryRegion(start, end, true, true))
            return this
        }

        fun ram(range: IntRange): Builder =
            ram(range.first, range.last)

        fun ram(range: Pair<Int, Int>): Builder =
            ram(range.first, range.second)

        fun rom(start: Int, end: Int): Builder {
            if (romRegion != null)
                throw IllegalStateException("ROM region is already set!")

            romRegion = MemoryRegion(start, end, isWritable = false, isReadable = true)
            return this
        }

        fun rom(range: IntRange): Builder =
            rom(range.first, range.last)

        fun rom(range: Pair<Int, Int>): Builder =
            rom(range.first, range.second)

        fun build(): MemoryLayout {
            return MemoryLayout(romRegion, ramRegions.toList())
        }

    }

    companion object {

        fun new() = Builder()

    }

}