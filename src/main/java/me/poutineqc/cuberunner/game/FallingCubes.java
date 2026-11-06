package me.poutineqc.cuberunner.game;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import me.poutineqc.cuberunner.CubeRunner;

/**
 * Spawns the falling cubes of a game.
 *
 * <p>The 1.8-1.12 plugin built {@code EntityFallingBlock}s through NMS reflection and stored the UUID of the
 * player the cube was aimed at in the entity's custom name. This uses the Bukkit API instead and keeps the
 * UUID in the entity's persistent data container.
 */
public final class FallingCubes {

	/** 1.8-1.12 vanilla defaults for falling blocks that hurt entities (anvils): 2 damage per block, max 40. */
	private static final float DAMAGE_PER_BLOCK = 2.0F;
	private static final int MAX_DAMAGE = 40;

	private static NamespacedKey targetKey;

	private FallingCubes() {
	}

	private static NamespacedKey targetKey() {
		if (targetKey == null)
			targetKey = new NamespacedKey(CubeRunner.get(), "target");
		return targetKey;
	}

	/**
	 * Spawns a cube that will crush whoever it lands on.
	 *
	 * @param target the player the cube was aimed at, credited with a kill if it crushes someone else
	 * @param push whether to give the cube a small random horizontal push
	 */
	public static FallingBlock spawn(Location location, Material material, UUID target, boolean push) {
		FallingBlock cube = location.getWorld().spawn(location, FallingBlock.class, falling -> {
			falling.setBlockData(material.createBlockData());
			falling.setDropItem(false);
			falling.setCancelDrop(false);
			falling.setDamagePerBlock(DAMAGE_PER_BLOCK);
			falling.setMaxDamage(MAX_DAMAGE);
			falling.setHurtEntities(true);
			falling.setPersistent(false);
			falling.getPersistentDataContainer().set(targetKey(), PersistentDataType.STRING, target.toString());
		});

		if (push)
			cube.setVelocity(new Vector((1 - (Math.random() * 2)) / 10.0, 0, (1 - (Math.random() * 2)) / 10.0));

		return cube;
	}

	/** Whether the entity is a CubeRunner cube. */
	public static boolean isCube(Entity entity) {
		return entity instanceof FallingBlock
				&& entity.getPersistentDataContainer().has(targetKey(), PersistentDataType.STRING);
	}

	/** The UUID of the player the cube was aimed at, or null if the entity is not a CubeRunner cube. */
	public static UUID getTarget(Entity entity) {
		String uuid = entity.getPersistentDataContainer().get(targetKey(), PersistentDataType.STRING);
		if (uuid == null)
			return null;

		try {
			return UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
