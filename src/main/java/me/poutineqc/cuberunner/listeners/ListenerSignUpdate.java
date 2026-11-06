package me.poutineqc.cuberunner.listeners;

import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.poutineqc.cuberunner.CubeRunner;
import me.poutineqc.cuberunner.Language;
import me.poutineqc.cuberunner.Language.Messages;
import me.poutineqc.cuberunner.commands.signs.CRSignJoin;
import me.poutineqc.cuberunner.commands.signs.CRSignPlay;
import me.poutineqc.cuberunner.commands.signs.CRSignQuit;
import me.poutineqc.cuberunner.commands.signs.CRSignStart;
import me.poutineqc.cuberunner.commands.signs.CRSignStats;
import me.poutineqc.cuberunner.commands.signs.CRSignTop;
import me.poutineqc.cuberunner.game.Arena;
import me.poutineqc.cuberunner.utils.Permissions;
import me.poutineqc.cuberunner.utils.SignLines;
import me.poutineqc.cuberunner.utils.Utils;

public class ListenerSignUpdate implements Listener {

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		// CubeRunner signs are read from and written to the front side only.
		if (event.getSide() != Side.FRONT)
			return;

		Language local = CubeRunner.get().getLang(event.getPlayer());

		if (isPrefixInLine(SignLines.get(event, 0))) {
			if (!Permissions.hasPermission(Permissions.createSign, event.getPlayer(), false)) {
				setNoPermissionsSign(event, local);
				return;
			}

			if (SignLines.get(event, 1).equalsIgnoreCase("join")) {
				Arena arena = Arena.getArena(SignLines.get(event, 2));
				if (arena != null) {
					new CRSignJoin(event, arena);
				} else {
					setNoValidSign(event, local);
				}
			} else if (SignLines.get(event, 1).equalsIgnoreCase("play")) {
				Arena arena = Arena.getArena(SignLines.get(event, 2));
				if (arena != null) {
					if (arena.getWorld() == null) {
						setNoValidSign(event, local);
					} else if (arena.getWorld() != event.getBlock().getWorld()) {
						setNoValidSign(event, local);
					} else {
						new CRSignPlay(event, arena);
					}
				} else {
					setNoValidSign(event, local);
				}
			} else if (SignLines.get(event, 1).equalsIgnoreCase("quit")) {
				new CRSignQuit(event);

			} else if (SignLines.get(event, 1).equalsIgnoreCase("top")) {
				Arena arena = Arena.getArena(SignLines.get(event, 2));
				if (arena != null) {
					new CRSignTop(event, arena);
				} else {
					setNoValidSign(event, local);
				}
			} else if (SignLines.get(event, 1).equalsIgnoreCase("start")) {
				new CRSignStart(event);

			} else if (SignLines.get(event, 1).equalsIgnoreCase("stats")) {
				new CRSignStats(event);

			} else {
				setNoValidSign(event, local);
			}
		} else if (isPrefixInLine(SignLines.get(event, 1)) || isPrefixInLine(SignLines.get(event, 2))
				|| isPrefixInLine(SignLines.get(event, 3))) {

			if (Permissions.hasPermission(Permissions.createSign, event.getPlayer(), false))
				setNoValidSign(event, local);
			else
				setNoPermissionsSign(event, local);
		}
	}

	private void setNoPermissionsSign(SignChangeEvent e, Language local) {
		SignLines.set(e, 0, Utils.color(local.get(Messages.SIGN_PERM_0)));
		SignLines.set(e, 1, Utils.color(local.get(Messages.SIGN_PERM_1)));
		SignLines.set(e, 2, Utils.color(local.get(Messages.SIGN_PERM_2)));
		SignLines.set(e, 3, Utils.color(local.get(Messages.SIGN_PERM_3)));
	}

	private void setNoValidSign(SignChangeEvent e, Language local) {
		SignLines.set(e, 0, Utils.color(local.get(Messages.PREFIX_LONG)));
		SignLines.set(e, 1, Utils.color(local.get(Messages.SIGN_VALID_1)));
		SignLines.set(e, 2, Utils.color(local.get(Messages.SIGN_VALID_2)));
		SignLines.set(e, 3, Utils.color(local.get(Messages.SIGN_VALID_3)));
	}

	private boolean isPrefixInLine(String line) {
		Language local = Language.getDefault();
		String stipedLine = Utils.strip(Utils.color(line)).toLowerCase();
		return stipedLine.contains("[cr]")
				|| stipedLine.contains(Utils.strip(Utils.color(local.get(Messages.PREFIX_LONG).toLowerCase().trim())))
				|| stipedLine.contains(Utils.strip(
						Utils.color(local.get(Messages.PREFIX_SHORT).toLowerCase().trim())));
	}

}
