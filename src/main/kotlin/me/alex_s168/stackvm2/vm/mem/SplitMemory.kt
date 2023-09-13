package me.alex_s168.stackvm2.vm.mem

class SplitMemory(
    private val regions: List<MemoryRegion>
): Memory {

    data class MemoryRegion(
        val start: Int,
        val end: Int,
        val isWritable: Boolean,
        val isReadable: Boolean,
        @Suppress("ArrayInDataClass") val mem: IntArray
    )

    class Builder {

        private val regions = ArrayList<MemoryRegion>()

        fun addRegion(start: Int, end: Int, isWritable: Boolean, isReadable: Boolean): Builder {
            regions.add(MemoryRegion(start, end, isWritable, isReadable, IntArray(end - start) { 0 }))
            return this
        }

        fun addRegion(start: Int, end: Int, isWritable: Boolean): Builder =
            addRegion(start, end, isWritable, true)

        fun addRegion(start: Int, end: Int): Builder =
            addRegion(start, end, true)

        fun addRegion(start: Int, end: Int, mem: IntArray): Builder {
            regions.add(MemoryRegion(start, end, isWritable = true, isReadable = true, mem))
            return this
        }

        fun build() = SplitMemory(regions)

    }

    companion object {

        fun new() = Builder()

    }

    override fun get(index: Int): Int {
        for (region in regions) {
            if (index in region.start..region.end) {
                return region.mem[index - region.start]
            }
        }

        return 0
    }

    override fun set(index: Int, value: Int) {
        for (region in regions) {
            if (index in region.start..region.end) {
                region.mem[index - region.start] = value
                return
            }
        }
    }

    override fun isWritable(index: Int): Boolean {
        for (region in regions) {
            if (index in region.start..region.end) {
                return region.isWritable
            }
        }

        return false
    }

    override fun isReadable(index: Int): Boolean {
        for (region in regions) {
            if (index in region.start..region.end) {
                return region.isReadable
            }
        }

        return false
    }

}