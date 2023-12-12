package de.glowman554.spawn;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

public class SpawnRegion
{
	private final int startX;
	private final int startZ;
	private final int endX;
	private final int endZ;

	private final SpawnEntity[] entities;

	private final World world;

	private Random random = new Random();

	public SpawnRegion(int startX, int startZ, int endX, int endZ, SpawnEntity[] entities, World world)
	{
		this.startX = startX;
		this.startZ = startZ;
		this.endX = endX;
		this.endZ = endZ;
		this.entities = entities;
		this.world = world;
	}

	private boolean checkBounds(int x, int z)
	{
		return x >= startX && x <= endX && z >= startZ && z <= endZ;
	}

	public void run(Location location)
	{
		if (checkBounds(location.getBlockX(), location.getBlockZ()) && location.getWorld().getName().equals(world.getName()))
		{
			int entityIdx = random.nextInt(0, entities.length);
			entities[entityIdx].spawnEntity(location);
		}
	}

	@Override
	public String toString()
	{
		return String.format("%d,%d -> %d,%d in world %s with %d different entities being abel to spawn", startX, startZ, endX, endZ, world.getName(), entities.length);
	}

}
