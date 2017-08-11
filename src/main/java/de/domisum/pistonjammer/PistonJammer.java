package de.domisum.pistonjammer;

import de.domisum.lib.auxiliumspigot.AuxiliumSpigotLib;
import de.domisum.lib.codex.mysql.MySQLConPoolWrapper;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class PistonJammer extends JavaPlugin
{

	// REFERENCES
	@Getter private static PistonJammer instance;
	private MySQLConPoolWrapper mySQLConPool;


	// INIT
	@Override public void onEnable()
	{
		instance = this;

		AuxiliumSpigotLib.enable(this);

		initConfig();
		connectToDatabase();


		getLogger().info(this.getClass().getSimpleName()+" has been enabled");
	}

	@Override public void onDisable()
	{
		this.mySQLConPool.close();

		AuxiliumSpigotLib.disable();


		getLogger().info(this.getClass().getSimpleName()+" has been disabled");
	}

	private void initConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}


	// DATABASE
	private void connectToDatabase()
	{
		String serverAddress = getConfig().getString("database.serverAddress");
		int port = getConfig().getInt("database.port");
		String username = getConfig().getString("database.username");
		String password = getConfig().getString("database.password");

		getLogger().info("Connecting to MySQL database '"+serverAddress+":"+port+"' with username '"+username+"' using password '"
				+"XXXXX"+"'");

		this.mySQLConPool = new MySQLConPoolWrapper(serverAddress, port, username, password);
	}

}
