package me.poutineqc.cuberunner.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;

import me.poutineqc.cuberunner.ArenaData;
import me.poutineqc.cuberunner.CubeRunner;
import me.poutineqc.cuberunner.MySQL;
import me.poutineqc.cuberunner.utils.ColorBlocks;
import me.poutineqc.cuberunner.utils.ItemStackManager;

public class ColorManager {

	private long colorIndice;
	private List<ItemStackManager> allBlocks;
	private List<ItemStackManager> onlyChoosenBlocks;
	private MySQL mysql;
	private Arena arena;
	private ArenaData arenaData;

	public ColorManager(Long colorIndice, CubeRunner plugin, Arena arena) {
		this.colorIndice = colorIndice;
		this.mysql = plugin.getMySQL();
		this.arenaData = plugin.getArenaData();
		this.arena = arena;
		updateLists();
	}

	public void setColorIndice(long colorIndice) {
		this.colorIndice = colorIndice;
		updateLists();

		if (mysql.hasConnection()) {
			mysql.update("UPDATE " + mysql.table(MySQL.ARENAS) + " SET colorIndice = ? WHERE name = ?", colorIndice,
					arena.getName());
		} else {
			arenaData.getData().set("arenas." + arena.getName() + ".colorIndice", colorIndice);
			arenaData.saveArenaData();
		}
	}

	public void updateLists() {
		allBlocks = new ArrayList<ItemStackManager>();
		onlyChoosenBlocks = new ArrayList<ItemStackManager>();

		for (int i = 0; i < ColorBlocks.COUNT; i++) {
			ItemStackManager icon = new ItemStackManager(ColorBlocks.get(i));

			if ((colorIndice & (1L << i)) != 0) {
				icon.addEnchantement(Enchantment.UNBREAKING, 1);
				onlyChoosenBlocks.add(icon);
			}

			allBlocks.add(icon);
		}

		if (onlyChoosenBlocks.size() == 0)
			onlyChoosenBlocks = allBlocks;
	}

	public ItemStackManager getRandomAvailableBlock() {
		return onlyChoosenBlocks.get((int) Math.floor(Math.random() * onlyChoosenBlocks.size()));
	}

	public List<ItemStackManager> getAllBlocks() {
		return allBlocks;
	}

	public List<ItemStackManager> getOnlyChoosenBlocks() {
		return onlyChoosenBlocks;
	}

	public long getColorIndice() {
		return colorIndice;
	}

}
