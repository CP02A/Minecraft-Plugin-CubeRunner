package me.poutineqc.cuberunner.commands;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.poutineqc.cuberunner.utils.ItemBannerManager;
import me.poutineqc.cuberunner.utils.ItemHeadManager;
import me.poutineqc.cuberunner.utils.ItemStackManager;

public class InventoryItem {
	private int position;
	private ItemStackManager item;
	
	public InventoryItem(ItemStackManager item, int position) {
		this.item = item;
		this.position = position;
	}
	
	public InventoryItem(ItemStackManager item) {
		this.item = item;
	}
	
	public InventoryItem(ItemStack itemStack) {
		if (itemStack.getType() == Material.PLAYER_HEAD)
			this.item = new ItemHeadManager(itemStack);
		else if (ItemBannerManager.isBanner(itemStack.getType()))
			this.item = new ItemBannerManager(itemStack);
		else
			this.item = new ItemStackManager(itemStack);
	}

	public InventoryItem(Material material) {
		if (material == Material.PLAYER_HEAD)
			this.item = new ItemHeadManager();
		else if (ItemBannerManager.isBanner(material))
			this.item = new ItemBannerManager();
		else
			this.item = new ItemStackManager(material);
	}

	public InventoryItem(Material material, int position) {
		this(material);
		this.position = position;
	}

	public ItemStackManager getItem() {
		return item;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void addToInventory(Inventory inventory) {
		inventory.setItem(position, item.getItem());
	}
}