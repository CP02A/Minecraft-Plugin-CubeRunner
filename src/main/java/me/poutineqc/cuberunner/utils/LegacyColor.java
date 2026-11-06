package me.poutineqc.cuberunner.utils;

/**
 * Legacy {@code §} formatting codes, for building the legacy strings CubeRunner passes around.
 *
 * <p>Replaces the deprecated {@code org.bukkit.ChatColor}; {@link Utils#text(String)} turns the result into a
 * component.
 */
public enum LegacyColor {
	BLACK('0'), DARK_BLUE('1'), DARK_GREEN('2'), DARK_AQUA('3'), DARK_RED('4'), DARK_PURPLE('5'), GOLD('6'),
	GRAY('7'), DARK_GRAY('8'), BLUE('9'), GREEN('a'), AQUA('b'), RED('c'), LIGHT_PURPLE('d'), YELLOW('e'),
	WHITE('f'), MAGIC('k'), BOLD('l'), STRIKETHROUGH('m'), UNDERLINE('n'), ITALIC('o'), RESET('r');

	private final String code;

	LegacyColor(char code) {
		this.code = "§" + code;
	}

	@Override
	public String toString() {
		return code;
	}
}
