package de.domisum.pistonjammer.register;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of the number of pistons in the chunks of the world supplied through the constructor.
 */
public class WorldChunkPistonRegister
{

	private World world;

	private Map<ChunkLocation, Integer> chunkPistonCount = new HashMap<>();


	// INIT
	public WorldChunkPistonRegister(World world)
	{
		this.world = world;
	}


	// GETTERS
	private boolean isChunkRegistered(int cX, int cZ)
	{
		return this.chunkPistonCount.containsKey(new ChunkLocation(cX, cZ));
	}

	private int getChunkPistons(int cX, int cZ)
	{
		if(!isChunkRegistered(cX, cZ))
			countChunkPistons(cX, cZ);

		return this.chunkPistonCount.get(new ChunkLocation(cX, cZ));
	}

	public int getChunkPistons(Chunk chunk)
	{
		return getChunkPistons(chunk.getX(), chunk.getZ());
	}


	// CHANGERS
	public void changeChunkPistons(int cX, int cZ, int delta)
	{
		int countBefore = getChunkPistons(cX, cZ);

		int newCount = countBefore+delta;
		if(newCount < 0)
			throw new IllegalArgumentException("The count can't go below 0");

		this.chunkPistonCount.put(new ChunkLocation(cX, cZ), newCount);
	}

	private void unloadChunkPistonCount(int cX, int cZ)
	{
		ChunkLocation chunkLocation = new ChunkLocation(cX, cZ);
		this.chunkPistonCount.remove(chunkLocation);
	}

	public void unloadChunkPistonCount(Chunk chunk)
	{
		unloadChunkPistonCount(chunk.getX(), chunk.getZ());
	}


	// COUNTING
	private void countChunkPistons(int cX, int cZ)
	{
		ChunkPistonCounter counter = new ChunkPistonCounter(this.world, cX, cZ);
		int count = counter.count();

		this.chunkPistonCount.put(new ChunkLocation(cX, cZ), count);
	}


	// CHUNK LOCATION
	@EqualsAndHashCode
	@AllArgsConstructor
	private class ChunkLocation
	{

		public final int cX;
		public final int cZ;

	}

}

