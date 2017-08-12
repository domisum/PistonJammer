package de.domisum.pistonjammer;

import de.domisum.lib.auxilium.util.TextUtil;
import de.domisum.pistonjammer.register.ChunkPistonRegister;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * This plugin blocks redstone events in chunks that contain more than a specified ammount of pistons in specified worlds.
 * <p>
 * Both the threshold ammount of pistons and the active worlds can be changed in the configuration file.
 * <p>
 * The current number of pistons in each chunk of the active worlds are tracked in {@link ChunkPistonRegister}
 * and event handling is done in the {@link PistonJammerListener}.
 */
public class PistonJammer extends JavaPlugin
{

	// REFERENCES
	@Getter private static PistonJammer instance;

	@Getter private ChunkPistonRegister chunkPistonRegister;

	// SETTINGS
	@Getter private int maxPistons;
	private List<String> worldNames;
	@Getter private List<World> worlds;


	// INIT
	@Override public void onEnable()
	{
		instance = this;

		initConfig();
		loadConfigValues();

		this.chunkPistonRegister = new ChunkPistonRegister(this.worlds);
		new PistonJammerListener();

		getLogger().info("Max pistons per chunk: "+this.maxPistons);
		getLogger().info("Active in worlds: "+TextUtil.getListAsString(this.worldNames));


		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	@Override public void onDisable()
	{
		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}


	// CONFIG
	private void initConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void loadConfigValues()
	{
		this.maxPistons = getConfig().getInt("maxPistons");
		this.worldNames = getConfig().getStringList("worlds");

		this.worlds = new ArrayList<>();
		for(String worldName : this.worldNames)
			this.worlds.add(Bukkit.getWorld(worldName));
	}

}
