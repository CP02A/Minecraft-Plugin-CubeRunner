package me.poutineqc.cuberunner.commands.inventories;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.poutineqc.cuberunner.CRPlayer;
import me.poutineqc.cuberunner.CRPlayer.PlayerStatsException;
import me.poutineqc.cuberunner.Language.Messages;
import me.poutineqc.cuberunner.utils.Utils;

public abstract class CRInventory {
	protected Inventory inventory;
	protected CRPlayer crPlayer;
	protected int amountOfRows;
	protected String title;

	public CRInventory(CRPlayer crPlayer) {
		this.crPlayer = crPlayer;
	}

	public abstract void fillInventory() throws PlayerStatsException;

	public abstract void update(ItemStack itemStack, InventoryAction action);

	public static boolean areEqualOnColorStrip(String itemA, String itemB) {
		return Utils.strip(Utils.color(itemA))
				.equalsIgnoreCase(Utils.strip(Utils.color(itemB)));
	}

	protected void createInventory() {
		inventory = Bukkit.createInventory(crPlayer.getPlayer(), amountOfRows * 9, Utils.text(getFullTitle()));
	}

	protected String getFullTitle() {
		String title = Utils.color(crPlayer.getLanguage().get(Messages.PREFIX_SHORT) + " " + this.title);
		return title;
	}

	protected void openInventory() {
		crPlayer.getPlayer().openInventory(inventory);
		crPlayer.setCurrentInventory(this);
	}

	protected void closeInventory() {
		crPlayer.getPlayer().closeInventory();
		crPlayer.setCurrentInventory(null);
	}
}
