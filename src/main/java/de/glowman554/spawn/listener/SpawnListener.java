package de.glowman554.spawn.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

import de.glowman554.spawn.SpawnMain;
import de.glowman554.spawn.SpawnRegion;

public class SpawnListener implements Listener
{
	private void run(Location location)
	{
		for (SpawnRegion region : SpawnMain.getInstance().getRegions())
		{
			region.run(location);
		}
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.NATURAL)
		{
			run(event.getLocation());
		}
	}

	@EventHandler
	public void onChunkLoadEvent(ChunkLoadEvent event)
	{
		if (event.isNewChunk())
		{
			for (Entity entity : event.getChunk().getEntities())
			{
				run(entity.getLocation());
			}
		}
	}
}
