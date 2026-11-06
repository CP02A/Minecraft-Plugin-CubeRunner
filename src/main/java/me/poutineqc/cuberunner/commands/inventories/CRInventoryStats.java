package me.poutineqc.cuberunner.commands.inventories;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.poutineqc.cuberunner.AchievementManager.Achievement;
import me.poutineqc.cuberunner.CRPlayer;
import me.poutineqc.cuberunner.CRPlayer.PlayerStatsException;
import me.poutineqc.cuberunner.CRStats;
import me.poutineqc.cuberunner.CubeRunner;
import me.poutineqc.cuberunner.Language;
import me.poutineqc.cuberunner.Language.Messages;
import me.poutineqc.cuberunner.commands.InventoryItem;
import me.poutineqc.cuberunner.utils.ItemStackManager;
import me.poutineqc.cuberunner.utils.Utils;
import me.poutineqc.cuberunner.utils.LegacyColor;

public class CRInventoryStats extends CRInventory {

	public CRInventoryStats(CRPlayer crPlayer) {
		super(crPlayer);

		Language local = crPlayer.getLanguage();
		this.title = local.get(Messages.STATS_GUI_TITLE);
		this.amountOfRows = 6;
		createInventory();

		try {
			fillInventory();
		} catch (PlayerStatsException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fillInventory() throws PlayerStatsException {

		Language local = crPlayer.getLanguage();
		int location;
		InventoryItem icon;

		/***************************************************
		 * Glass Spacer
		 ***************************************************/

		icon = new InventoryItem(new ItemStackManager(Material.PURPLE_STAINED_GLASS_PANE));
		icon.getItem().setDisplayName(LegacyColor.RED + "");

		for (int i = 0; i < inventory.getSize(); i++)
			switch (i) {
			case 1:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 19:
			case 28:
			case 37:
			case 46:
				icon.setPosition(i);
				icon.addToInventory(inventory);
			}

		/***************************************************
		 * Stats
		 ***************************************************/

		NumberFormat format2 = new DecimalFormat("#0.00");
		NumberFormat format3 = new DecimalFormat("#0.000");
		
		icon = new InventoryItem(new ItemStackManager(Material.PAPER), 2);
		icon.getItem().setDisplayName(
				Utils.color(local.get(Messages.STATS_GUI_TITLE) + " : CubeRunner"));

		icon.getItem().addToLore(LegacyColor.STRIKETHROUGH + "" + LegacyColor.YELLOW + "----------------------------");
		icon.getItem().addToLore(local.get(Messages.STATS_INFO_AVERAGE_SCORE) + " : &e"
				+ format2.format(crPlayer.getDouble(CRStats.AVERAGE_SCORE)));
		icon.getItem()
				.addToLore(local.get(Messages.STATS_INFO_DISTANCE_RAN) + " : &e"
						+ format3.format(crPlayer.getDouble(CRStats.TOTAL_DISTANCE) / 1000) + " " + LegacyColor.GREEN
						+ local.get(Messages.KEYWORD_GENERAL_DISTANCE));
		icon.getItem().addToLore(LegacyColor.STRIKETHROUGH + "" + LegacyColor.YELLOW + "----------------------------");
		icon.getItem().addToLore(
				local.get(Messages.STATS_INFO_GAMES) + " : &e" + String.valueOf(crPlayer.getInt(CRStats.GAMES_PLAYED)));
		icon.getItem().addToLore(local.get(Messages.STATS_INFO_TOTAL_SCORE) + " : &e"
				+ String.valueOf(crPlayer.getInt(CRStats.TOTAL_SCORE)));
		icon.getItem().addToLore(
				local.get(Messages.STATS_INFO_KILLS) + " : &e" + String.valueOf(crPlayer.getInt(CRStats.KILLS)));
		icon.getItem().addToLore(local.get(Messages.STATS_INFO_MULTIPLAYER_WON) + " : &e"
				+ String.valueOf(crPlayer.getInt(CRStats.MULTIPLAYER_WON)));
		icon.getItem().addToLore(LegacyColor.STRIKETHROUGH + "" + LegacyColor.YELLOW + "----------------------------");
		icon.getItem().addToLore(LegacyColor.LIGHT_PURPLE + Utils.strip(local.get(Messages.STATS_INFO_TIME_PLAYED)) + ": "
				+ getTimePLayed(local, crPlayer.getInt(CRStats.TIME_PLAYED)));
		if (CubeRunner.get().isEconomyEnabled())
			icon.getItem()
					.addToLore(LegacyColor.LIGHT_PURPLE + Utils.strip(local.get(Messages.STATS_INFO_MONEY)) + ": &e"
							+ format2.format(crPlayer.getDouble(CRStats.MONEY)) + LegacyColor.GREEN
							+ CubeRunner.get().getEconomy().currencyNamePlural());

		icon.addToInventory(inventory);

		/***************************************************
		 * Top Ratio
		 ***************************************************/

		int position = 0;
		for (Top10 top : Top10.values()) {
			icon = new InventoryItem(new ItemStackManager(Material.MAP));
			icon.getItem().setDisplayName(top.getName(local));
			icon.getItem().addToLore(LegacyColor.STRIKETHROUGH + "" + LegacyColor.YELLOW + "----------------------------");

			List<CRPlayer> view = CubeRunner.get().getPlayerData().getViews().get(top.getCrStats()).getList();
			for (int i = 0; i < 10 && i < view.size(); i++)
				icon.getItem().addToLore(LegacyColor.LIGHT_PURPLE + view.get(i).getName() + " : " + LegacyColor.YELLOW
						+ top.getAmount(view.get(i)));

			icon.setPosition(position);
			icon.addToInventory(inventory);
			position += 9;
		}

		/***************************************************
		 * Achievements games
		 ***************************************************/
		int generalLocation = 20;
		HashMap<Achievement, LinkedHashMap<Integer, Double>> entries = CubeRunner.get().getAchievementManager()
				.getAchievements();
		for (Achievement achievement : Achievement.values()) {

			location = generalLocation;

			for (Entry<Integer, Double> goal : entries.get(achievement).entrySet()) {

				boolean done = goal.getKey() <= crPlayer.getInt(achievement.getCrStats());

				icon = new InventoryItem(
						new ItemStackManager(done ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL));
				icon.getItem().setDisplayName((done ? LegacyColor.GREEN : LegacyColor.RED) + Utils.strip(local
						.get(achievement.getAchievementMessage()).replace("%amount%", String.valueOf(goal.getKey()))));
				icon.getItem().addToLore(LegacyColor.YELLOW + "----------------------------");
				icon.getItem()
						.addToLore(LegacyColor.AQUA + Utils.strip(local.get(Messages.KEYWORD_STATS_PROGRESSION) + ": ")
								+ (done ? (LegacyColor.GREEN + Utils.strip(local.get(Messages.KEYWORD_STATS_COMPLETED)))
										: LegacyColor.YELLOW + String.valueOf(crPlayer.getInt(achievement.getCrStats())) + "/"
												+ String.valueOf(goal.getKey())));

				if (!done && CubeRunner.get().isEconomyEnabled()
						&& CubeRunner.get().getConfiguration().achievementsRewards)
					icon.getItem()
							.addToLore(LegacyColor.AQUA + Utils.strip(local.get(Messages.KEYWORD_STATS_REWARD)) + ": "
									+ LegacyColor.YELLOW + String.valueOf(goal.getValue())
									+ CubeRunner.get().getEconomy().currencyNamePlural());

				icon.setPosition(location++);
				icon.addToInventory(inventory);

			}

			generalLocation += 9;
		}

		/***************************************************
		 * Arrow
		 ***************************************************/

		icon = new InventoryItem(new ItemStackManager(Material.ARROW));

		icon.getItem().setDisplayName(local.get(Messages.STATS_CHALLENGES_TITLE));

		icon.setPosition(8);
		icon.addToInventory(inventory);

		/***************************************************
		 * Display
		 ***************************************************/

		openInventory();

	}

	private String getTimePLayed(Language local, int timePlayed) {
		long hours = 0;

		timePlayed /= 60000;
		while (timePlayed > 60) {
			timePlayed -= 60;
			hours++;
		}

		return LegacyColor.YELLOW + String.valueOf(hours) + LegacyColor.GREEN + " "
				+ Utils.strip(local.get(Messages.KEYWORD_GENERAL_HOURS)) + LegacyColor.YELLOW + " "
				+ String.valueOf(timePlayed) + LegacyColor.GREEN + " "
				+ Utils.strip(local.get(Messages.KEYWORD_GENERAL_MINUTES));
	}

	@Override
	public void update(ItemStack itemStack, InventoryAction action) {
		Language local = crPlayer.getLanguage();

		if (Utils.isEqualOnColorStrip(Utils.legacy(itemStack.getItemMeta().displayName()),
				local.get(Messages.STATS_CHALLENGES_TITLE))) {
			new CRInventoryChallenge(crPlayer);
		}
	}

	private enum Top10 {
		AVERAGE_SCORE(CRStats.AVERAGE_SCORE, Messages.STATS_INFO_AVERAGE_SCORE) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return LegacyColor.YELLOW + new DecimalFormat("#0.00").format(player.getDouble(getCrStats()));
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		},
		TOTAL_DISTANCE(CRStats.TOTAL_DISTANCE, Messages.STATS_INFO_DISTANCE_RAN) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return LegacyColor.YELLOW + new DecimalFormat("#0.000").format(player.getDouble(getCrStats()) / 1000) + " "
							+ LegacyColor.GREEN + player.getLanguage().get(Messages.KEYWORD_GENERAL_DISTANCE);
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		},
		GAMES(CRStats.GAMES_PLAYED, Messages.STATS_INFO_GAMES) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return String.format("%1$d", player.getInt(getCrStats()));
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		},
		TOTAL_SCORE(CRStats.TOTAL_SCORE, Messages.STATS_INFO_TOTAL_SCORE) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return String.format("%1$d", player.getInt(getCrStats()));
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		},
		KILLS(CRStats.KILLS, Messages.STATS_INFO_KILLS) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return String.format("%1$d", player.getInt(getCrStats()));
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		},
		MULTIPLAYER_WON(CRStats.MULTIPLAYER_WON, Messages.STATS_INFO_MULTIPLAYER_WON) {
			@Override
			public String getAmount(CRPlayer player) {
				try {
					return String.format("%1$d", player.getInt(getCrStats()));
				} catch (PlayerStatsException e) {
					e.printStackTrace();
					return "";
				}
			}
		};

		private CRStats crStats;
		private Messages message;

		private Top10(CRStats crStats, Messages message) {
			this.message = message;
			this.crStats = crStats;
		}

		public abstract String getAmount(CRPlayer player);

		public String getName(Language local) {
			return LegacyColor.GOLD + local.get(Messages.KEYWORD_STATS_TOP10) + " : " + local.get(message);
		}

		public CRStats getCrStats() {
			return crStats;
		}
	}
}
