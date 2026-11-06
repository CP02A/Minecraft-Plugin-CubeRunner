package me.poutineqc.cuberunner.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import me.poutineqc.cuberunner.game.FallingCubes;

public class ListenerEntityChangeBlock implements Listener {

	/** Lets CubeRunner's cubes land even where a protection plugin cancels block changes. */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (FallingCubes.isCube(event.getEntity()) && event.getBlock().getType() == Material.AIR)
			event.setCancelled(false);
	}

}
