package me.poutineqc.cuberunner.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;

/**
 * Text helpers.
 *
 * <p>CubeRunner's messages (language files, config, sign lines) are legacy strings using {@code &} or
 * {@code §} color codes. They are kept as strings internally and turned into Adventure components only
 * where they are handed to the server.
 */
public final class Utils {

	private static final Pattern AMPERSAND_CODE = Pattern.compile("&(?=[0-9a-fk-orA-FK-OR])");
	private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

	private Utils() {
	}

	/** Translates {@code &} color codes to {@code §} codes. */
	public static String color(String string) {
		return string == null ? null : AMPERSAND_CODE.matcher(string).replaceAll("§");
	}

	/** A legacy string ({@code &} or {@code §} codes) as a component. */
	public static Component text(String legacy) {
		return legacy == null ? Component.empty() : LEGACY.deserialize(color(legacy));
	}

	public static List<Component> text(List<String> legacy) {
		List<Component> components = new ArrayList<>(legacy.size());
		for (String line : legacy)
			components.add(text(line));
		return components;
	}

	/** A component back to a {@code §}-coded string. */
	public static String legacy(Component component) {
		return component == null ? null : LEGACY.serialize(component);
	}

	public static List<String> legacy(List<Component> components) {
		List<String> lines = new ArrayList<>(components.size());
		for (Component component : components)
			lines.add(legacy(component));
		return lines;
	}

	/** The text without any color codes. */
	public static String strip(String string) {
		return PlainTextComponentSerializer.plainText().serialize(text(string));
	}

	public static String strip(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	public static boolean isEqualOnColorStrip(String l2, String l1) {
		return strip(l1).equalsIgnoreCase(strip(l2));
	}

	public static void sendMessage(Player player, String legacy) {
		player.sendMessage(text(legacy));
	}

	/** The countdown title: the number in bold gold, the subtitle in italic gray. Times are in ticks. */
	public static void sendCountdownTitle(Player player, int seconds, String subtitle, int fadeIn, int stay,
			int fadeOut) {
		Component title = Component.text(seconds, NamedTextColor.GOLD, TextDecoration.BOLD);
		Component sub = Component.text(subtitle, NamedTextColor.GRAY, TextDecoration.ITALIC);
		player.showTitle(Title.title(title, sub,
				Title.Times.times(ticks(fadeIn), ticks(stay), ticks(fadeOut))));
	}

	private static Duration ticks(int ticks) {
		return Duration.ofMillis(ticks * 50L);
	}
}
