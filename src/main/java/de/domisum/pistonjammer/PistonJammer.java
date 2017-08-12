package de.domisum.pistonjammer;

import de.domisum.lib.auxilium.util.TextUtil;
import de.domisum.lib.auxiliumspigot.AuxiliumSpigotLib;
import de.domisum.pistonjammer.register.ChunkPistonRegister;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PistonJammer extends JavaPlugin
{

	// REFERENCES
	@Getter private static PistonJammer instance;

	@Getter ChunkPistonRegister chunkPistonRegister;

	// SETTINGS
	@Getter private int maxPistons;
	private List<String> worldNames;
	@Getter private List<World> worlds;


	// INIT
	@Override public void onEnable()
	{
		instance = this;

		AuxiliumSpigotLib.enable(this);

		initConfig();
		loadConfigValues();

		this.chunkPistonRegister = new ChunkPistonRegister(this.worlds);
		new PistonJammerListener();

		getLogger().info("Max pistons per chunk: "+this.maxPistons);
		getLogger().info("Active in worldNames: "+TextUtil.getListAsString(this.worldNames));


		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	@Override public void onDisable()
	{
		AuxiliumSpigotLib.disable();


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
		this.worldNames = getConfig().getStringList("worldNames");

		this.worlds = new ArrayList<>();
		for(String worldName : this.worldNames)
			this.worlds.add(Bukkit.getWorld(worldName));
	}

}
