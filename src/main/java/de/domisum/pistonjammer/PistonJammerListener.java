package de.domisum.pistonjammer;

import de.domisum.lib.auxiliumspigot.util.DebugUtil;
import de.domisum.pistonjammer.register.ChunkPistonRegister;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import java.util.List;

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


	@EventHandler public void onTest(PlayerInteractEvent event)
	{

		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if(event.getHand() != EquipmentSlot.HAND)
			return;

		Location location = event.getClickedBlock().getLocation();
		if(!PistonJammer.getInstance().getWorlds().contains(location.getWorld()))
			return;

		int count = PistonJammer.getInstance().getChunkPistonRegister().getChunkPistons(location.getChunk());
		DebugUtil.say("count: "+count);
	}

	// EVENTS: REDSTONE

	/**
	 * Cancels the redstone event if the number of pistons in the chunk are too high.
	 *
	 * @param event the redstoneEvent
	 */
	@EventHandler public void onRedstone(BlockRedstoneEvent event)
	{
		if(isRedstoneActivated(event.getBlock().getChunk()))
			return;

		// this effectively cancels the event
		event.setNewCurrent(event.getOldCurrent());
	}


	// EVENTS: CHANGE

	/**
	 * When a piston is placed by a player in a chunk add one to the piston count of that chunk.
	 *
	 * @param event the blockPlaceEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockPlace(BlockPlaceEvent event)
	{
		checkChangeChunkPistons(event.getBlockPlaced(), +1);
	}

	/**
	 * When a piston is broken by a player in a chunk subtract one from the piston count of that chunk.
	 *
	 * @param event the blockBreakEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event)
	{
		checkChangeChunkPistons(event.getBlock(), -1);
	}

	/**
	 * When an entity explodes and destroys a piston, remove one from the chunk's piston count.
	 *
	 * @param event the entityExplodeEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onEntityExplode(EntityExplodeEvent event)
	{
		for(Block block : event.blockList())
			checkChangeChunkPistons(block, -1);
	}

	/**
	 * When a block explodes and destroys a piston, remove one from the chunk's piston count.
	 *
	 * @param event the blockExplodeEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockExplode(BlockExplodeEvent event)
	{
		for(Block block : event.blockList())
			checkChangeChunkPistons(block, -1);
	}

	@EventHandler(priority = EventPriority.MONITOR) public void onBlockMoveByPiston(BlockPistonExtendEvent event)
	{
		pistonMoveBlocks(event.getBlocks(), event.getDirection());
	}

	@EventHandler(priority = EventPriority.MONITOR) public void onBlockMoveByPiston(BlockPistonRetractEvent event)
	{
		pistonMoveBlocks(event.getBlocks(), event.getDirection());
	}

	/**
	 * When a piston moves other pistons, check if they cross a chunk border.
	 * If this is the case, subtract one from the piston count of the chunk that the piston is leaving
	 * and add one to the piston count of the chunk the piston is entering.
	 *
	 * @param blocks    the blocks of the movement
	 * @param direction the direction of the movement
	 */
	private void pistonMoveBlocks(List<Block> blocks, BlockFace direction)
	{
		// blocks can't change chunk if they move up or down
		if(direction == BlockFace.DOWN || direction == BlockFace.UP)
			return;

		for(Block block : blocks)
		{
			Block newBlock = block.getRelative(direction);

			Chunk oldChunk = block.getChunk();
			Chunk newChunk = newBlock.getChunk();

			if(oldChunk == newChunk)
				continue;

			checkChangeChunkPistons(block, -1);
			checkChangeChunkPistons(newBlock, +1);
		}
	}


	// SHORTCUTS
	private boolean isActiveInWorld(World world)
	{
		return PistonJammer.getInstance().getWorlds().contains(world);
	}

	private ChunkPistonRegister getChunkPistonRegister()
	{
		return PistonJammer.getInstance().getChunkPistonRegister();
	}

	private boolean isRedstoneActivated(Chunk chunk)
	{
		int pistonCount = getChunkPistonRegister().getChunkPistons(chunk);

		return pistonCount <= PistonJammer.getInstance().getMaxPistons();
	}


	/**
	 * Checks if the supplied block is in a world this plugin is working in and if the block is actually a piston.
	 * If this is the case, changes the piston count of the block's chunk by delta.
	 *
	 * @param block the block to check
	 * @param delta the amount for what the chunk's piston count should be changed if check is true
	 */
	private void checkChangeChunkPistons(Block block, int delta)
	{
		if(!isActiveInWorld(block.getWorld()))
			return;

		Material material = block.getType();
		if(!(material == Material.PISTON_BASE || material == Material.PISTON_STICKY_BASE))
			return;

		getChunkPistonRegister().changeChunkPistons(block.getChunk(), delta);
	}

}
