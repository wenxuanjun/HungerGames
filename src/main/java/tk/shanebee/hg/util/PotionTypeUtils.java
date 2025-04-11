package tk.shanebee.hg.util;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Util for getting {@link PotionData}
 */
public enum PotionTypeUtils {

    EMPTY("UNCRAFTABLE"),
    WATER("WATER"),
    MUNDANE("MUNDANE"),
    THICK("THICK"),
    AWKWARD("AWKWARD"),
    NIGHT_VISION("NIGHT_VISION"),
    LONG_NIGHT_VISION("LONG_NIGHT_VISION"),
    INVISIBILITY("INVISIBILITY"),
    LONG_INVISIBILITY("LONG_INVISIBILITY"),
    LEAPING("LEAPING"),
    LONG_LEAPING("LONG_LEAPING"),
    STRONG_LEAPING("STRONG_LEAPING"),
    FIRE_RESISTANCE("FIRE_RESISTANCE"),
    LONG_FIRE_RESISTANCE("LONG_FIRE_RESISTANCE"),
    SWIFTNESS("SWIFTNESS"),
    LONG_SWIFTNESS("LONG_SWIFTNESS"),
    STRONG_SWIFTNESS("STRONG_SWIFTNESS"),
    SLOWNESS("SLOWNESS"),
    LONG_SLOWNESS("LONG_SLOWNESS"),
    STRONG_SLOWNESS("STRONG_SLOWNESS"),
    WATER_BREATHING("WATER_BREATHING"),
    LONG_WATER_BREATHING("LONG_WATER_BREATHING"),
    HEALING("HEALING"),
    STRONG_HEALING("STRONG_HEALING"),
    HARMING("HARMING"),
    STRONG_HARMING("STRONG_HARMING"),
    POISON("POISON"),
    LONG_POISON("LONG_POISON"),
    STRONG_POISON("STRONG_POISON"),
    REGENERATION("REGENERATION"),
    LONG_REGENERATION("LONG_REGENERATION"),
    STRONG_REGENERATION("STRONG_REGENERATION"),
    STRENGTH("STRENGTH"),
    LONG_STRENGTH("LONG_STRENGTH"),
    STRONG_STRENGTH("STRONG_STRENGTH"),
    WEAKNESS("WEAKNESS"),
    LONG_WEAKNESS("LONG_WEAKNESS"),
    LUCK("LUCK"),
    TURTLE_MASTER("TURTLE_MASTER"),
    LONG_TURTLE_MASTER("LONG_TURTLE_MASTER"),
    STRONG_TURTLE_MASTER("STRONG_TURTLE_MASTER"),
    SLOW_FALLING("SLOW_FALLING"),
    LONG_SLOW_FALLING("LONG_SLOW_FALLING"),
    WIND_CHARGED("WIND_CHARGED"),
    WEAVING("WEAVING"),
    OOZING("OOZING"),
    INFESTED("INFESTED");

    private final String bukkit;
    private static final Map<String, String> BY_NAME = new HashMap<>();

    PotionTypeUtils(String bukkit) {
        this.bukkit = bukkit;
    }

    static {
        for (PotionTypeUtils p : values()) {
            BY_NAME.put(p.name(), p.bukkit);
        }
        for (PotionType value : PotionType.values()) {
            if (!BY_NAME.containsValue(value.toString())) {
                Util.warning("Missing PotionType for '&7" + value + "&e' please let dev know.");
            }
        }
    }

    /**
     * Get a PotionType based on a Minecraft namespace with Bukkit key fallback
     *
     * @param key Key for PotionType (can be Minecraft namespace or Bukkit key)
     * @return PotionType (null if MC or Bukkit key does not exist)
     */
    @Nullable
    public static PotionType get(String key) {
        String upper = key.toUpperCase();
        if (BY_NAME.containsKey(upper)) {
            return getByKey(upper);
        } else if (BY_NAME.containsValue(upper)) {
            return getByBukkit(upper);
        }
        return null;
    }

