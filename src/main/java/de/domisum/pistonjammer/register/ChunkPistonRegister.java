package de.domisum.pistonjammer.register;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkPistonRegister
{

	private Map<World, WorldChunkPistonRegister> registers = new HashMap<>();


	// INIT
	public ChunkPistonRegister(List<World> worlds)
	{
		for(World world : worlds)
			this.registers.put(world, new WorldChunkPistonRegister(world));
	}


	// GETTERS
	public int getChunkPistons(Chunk chunk)
	{
		WorldChunkPistonRegister register = getRegister(chunk.getWorld());

		return register.getChunkPistons(chunk);
	}

	private WorldChunkPistonRegister getRegister(World world)
	{
		if(!this.registers.containsKey(world))
			throw new IllegalArgumentException("The world '"+world+"' is not tracked in this ChunkPistonRegister");

		return this.registers.get(world);
	}


	// CHANGERS
	public void changeChunkPistons(Chunk chunk, int delta)
	{
		WorldChunkPistonRegister register = getRegister(chunk.getWorld());

		register.changeChunkPistons(chunk.getX(), chunk.getZ(), delta);
	}

}
