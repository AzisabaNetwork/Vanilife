package net.azisaba.vanilife.npc.wrapper

import net.kyori.adventure.text.Component
import org.bukkit.entity.Chicken

interface NpcNameAccessor {
    fun customName(): Component?

    fun customName(customName: Component?)
}

internal class NpcNameAccessorImpl(private val delegate: Chicken) : NpcNameAccessor {
    override fun customName(): Component? = delegate.customName()

    override fun customName(customName: Component?) {
        delegate.customName(customName)
    }
}
