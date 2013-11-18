package kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kegare.caveworld.core.Caveworld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
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
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
		mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
	}

	public ChunkProviderCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.generateStructures = world.getWorldInfo().isMapFeaturesEnabled();
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blocks = new byte[65536];
		Arrays.fill(blocks, (byte)Block.stone.blockID);

		if (Caveworld.generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Caveworld.generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Caveworld.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		Chunk chunk = new Chunk(worldObj, blocks, chunkX, chunkZ);
		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public Chunk loadChunk(int chunkX, int chunkZ)
	{
		return provideChunk(chunkX, chunkZ);
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ)
	{
		BlockSand.fallInstantly = true;

		int chunk_X = chunkX * 16;
		int chunk_Z = chunkZ * 16;
		Chunk chunk = worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(chunk_X + 16, chunk_Z + 16);
		BiomeDecorator decorator = biome.createBiomeDecorator();
		long worldSeed = worldObj.getSeed();
		random.setSeed(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ worldSeed);

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		if (Caveworld.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
		}

		int x;
		int y;
		int z;

		if (Caveworld.generateLakes)
		{
			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE) && random.nextInt(4) == 0)
			{
				x = chunk_X + random.nextInt(16) + 8;
				y = random.nextInt(224);
				z = chunk_Z + random.nextInt(16) + 8;

				(new WorldGenLakes(Block.waterStill.blockID)).generate(worldObj, random, x, y, z);
			}

			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA) && random.nextInt(8) == 0)
			{
				x = chunk_X + random.nextInt(16) + 8;
				y = random.nextInt(random.nextInt(120) + 8);
				z = chunk_Z + random.nextInt(16) + 8;

				if (y < 63 || random.nextInt(10) == 0)
				{
					(new WorldGenLakes(Block.lavaStill.blockID)).generate(worldObj, random, x, y, z);
				}
			}
		}

		int i;

		if (Caveworld.generateDungeon && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (i = 0; i < 8; ++i)
			{
				x = chunk_X + random.nextInt(16) + 8;
				y = random.nextInt(224);
				z = chunk_Z + random.nextInt(16) + 8;

				(new WorldGenDungeons()).generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.dirtGen, chunkX, chunkZ, GenerateMinable.EventType.DIRT))
		{
			for (i = 0; i < 20; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.dirtGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.gravelGen, chunkX, chunkZ, GenerateMinable.EventType.GRAVEL))
		{
			for (i = 0; i < 10; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.gravelGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.decorate(worldObj, random, chunkX, chunkZ, Decorate.EventType.SHROOM))
		{
			if (random.nextInt(4) == 0)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.mushroomBrownGen.generate(worldObj, random, x, y, z);
			}

			if (random.nextInt(8) == 0)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.mushroomRedGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.decorate(worldObj, random, chunkX, chunkZ, Decorate.EventType.REED))
		{
			for (i = 0; i < decorator.reedsPerChunk; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.reedGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.coalGen, chunkX, chunkZ, GenerateMinable.EventType.COAL))
		{
			for (i = 0; i < 25; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.coalGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.ironGen, chunkX, chunkZ, GenerateMinable.EventType.IRON))
		{
			for (i = 0; i < 25; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.ironGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.goldGen, chunkX, chunkZ, GenerateMinable.EventType.GOLD))
		{
			for (i = 0; i < 6; ++i)
			{
				x = chunk_X + random.nextInt(16);
				y = random.nextInt(255 - 128) + 128;
				z = chunk_Z + random.nextInt(16);

				decorator.goldGen.generate(worldObj, random, x, y, z);
			}
		}

		for (i = 0; i < 6; ++i)
		{
			x = chunk_X + random.nextInt(16);
			y = random.nextInt(255 - 128) + 128;
			z = chunk_Z + random.nextInt(16);

			(new WorldGenMinable(Block.oreEmerald.blockID, 8)).generate(worldObj, random, x, y, z);
		}

		biome.decorate(worldObj, random, chunk_X, chunk_Z);

		for (x = 0; x < 16; ++x)
		{
			for (z = 0; z < 16; ++z)
			{
				chunk.setBlockIDWithMetadata(x, 0, z, Block.bedrock.blockID, 0);
				chunk.setBlockIDWithMetadata(x, 255, z, Block.bedrock.blockID, 0);
			}
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		BlockSand.fallInstantly = false;
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate progress)
	{
		return true;
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
		return "Mineshaft".equals(name) ? mineshaftGenerator.getNearestInstance(world, x, y, z) : null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ)
	{
		if (Caveworld.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, (byte[])null);
		}
	}

	@Override
	public void saveExtraData() {}
}