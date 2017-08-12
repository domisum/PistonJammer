package de.domisum.pistonjammer.register;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

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
			throw new IllegalArgumentException("The count cant go below 0");

		this.chunkPistonCount.put(new ChunkLocation(cX, cZ), newCount);
	}


	// COUNTING
	private void countChunkPistons(int cX, int cZ)
	{
		ChunkPistonCounter counter = new ChunkPistonCounter(this.world, cX, cZ);
		int count = counter.count();

		this.chunkPistonCount.put(new ChunkLocation(cX, cZ), count);
	}

}

