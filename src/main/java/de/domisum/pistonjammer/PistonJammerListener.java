package de.domisum.pistonjammer;

import de.domisum.pistonjammer.register.ChunkPistonRegister;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

/**
 * This listener processes the redstone event, which it will cancel if the number of pistons in its chunk are too high.
 * <p>
 * It listens to chunk load events and counts then number of pistons in the chunk when one is loaded. When a chunk is unloaded,
 * the number of pistons of this chunk is cleared from memory.
 * <p>
 * It also processes events which change the number of pistons in the chunk
 * and alters the number of pistons in the chunk saved in the ChunkPistonRegister accordingly.
 */
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


	// EVENTS: CHUNK LOAD
	@EventHandler public void onChunkLoad(ChunkLoadEvent event)
	{
		if(!isActiveInWorld(event.getWorld()))
			return;

		// call this to count the pistons
		getChunkPistonRegister().getChunkPistons(event.getChunk());
	}

	@EventHandler public void onChunkUnload(ChunkUnloadEvent event)
	{
		if(!isActiveInWorld(event.getWorld()))
			return;

		getChunkPistonRegister().unloadChunkPistonCount(event.getChunk());
	}


	// EVENTS: REDSTONE
	@EventHandler public void cancelRedstone(BlockRedstoneEvent event)
	{
		if(isRedstoneActivated(event.getBlock().getChunk()))
			return;

		// this effectively cancels the event
		event.setNewCurrent(event.getOldCurrent());
	}

	@EventHandler public void cancelPistonExtend(BlockPistonExtendEvent event)
	{
		if(isRedstoneActivated(event.getBlock().getChunk()))
			return;

		event.setCancelled(true);
	}

	@EventHandler public void cancelPistonRetract(BlockPistonRetractEvent event)
	{
		if(isRedstoneActivated(event.getBlock().getChunk()))
			return;

		event.setCancelled(true);
	}


	// EVENTS: CHANGE

	/**
	 * When a piston is placed by a player in a chunk add one to the piston count of that chunk.
	 *
	 * @param event the blockPlaceEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockPlace(BlockPlaceEvent event)
	{
		if(event.isCancelled())
			return;

		checkChangeChunkPistons(event.getBlockPlaced(), +1);
	}

	/**
	 * When a piston is broken by a player in a chunk subtract one from the piston count of that chunk.
	 *
	 * @param event the blockBreakEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event)
	{
		if(event.isCancelled())
			return;

		checkChangeChunkPistons(event.getBlock(), -1);
	}

	/**
	 * When an entity explodes and destroys a piston, remove one from the chunk's piston count.
	 *
	 * @param event the entityExplodeEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onEntityExplode(EntityExplodeEvent event)
	{
		if(event.isCancelled())
			return;

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
		if(event.isCancelled())
			return;

		for(Block block : event.blockList())
			checkChangeChunkPistons(block, -1);
	}

	/**
	 * @param event the piston event
	 * @see #pistonMoveBlocks(List, BlockFace)
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockMoveByPiston(BlockPistonExtendEvent event)
	{
		if(event.isCancelled())
			return;

		pistonMoveBlocks(event.getBlocks(), event.getDirection());
	}

	/**
	 * @param event the piston event
	 * @see #pistonMoveBlocks(List, BlockFace)
	 */
	@EventHandler(priority = EventPriority.MONITOR) public void onBlockMoveByPiston(BlockPistonRetractEvent event)
	{
		if(event.isCancelled())
			return;

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

		if(blocks.isEmpty())
			return;
		if(!isActiveInWorld(blocks.get(0).getWorld()))
			return;

		for(Block block : blocks)
		{
			Block newBlock = block.getRelative(direction);

			Chunk oldChunk = block.getChunk();
			Chunk newChunk = newBlock.getChunk();

			if(Objects.equals(oldChunk, newChunk))
				continue;

			if(!isPiston(block))
				continue;

			getChunkPistonRegister().changeChunkPistons(block.getChunk(), -1);
			getChunkPistonRegister().changeChunkPistons(newBlock.getChunk(), +1);
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
		if(!isActiveInWorld(chunk.getWorld()))
			return true;

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

		if(!isPiston(block))
			return;

		getChunkPistonRegister().changeChunkPistons(block.getChunk(), delta);
	}

	private static boolean isPiston(Block block)
	{
		Material material = block.getType();
		return material == Material.PISTON_BASE || material == Material.PISTON_STICKY_BASE;
	}
}
