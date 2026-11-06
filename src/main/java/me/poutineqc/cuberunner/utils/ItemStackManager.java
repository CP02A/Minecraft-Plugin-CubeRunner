package me.poutineqc.cuberunner.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A mutable item description that the GUIs build up and turn into an {@link ItemStack}.
 *
 * <p>Before the flattening, colored items were a material plus a data value ({@code WOOL:5}); now every
 * variant is its own {@link Material}, so there is no data value any more.
 */
public class ItemStackManager {

	protected Material material;
	protected int amount = 1;

	protected String name;
	protected List<String> lore = new ArrayList<String>();
	protected Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

	public ItemStackManager(Material material) {
		this.material = material;
	}

	public ItemStackManager(ItemStack itemStack) {
		this.material = itemStack.getType();
		this.amount = itemStack.getAmount();

		ItemMeta meta = itemStack.getItemMeta();
		this.name = meta.hasDisplayName() ? Utils.legacy(meta.displayName()) : null;
		this.enchantments = meta.hasEnchants() ? new HashMap<>(meta.getEnchants()) : new HashMap<Enchantment, Integer>();
		this.lore = meta.hasLore() ? Utils.legacy(meta.lore()) : new ArrayList<String>();
	}

	public ItemStack getItem() {
		ItemStack itemStack = new ItemStack(material, amount);

		ItemMeta meta = itemStack.getItemMeta();
		if (name != null)
			meta.displayName(Utils.text(name));
		if (lore != null && !lore.isEmpty())
			meta.lore(Utils.text(lore));
		for (Entry<Enchantment, Integer> enchantment : enchantments.entrySet())
			meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);

		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public boolean isSame(ItemStack itemStack) {
		if (material != itemStack.getType())
			return false;

		ItemMeta meta = itemStack.getItemMeta();
		if (meta.hasDisplayName()) {
			if (name == null)
				return false;

			if (!Utils.isEqualOnColorStrip(Utils.legacy(meta.displayName()), name))
				return false;

		} else if (name != null)
			return false;

		if (meta.hasEnchants()) {
			for (Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet())
				if (!hasEnchantement(enchantment.getKey(), enchantment.getValue()))
					return false;
		} else if (enchantments.size() > 0)
			return false;

		return true;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setDisplayName(String displayName) {
		this.name = Utils.color(displayName);
	}

	public void addToLore(String loreLine) {
		lore.add(Utils.color(loreLine));
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public void clearLore() {
		lore = new ArrayList<String>();
	}

	public void addEnchantement(Enchantment enchantment, int level) {
		enchantments.put(enchantment, level);
	}

	public void setEnchantements(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
	}

	public void clearEnchantements() {
		enchantments.clear();
	}

	public int getMaxStackSize() {
		return material.getMaxStackSize();
	}

	public Material getMaterial() {
		return material;
	}

	public boolean hasEnchantement(Enchantment enchantement) {
		return enchantments.containsKey(enchantement);
	}

	public boolean hasEnchantement(Enchantment enchantement, int value) {
		Integer level = enchantments.get(enchantement);
		return level != null && level == value;
	}

	public String getDisplayName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}
}
