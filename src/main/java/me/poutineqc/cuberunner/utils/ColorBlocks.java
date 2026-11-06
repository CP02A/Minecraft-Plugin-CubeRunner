package me.poutineqc.cuberunner.utils;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * The 32 blocks an arena can rain down: the 16 wools and the 16 dyed terracottas.
 *
 * <p>Before the flattening these were {@code WOOL:0-15} and {@code STAINED_CLAY:0-15}. An arena stores its
 * selection as a 32-bit mask ({@code colorIndice}): bits 0-15 are the wools and bits 16-31 the terracottas,
 * both in legacy data-value order (white, orange, magenta, …, black). That order is the same as
 * {@link DyeColor}'s declaration order, so existing arena data keeps working.
 */
public final class ColorBlocks {

	public static final int COUNT = 32;

	private static final Material[] BY_BIT = new Material[COUNT];
	private static final Map<Material, Integer> BIT_BY_MATERIAL = new EnumMap<>(Material.class);

	static {
		DyeColor[] colors = DyeColor.values();
		for (int i = 0; i < 16; i++) {
			register(i, Material.valueOf(colors[i].name() + "_WOOL"));
			register(i + 16, Material.valueOf(colors[i].name() + "_TERRACOTTA"));
		}
	}

	private ColorBlocks() {
	}

	private static void register(int bit, Material material) {
		BY_BIT[bit] = material;
		BIT_BY_MATERIAL.put(material, bit);
	}

	/** The block for a bit of the arena's color mask (0-31). */
	public static Material get(int bit) {
		return BY_BIT[bit];
	}

	/** The bit of the arena's color mask for a block, or -1 if it isn't one of the 32 arena blocks. */
	public static int bitOf(Material material) {
		return BIT_BY_MATERIAL.getOrDefault(material, -1);
	}

	public static boolean isColorBlock(Material material) {
		return BIT_BY_MATERIAL.containsKey(material);
	}

	public static boolean isWool(Material material) {
		int bit = bitOf(material);
		return bit >= 0 && bit < 16;
	}

	/** {@code <COLOR>_<suffix>}, e.g. {@code dyed(DyeColor.PURPLE, "STAINED_GLASS_PANE")}. */
	public static Material dyed(DyeColor color, String suffix) {
		return Material.valueOf(color.name() + "_" + suffix);
	}
}
