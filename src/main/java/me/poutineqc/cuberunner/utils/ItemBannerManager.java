package me.poutineqc.cuberunner.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class ItemBannerManager extends ItemStackManager {
	private DyeColor baseColor;
	private List<Pattern> patterns = new ArrayList<Pattern>();

	public enum CustomPattern {
		ARROW_RIGHT, ARROW_LEFT, ARROW_ACTUAL, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SYMBOL_PLUS, SYMBOL_MINUS, DOT;

		public static CustomPattern getPattern(int number) {
			switch (number) {
			case 0:
				return CustomPattern.ZERO;
			case 1:
				return CustomPattern.ONE;
			case 2:
				return CustomPattern.TWO;
			case 3:
				return CustomPattern.THREE;
			case 4:
				return CustomPattern.FOUR;
			case 5:
				return CustomPattern.FIVE;
			case 6:
				return CustomPattern.SIX;
			case 7:
				return CustomPattern.SEVEN;
			case 8:
				return CustomPattern.EIGHT;
			case 9:
				return CustomPattern.NINE;
			default:
				return null;
			}
		}
	}

	public ItemBannerManager() {
		super(Material.WHITE_BANNER);
		baseColor = DyeColor.WHITE;
	}

	public ItemBannerManager(ItemStack itemStack) {
		super(itemStack);

		// Since 1.13 the base color is part of the material (WHITE_BANNER, RED_BANNER, ...).
		baseColor = baseColorOf(itemStack.getType());
		BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
		patterns = new ArrayList<Pattern>(meta.getPatterns());
	}

	public ItemBannerManager(CustomPattern pattern) {
		this(pattern, DyeColor.BLACK);
	}

	public ItemBannerManager(CustomPattern pattern, DyeColor dyeColor) {
		super(Material.WHITE_BANNER);
		setPattern(pattern, dyeColor);
	}

	public static boolean isBanner(Material material) {
		return baseColorOf(material) != null;
	}

	private static DyeColor baseColorOf(Material material) {
		String name = material.name();
		if (!name.endsWith("_BANNER") || name.endsWith("_WALL_BANNER"))
			return null;

		try {
			return DyeColor.valueOf(name.substring(0, name.length() - "_BANNER".length()));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public ItemStack getItem() {
		material = ColorBlocks.dyed(baseColor, "BANNER");
		ItemStack itemStack = super.getItem();
		BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
		meta.setPatterns(patterns);

		itemStack.setItemMeta(meta);
		return itemStack;
	}

	@Override
	public boolean isSame(ItemStack itemStack) {
		material = ColorBlocks.dyed(baseColor, "BANNER");
		if (!super.isSame(itemStack))
			return false;

		if (baseColorOf(itemStack.getType()) != baseColor)
			return false;

		BannerMeta meta = (BannerMeta) itemStack.getItemMeta();

		if (meta.getPatterns().size() != patterns.size())
			return false;

		for (int i = 0; i < patterns.size(); i++) {
			if (patterns.get(i).getColor() != meta.getPatterns().get(i).getColor()
					|| !patterns.get(i).getPattern().equals(meta.getPatterns().get(i).getPattern()))
				return false;
		}

		return true;
	}

	public void add(Pattern pattern) {
		patterns.add(pattern);
	}

	public void setPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
	}

	public void clearPatterns() {
		patterns.clear();
	}

	public List<Pattern> getPatterns() {
		return patterns;
	}

	private void setPattern(CustomPattern pattern, DyeColor dyeColor) {
		patterns.clear();
		switch (pattern) {
		case ARROW_LEFT:
			baseColor = dyeColor;
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS));
			patterns.add(new Pattern(dyeColor, PatternType.HALF_VERTICAL_RIGHT));
			break;
		case ARROW_RIGHT:
			baseColor = dyeColor;
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS));
			patterns.add(new Pattern(dyeColor, PatternType.HALF_VERTICAL));
			break;
		case ARROW_ACTUAL:
			baseColor = dyeColor;
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.CIRCLE));
			break;
		case SYMBOL_MINUS:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case SYMBOL_PLUS:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			break;
		case DOT:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.CREEPER));
			break;
		case ZERO:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case ONE:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case TWO:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.TRIANGLE_TOP));
			patterns.add(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.SQUARE_TOP_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case THREE:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case FOUR:
			baseColor = dyeColor;
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case FIVE:
			baseColor = dyeColor;
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_VERTICAL_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.DIAGONAL_UP_RIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case SIX:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case SEVEN:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.SQUARE_BOTTOM_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case EIGHT:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case NINE:
			baseColor = DyeColor.WHITE;
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_BOTTOM));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_TOP));
			patterns.add(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
			patterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		}
	}

	public DyeColor getBaseColor() {
		return baseColor;
	}
}