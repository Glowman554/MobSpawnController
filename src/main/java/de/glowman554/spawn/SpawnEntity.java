package de.glowman554.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class SpawnEntity
{
	private final EntityType type;
	private final String customName;
	private final PotionEffectType[] effectTypes;
	private final int hp;

	public SpawnEntity(EntityType type, String customName, PotionEffectType[] effectTypes, int hp)
	{
		this.type = type;
		this.customName = customName;
		this.effectTypes = effectTypes;
		this.hp = hp;
	}

	private Player getNearestPlayerToLocation(Location targetLocation)
	{
		Player nearestPlayer = null;
		double nearestDistanceSquared = Double.MAX_VALUE;

		for (Player player : Bukkit.getOnlinePlayers())
		{
			double distanceSquared = player.getLocation().distanceSquared(targetLocation);

			if (distanceSquared < nearestDistanceSquared)
			{
				nearestDistanceSquared = distanceSquared;
				nearestPlayer = player;
			}
		}

		return nearestPlayer;
	}

	public void spawnEntity(Location location)
	{
		location.getWorld().spawn(location, type.getEntityClass(), true, (e) -> {
			e.setCustomName(ChatColor.translateAlternateColorCodes('&', customName));
			e.setCustomNameVisible(true);

			if (e instanceof LivingEntity)
			{
				LivingEntity livingE = (LivingEntity) e;

				livingE.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
				livingE.setHealth(hp);

				for (PotionEffectType type : effectTypes)
				{
					PotionEffect effect = new PotionEffect(type, -1, 0);
					effect.apply(livingE);
				}

				if (livingE instanceof Mob)
				{
					Mob mob = (Mob) livingE;
					Player nearest = getNearestPlayerToLocation(location);
					if (nearest != null)
					{
						mob.setTarget(nearest);
					}
				}
			}

		});
	}
}
