package de.domisum.pistonjammer;

import de.domisum.lib.auxiliumspigot.AuxiliumSpigotLib;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class PistonJammer extends JavaPlugin
{

	// REFERENCES
	@Getter private static PistonJammer instance;


	// INIT
	@Override public void onEnable()
	{
		instance = this;

		AuxiliumSpigotLib.enable(this);

		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	@Override public void onDisable()
	{
		AuxiliumSpigotLib.disable();

		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}

}
