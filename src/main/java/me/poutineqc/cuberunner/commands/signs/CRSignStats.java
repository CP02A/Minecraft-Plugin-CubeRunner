package me.poutineqc.cuberunner.commands.signs;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import me.poutineqc.cuberunner.Language;
import me.poutineqc.cuberunner.Language.Messages;
import me.poutineqc.cuberunner.commands.CRCommand;
import me.poutineqc.cuberunner.utils.Permissions;
import me.poutineqc.cuberunner.utils.SignLines;

public class CRSignStats extends CRSign {

	public CRSignStats(SignChangeEvent event) {
		super(event.getBlock().getLocation(), SignType.STATS);

		Language local = Language.getDefault();
		SignLines.set(event, 0, "");
		SignLines.set(event, 1, ChatColor.translateAlternateColorCodes('&', local.get(Messages.PREFIX_LONG)));
		SignLines.set(event, 2, ChatColor.translateAlternateColorCodes('&', local.get(Messages.KEYWORD_SIGN_STATS)));
		SignLines.set(event, 3, "");

		signs.add(this);

	}

	public CRSignStats(UUID uuid, Location location) {
		super(uuid, location, SignType.STATS);

		boolean delete = false;
		BlockState block = null;
		try {
			block = location.getBlock().getState();
			if (!(block instanceof Sign))
				delete = true;
		} catch (NullPointerException e) {
			delete = true;
		}

		if (delete) {
			removeSign();
			return;
		}

		signs.add(this);
	}

	@Override
	protected boolean updateSign(Language local, Sign sign) {
		SignLines.set(sign, 1, ChatColor.translateAlternateColorCodes('&', local.get(Messages.PREFIX_LONG)));
		SignLines.set(sign, 2, ChatColor.translateAlternateColorCodes('&', local.get(Messages.KEYWORD_SIGN_STATS)));
		sign.update();

		return true;
	}

	@Override
	public void onInteract(Player player) {
		if (Permissions.hasPermission(CRCommand.STATS.getPermission(), player, true))
			CRCommand.STATS.execute(plugin, player, new String[0]);
	}

}