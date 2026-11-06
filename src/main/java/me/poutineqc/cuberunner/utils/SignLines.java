package me.poutineqc.cuberunner.utils;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;

/**
 * Sign line access with legacy strings.
 *
 * <p>Signs have two sides since 1.20. CubeRunner only uses the front side, as the 1.8-1.12 plugin did with
 * the only side signs had back then.
 */
public final class SignLines {

	private SignLines() {
	}

	public static String get(Sign sign, int line) {
		return orEmpty(sign.getSide(Side.FRONT).line(line));
	}

	public static void set(Sign sign, int line, String legacy) {
		sign.getSide(Side.FRONT).line(line, Utils.text(legacy));
	}

	public static String get(SignChangeEvent event, int line) {
		return orEmpty(event.line(line));
	}

	public static void set(SignChangeEvent event, int line, String legacy) {
		event.line(line, Utils.text(legacy));
	}

	private static String orEmpty(Component component) {
		return component == null ? "" : Utils.legacy(component);
	}
}
