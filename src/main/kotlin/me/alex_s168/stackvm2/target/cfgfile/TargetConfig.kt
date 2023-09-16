package me.alex_s168.stackvm2.target.cfgfile

import me.alex_s168.stackvm2.common.MemoryLayout

data class TargetConfig(
    val name: String,
    val layout: (prgEntryPoint: Int, progSize: Int, ramAmount: Int) -> MemoryLayout,
    val interrupts: Map<String, Int>,
    val labels: Collection<Pair<String, Int>>
)