    /**
     * Get a PotionType based on a Minecraft namespace
     *
     * @param key Minecraft namespace
     * @return PotionType
     */
    @Nullable
    public static PotionType getByKey(String key) {
        return getByBukkit(valueOf(key).bukkit);
    }

    /**
     * Get a PotionType based on a Bukkit key
     *
     * @param bukkit Key for PotionType
     * @return PotionType
     */
    public static PotionType getByBukkit(String bukkit) {
        return PotionType.valueOf(bukkit.toUpperCase());
    }

    /**
     * Get PotionData from a String
     * <p><b>Formats:</b>
     * <br>POTION-TYPE (optional start with 'LONG_' or 'STRONG_')
     * <br>POTION-TYPE:boolean(strong):boolean(extended)</p>
     *
     * @param data data string of potion type
     * @return New PotionData if checks passed
     */
    @Nullable
    public static PotionData getPotionData(String data) {
        String[] potionData = data.split(":");
        if (potionData.length == 1) {
            String pData = potionData[0].toUpperCase(Locale.ROOT);
            boolean strong = potionData[0].startsWith("STRONG_");
            boolean extended = potionData[0].startsWith("LONG_");
            pData = pData.replace("STRONG_", "").replace("LONG_", "");
            PotionType potionType = get(pData);
            if (potionType == null) {
                Util.warning("Potion base type not found: &c" + potionData[0].toUpperCase(Locale.ROOT) + " &ein: &b" + data);
                Util.warning("&r  - Check your configs");
                Util.warning("&r  - Proper examples:");
                Util.warning("      &bpotion-base:turtle_master");
                Util.warning("      &bpotion-base:LONG_TURTLE_MASTER");
                Util.warning("      &bpotion-base:strong_turtle_master");
                return null;
            } else if (extended && !potionType.isExtendable()) {
                Util.warning("Potion can not be extended: &b" + data);
                return null;
            } else if (strong && !potionType.isUpgradeable()) {
                Util.warning("Potion can not be upgraded: &b" + data);
                return null;
            }
            return new PotionData(potionType, extended, strong);
        } else if (potionData.length == 3) {
            PotionType potionType = get(potionData[0]);
            if (potionType == null) {
                potionTypeWarning("Potion base type not found: &c" + potionData[0].toUpperCase(Locale.ROOT) + " &ein: &b" + data);
                return null;
            } else if (!Util.isBool(potionData[1])) {
                potionTypeWarning("Not a valid boolean: &c" + potionData[1].toUpperCase(Locale.ROOT) + " &ein: &b" + data);
                return null;
            } else if (!Util.isBool(potionData[2])) {
                potionTypeWarning("Not a valid boolean: &c" + potionData[2].toUpperCase(Locale.ROOT) + " &ein: &b" + data);
                return null;
            }
            boolean upgraded = Boolean.parseBoolean(potionData[1]);
            boolean extended = Boolean.parseBoolean(potionData[2]);
            if (upgraded && !potionType.isUpgradeable()) {
                Util.warning("Potion can not be upgraded: &b" + data);
                return null;
            } else if (extended && !potionType.isExtendable()) {
                Util.warning("Potion can not be extended: &b" + data);
                return null;
            } else if (upgraded && extended) {
                Util.warning("Potion can not be both upgraded and extended in: &b" + data);
                return null;
            }

            return new PotionData(potionType, extended, upgraded);
        } else {
            potionTypeWarning("Improper setup of potion-data: &c");
            return null;
        }
    }

    private static void potionTypeWarning(@Nullable String warning) {
        if (warning != null) Util.warning(warning);
        Util.warning("&r  - Check your configs");
        Util.warning("&r  - Proper example:");
        Util.warning("      &bpotion-base:POTION_TYPE:UPGRADED:EXTENDED");
        Util.warning("      &bpotion-base:turtle_master:true:false");
    }

}
