package net.azisaba.vanilife;

import java.util.Objects;
import net.azisaba.vanilife.world.IslandsWorld;
import net.azisaba.vanilife.world.ResourceWorld;
import org.bukkit.Bukkit;

public final class Vanilife {
    public static final String NAMESPACE = "vanilife";

    public static IslandsWorld getIslandsWorld() {
        return (IslandsWorld) Objects.requireNonNull(Bukkit.getWorld(IslandsWorld.WORLD_KEY));
    }

    public static ResourceWorld getResourceWorld() {
        return (ResourceWorld) Objects.requireNonNull(Bukkit.getWorld(ResourceWorld.WORLD_KEY));
    }

    private Vanilife() {
    }
}
