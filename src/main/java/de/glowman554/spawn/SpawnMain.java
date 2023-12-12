package de.glowman554.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import de.glowman554.spawn.listener.SpawnListener;

public class SpawnMain extends JavaPlugin
{
	private static SpawnMain instance;

	public SpawnMain()
	{
		instance = this;
	}

	private SpawnRegion[] regions;
	private FileConfiguration config = getConfig();

	private SpawnEntity loadSpawnEntity(String id)
	{
		String customName = config.getString(id + ".custom_name");
		int hp = config.getInt(id + ".hp");

		@SuppressWarnings("unchecked")
		PotionEffectType[] effectTypes = ((List<String>) config.getList(id + ".effects")).stream().map(e -> {
			PotionEffectType type = PotionEffectType.getByKey(NamespacedKey.fromString(e));
			if (type == null)
			{
				throw new IllegalArgumentException("Potion effect type " + e + " not found!");
			}
			return type;
		}).toArray(PotionEffectType[]::new);

		String entitiyTypeString = config.getString(id + ".type");
		EntityType entityType = EntityType.valueOf(entitiyTypeString.toUpperCase());

		return new SpawnEntity(entityType, customName, effectTypes, hp);
	}

	private void loadRegions()
	{
		ArrayList<SpawnRegion> dynamicRegions = new ArrayList<>();
		ConfigurationSection section = config.getConfigurationSection("regions");
		for (String key : section.getKeys(false))
		{
			int startX = section.getInt(key + ".startX");
			int startZ = section.getInt(key + ".startZ");
			int endX = section.getInt(key + ".endX");
			int endZ = section.getInt(key + ".endZ");

			String worldString = section.getString(key + ".world");
			World world = Bukkit.getWorld(worldString);
			if (world == null)
			{
				throw new IllegalArgumentException("World " + worldString + " not found!");
			}

			@SuppressWarnings("unchecked")
			List<String> categoriesString = (List<String>) section.getList(key + ".categories");
			SpawnEntity[] categories = categoriesString.stream().map(c -> loadSpawnEntity(c)).toArray(SpawnEntity[]::new);

			SpawnRegion finalRegion = new SpawnRegion(startX, startZ, endX, endZ, categories, world);
			dynamicRegions.add(finalRegion);

			getLogger().log(Level.INFO, finalRegion.toString());
		}

		regions = dynamicRegions.toArray(SpawnRegion[]::new);
	}

	@Override
	public void onLoad()
	{
		config.addDefault("regions.region1.startX", 0);
		config.addDefault("regions.region1.startZ", 0);
		config.addDefault("regions.region1.endX", 100);
		config.addDefault("regions.region1.endZ", 100);
		config.addDefault("regions.region1.world", "world");
		config.addDefault("regions.region1.categories", new String[] {"weak_golem", "premium_chicken"});

		config.addDefault("regions.region2.startX", 0);
		config.addDefault("regions.region2.startZ", 0);
		config.addDefault("regions.region2.endX", -100);
		config.addDefault("regions.region2.endZ", -100);
		config.addDefault("regions.region2.world", "world");
		config.addDefault("regions.region2.categories", new String[] {"premium_chicken"});

		config.addDefault("premium_chicken.custom_name", "Premium Chicken");
		config.addDefault("premium_chicken.hp", 200);
		config.addDefault("premium_chicken.effects", new String[] {"jump_boost", "speed"});
		config.addDefault("premium_chicken.type", "chicken");

		config.addDefault("weak_golem.custom_name", "Weak Golem");
		config.addDefault("weak_golem.hp", 6);
		config.addDefault("weak_golem.effects", new String[] {"weakness"});
		config.addDefault("weak_golem.type", "iron_golem");

		config.options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new SpawnListener(), this);
		
		loadRegions();
	}

	public static SpawnMain getInstance()
	{
		return instance;
	}

	public SpawnRegion[] getRegions()
	{
		return regions;
	}
}
