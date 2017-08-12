package de.domisum.pistonjammer.register;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public class ChunkPistonCounter
{

	// INPUTS
	private World world;
	private int cX;
	private int cZ;


	// INIT
	public ChunkPistonCounter(World world, int cX, int cZ)
	{
		this.world = world;
		this.cX = cX;
		this.cZ = cZ;
	}


	// COUNTING
	public int count()
	{
		ChunkSnapshot cs = this.world.getChunkAt(this.cX, this.cZ).getChunkSnapshot(true, false, false);

		int pistonCount = 0;

		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++)
				for(int y = 0; y < this.world.getMaxHeight(); y++)
				{
					@SuppressWarnings("deprecation") int block = cs.getBlockTypeId(x, y, z);

					if(block == 29 || block == 33)
						pistonCount++;
				}

		return pistonCount;
	}

}
