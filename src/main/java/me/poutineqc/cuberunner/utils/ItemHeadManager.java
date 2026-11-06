package me.poutineqc.cuberunner.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;

/**
 * A player head. In 1.8-1.12 this was {@code SKULL_ITEM:3} with an owner name; now it is
 * {@link Material#PLAYER_HEAD} with a name-only {@link PlayerProfile}, which the client resolves to a skin.
 */
public class ItemHeadManager extends ItemStackManager {

	private String playerName;

	public ItemHeadManager(String playerName) {
		super(Material.PLAYER_HEAD);
		this.playerName = playerName;
	}

	public ItemHeadManager(ItemStack itemStack) {
		super(itemStack);

		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		PlayerProfile profile = meta.getPlayerProfile();
		this.playerName = profile != null ? profile.getName() : null;
	}

	public ItemHeadManager() {
		super(Material.PLAYER_HEAD);
	}

	@Override
	public ItemStack getItem() {
		ItemStack itemStack = super.getItem();
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		if (playerName != null)
			meta.setPlayerProfile(Bukkit.createProfile(playerName));

		itemStack.setItemMeta(meta);
		return itemStack;
	}

	@Override
	public boolean isSame(ItemStack itemStack) {
		if (!super.isSame(itemStack))
			return false;

		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		PlayerProfile profile = meta.getPlayerProfile();
		String owner = profile != null ? profile.getName() : null;
		if (owner != null) {
			if (playerName == null)
				return false;
			else if (!owner.equalsIgnoreCase(playerName))
				return false;
		} else if (playerName != null)
			return false;

		return true;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
}
