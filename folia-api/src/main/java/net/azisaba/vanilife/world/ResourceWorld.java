package net.azisaba.vanilife.world;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import net.azisaba.vanilife.Season;
import net.azisaba.vanilife.Vanilife;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public interface ResourceWorld extends World {
    float CLOUD_HEIGHT = 192.33F;

    Key WORLD_KEY = currentWorldKey();

    static Key currentWorldKey() {
        final String override = System.getProperty("vanilife.resourceWorldKey");
        if (override != null && !override.isBlank()) {
            return override.contains(":") ? Key.key(override) : Key.key(Vanilife.NAMESPACE, override);
        }
        final LocalDate now = LocalDate.now();
        final Season season = Season.now();
        final int year = (season == Season.WINTER && (now.getMonth() == Month.JANUARY || now.getMonth() == Month.FEBRUARY))
            ? now.getYear() - 1
            : now.getYear();
        return Key.key(Vanilife.NAMESPACE, year + "/" + season.name().toLowerCase(Locale.ROOT));
    }
}
