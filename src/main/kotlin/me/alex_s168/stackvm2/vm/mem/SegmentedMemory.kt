package me.alex_s168.stackvm2.vm.mem

import me.alex_s168.ktlib.Lockable
import me.alex_s168.stackvm2.mem.MemoryLayout
import me.alex_s168.stackvm2.mem.MemoryRegion

open class SegmentedMemory(
    protected val regions: List<MemoryRegion>
): Memory {

    class PreparingSegmentMemory(
        regions: List<MemoryRegion>
    ): SegmentedMemory(regions), Lockable {

        override fun get(index: Int): Int {
            if (locked)
                throw IllegalStateException("Memory is locked!")

            for (region in regions) {
                if (index in region.start..region.end) {
                    return region.mem!![index - region.start]
                }
            }

            return 0
        }

        override fun set(index: Int, value: Int) {
            if (locked)
                throw IllegalStateException("Memory is locked!")

            for (region in regions) {
                if (index in region.start..region.end) {
                    region.mem!![index - region.start] = value
                    return
                }
            }
        }

        fun finalize(): SegmentedMemory {
            lock()
            return SegmentedMemory(regions)
        }

        private var locked = false

        override fun isLocked(): Boolean =
            locked

        override fun lock() {
            locked = true
        }

    }

    companion object {

        fun of(layout: MemoryLayout): PreparingSegmentMemory {
            val regions = ArrayList<MemoryRegion>()

            layout.romRegion?.let { regions.add(it) }
            regions.addAll(layout.ramRegions)

            regions.forEach {
                if (it.mem == null)
                    it.mem = IntArray(it.end - it.start + 1) { 0 }
            }

            return PreparingSegmentMemory(regions)
        }

    }

    fun set(from: Int, to: Int, value: Int) {
        (from..<to).forEach {
            set(it, value)
        }
    }

    fun set(from: Int, values: IntArray, skip: Int = 0) {
        for ((i, it) in values.withIndex()) {
            if (i < skip)
                continue

            set(from + i - skip, it)
        }
    }

    override fun get(index: Int): Int {
        for (region in regions) {
            if (index in region.start..region.end) {
                if (!region.isReadable)
                    throw IllegalStateException("Memory region is not readable!")

                return region.mem!![index - region.start]
            }
        }

        return 0
    }

    override fun set(index: Int, value: Int) {
        for (region in regions) {
            if (index in region.start..region.end) {
                if (!region.isWritable)
                    throw IllegalStateException("Memory region is not writable (a$index)!")

                region.mem!![index - region.start] = value
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