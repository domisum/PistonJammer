package de.domisum.pistonjammer;

import de.domisum.lib.auxiliumspigot.util.DebugUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class PistonJammerListener implements Listener
{

	// INIT
	public PistonJammerListener()
	{
		registerListener();
	}

	private void registerListener()
	{
		Plugin instance = PistonJammer.getInstance();
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}


	// EVENTS
	@EventHandler public void onTest(PlayerInteractEvent event)
	{
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Location location = event.getClickedBlock().getLocation();

		int count = PistonJammer.getInstance().getChunkPistonRegister().getChunkPistons(location.getChunk());
		DebugUtil.say("count: "+count);
	}

}
