package kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random rand;

	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
		mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
	}

	public ChunkProviderCaveworld(World world, long seed)
	{
		this.worldObj = world;
		this.rand = new Random(seed);
	}

	@Override
	public Chunk loadChunk(int chunkX, int chunkZ)
	{
		return provideChunk(chunkX, chunkZ);
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blocks = new byte[32768];
		Arrays.fill(blocks, (byte)Block.stone.blockID);

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 127; y >= 0; --y)
				{
					int var1 = (z * 16 + x) * 128 + y;

					if (y <= 0 || y >= 127)
					{
						blocks[var1] = (byte)Block.bedrock.blockID;
					}
					else
					{
						blocks[var1] = (byte)Block.stone.blockID;
					}
				}
			}
		}

		if (Config.generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateMineshaft)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		Chunk chunk = new Chunk(worldObj, blocks, chunkX, chunkZ);
		chunk.generateSkylightMap();

		return chunk;
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ)
	{
		BlockSand.fallInstantly = true;

		int var1 = chunkX * 16;
		int var2 = chunkZ * 16;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(var1 + 16, var2 + 16);
		rand.setSeed(worldObj.getSeed());
		long var3 = rand.nextLong() / 2L * 2L + 1L;
		long var4 = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed((long)chunkX * var3 + (long)chunkZ * var4 ^ worldObj.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, rand, chunkX, chunkZ, false));

		if (Config.generateMineshaft)
		{
			mineshaftGenerator.generateStructuresInChunk(worldObj, rand, chunkX, chunkZ);
		}

		if (TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, false, EventType.LAKE) && rand.nextInt(4) == 0)
		{
			int x = var1 + rand.nextInt(16) + 8;
			int y = rand.nextInt(128);
			int z = var2 + rand.nextInt(16) + 8;

			(new WorldGenLakes(Block.waterStill.blockID)).generate(worldObj, rand, x, y, z);
		}

		if (TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, false, EventType.LAVA) && rand.nextInt(8) == 0)
		{
			int x = var1 + rand.nextInt(16) + 8;
			int y = rand.nextInt(rand.nextInt(120) + 8);
			int z = var2 + rand.nextInt(16) + 8;

			if (y < 63 || rand.nextInt(10) == 0)
			{
				(new WorldGenLakes(Block.lavaStill.blockID)).generate(worldObj, rand, x, y, z);
			}
		}

		for (int i = 0; TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, false, EventType.DUNGEON) && i < 8; ++i)
		{
			int x = var1 + rand.nextInt(16) + 8;
			int y = rand.nextInt(128);
			int z = var2 + rand.nextInt(16) + 8;

			(new WorldGenDungeons()).generate(worldObj, rand, x, y, z);
		}

		biome.decorate(worldObj, rand, var1, var2);

		for (int i = 0; i < rand.nextInt(3) + 4; ++i)
		{
			int x = var1 + rand.nextInt(16) + 8;
			int y = rand.nextInt(52) + 64;
			int z = var2 + rand.nextInt(16) + 8;

			if (worldObj.getBlockId(x, y, z) == Block.stone.blockID)
			{
				(new WorldGenMinable(Block.oreEmerald.blockID, 6)).generate(worldObj, rand, x, y, z);
			}
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, rand, chunkX, chunkZ, false));

		BlockSand.fallInstantly = false;
	}

	@Override
	public boolean saveChunks(boolean par1, IProgressUpdate progress)
	{
		return true;
	}

	@Override
	public void func_104112_b()
	{
		//NOOP
	}

	@Override
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	@Override
	public boolean canSave()
	{
		return true;
	}

	@Override
	public String makeString()
	{
		return "CaveworldRandomLevelSource";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType creature, int x, int y, int z)
	{
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);

		return biome == null ? null : biome.getSpawnableList(creature);
	}

	@Override
	public ChunkPosition findClosestStructure(World world, String name, int x, int y, int z)
	{
		return null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int x, int z)
	{
		if (Config.generateMineshaft)
		{
			mineshaftGenerator.generate(this, worldObj, x, z, (byte[])null);
		}
	}
}