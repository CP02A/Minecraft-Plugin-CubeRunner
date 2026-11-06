package me.poutineqc.cuberunner.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

/**
 * Reads a player's WorldEdit selection with the WorldEdit 7 API (the 6.x {@code Selection} API is gone).
 *
 * <p>WorldEdit is a soft dependency, so this class is only loaded after {@link #isAvailable()} returned
 * true. Works with FastAsyncWorldEdit too, which provides the same API.
 */
final class WorldEditSelection {

	final World world;
	final Location minPoint;
	final Location maxPoint;

	private WorldEditSelection(World world, BlockVector3 min, BlockVector3 max) {
		this.world = world;
		this.minPoint = new Location(world, min.x(), min.y(), min.z());
		this.maxPoint = new Location(world, max.x(), max.y(), max.z());
	}

	static boolean isAvailable() {
		return Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
				|| Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
	}

	/** The player's selection in their current world, or null if they have no complete selection. */
	static WorldEditSelection of(Player player) {
		com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
		LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);

		Region region;
		try {
			region = session.getSelection(actor.getWorld());
		} catch (IncompleteRegionException e) {
			return null;
		}

		return new WorldEditSelection(player.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
	}
}
