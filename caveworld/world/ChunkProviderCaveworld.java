package caveworld.world;

import java.util.List;
import java.util.Random;

import com.google.common.base.Strings;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveVein;
import caveworld.world.gen.MapGenCavesCaveworld;
import caveworld.world.gen.MapGenRavineCaveworld;
import caveworld.world.gen.MapGenUnderCaves;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveworld implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateUnderCaves;
	public static boolean generateExtremeCaves;
	public static boolean generateExtremeRavine;
	public static boolean generateMineshaft;
	public static boolean generateStronghold;
	public static boolean generateLakes;
	public static boolean generateDungeons;
	public static boolean generateAnimalDungeons;
	public static boolean decorateVines;
	public static String[] spawnerMobs;

	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenCavesCaveworld();
	private final MapGenBase ravineGenerator = new MapGenRavineCaveworld();
	private final MapGenBase underCaveGenerator = new MapGenUnderCaves();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private final MapGenStronghold strongholdGenerator = new MapGenStronghold();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator glowStoneGen = new WorldGenGlowStone1();
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private final WorldGenerator vinesGen = new WorldGenVines();

	{
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
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		ChunkPrimer data = new ChunkPrimer();
		biomesForGeneration = worldObj.getWorldChunkManager().getBiomeGenAt(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16, false);
		int worldHeight = worldObj.provider.getActualHeight();

		if (generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, data);
		}

		if (generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, data);
		}

		if (generateUnderCaves)
		{
			underCaveGenerator.generate(this, worldObj, chunkX, chunkZ, data);
		}

		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, data);
			}

			if (generateStronghold)
			{
				strongholdGenerator.generate(this, worldObj, chunkX, chunkZ, data);
			}
		}

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				data.setBlockState(x, 0, z, Blocks.bedrock.getDefaultState());
				data.setBlockState(x, worldHeight - 1, z, Blocks.bedrock.getDefaultState());
				data.setBlockState(x, worldHeight - 2, z, Blocks.stone.getDefaultState());

				if (worldHeight < 256)
				{
					for (int y = 255; y >= worldHeight; --y)
					{
						data.setBlockState(x, y, z, Blocks.air.getDefaultState());
					}
				}
			}
		}

		Chunk chunk = new Chunk(worldObj, data, chunkX, chunkZ);
		byte[] biomes = chunk.getBiomeArray();

		for (int index = 0; index < biomes.length; ++index)
		{
			biomes[index] = (byte)biomesForGeneration[index].biomeID;
		}

		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public Chunk provideChunk(BlockPos pos)
	{
		return provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BlockPos pos = new BlockPos(worldX, 0, worldZ);
		ChunkCoordIntPair coord = new ChunkCoordIntPair(chunkX, chunkZ);
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(pos.add(16, 0, 16));
		BiomeDecorator decorator = biome.theBiomeDecorator;
		int worldHeight = worldObj.provider.getActualHeight();
		random.setSeed(worldObj.getSeed());
		long xSeed = random.nextLong() / 2L * 2L + 1L;
		long zSeed = random.nextLong() / 2L * 2L + 1L;
		random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ worldObj.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.generateStructure(worldObj, random, coord);
			}

			if (generateStronghold)
			{
				strongholdGenerator.generateStructure(worldObj, random, coord);
			}
		}

		int i, x, y, z;

		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
		{
			if (generateLakes && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16);
				z = random.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, random, pos.add(x, y, z));
			}

			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.GLOWSTONE))
			{
				x = random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 10) + 10;
				z = random.nextInt(16) + 8;

				glowStoneGen.generate(worldObj, random, pos.add(x, y, z));
			}
		}
		else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
		{
			if (generateLakes)
			{
				if (!BiomeDictionary.isBiomeOfType(biome, Type.SANDY) && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 16);
					z = random.nextInt(16) + 8;

					lakeWaterGen.generate(worldObj, random, pos.add(x, y, z));
				}

				if (random.nextInt(20) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(worldHeight / 2);
					z = random.nextInt(16) + 8;

					lakeLavaGen.generate(worldObj, random, pos.add(x, y, z));
				}
			}

//			if (generateDungeons && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
//			{
//				for (i = 0; i < 12; ++i)
//				{
//					x = random.nextInt(16) + 8;
//					y = random.nextInt(worldHeight - 24);
//					z = random.nextInt(16) + 8;
//
//					dungeonGen.generate(worldObj, random, pos.add(x, y, z));
//				}
//			}
//
//			if (generateAnimalDungeons && random.nextInt(5) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
//			{
//				x = random.nextInt(16) + 8;
//				y = random.nextInt(worldHeight - 24);
//				z = random.nextInt(16) + 8;
//
//				animalDungeonGen.generate(worldObj, random, pos.add(x, y, z));
//			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, pos));

		for (ICaveVein vein : CaveworldAPI.veinManager.getCaveVeins())
		{
			vein.generateVeins(worldObj, random, pos);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, pos));
		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, random, pos));

		if (TerrainGen.decorate(worldObj, random, pos, Decorate.EventType.SHROOM))
		{
			i = 0;

			if (BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM))
			{
				i += 2;
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				i += 1;
			}

			if (random.nextInt(3) <= i)
			{
				x = random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = random.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, random, pos.add(x, y, z));
			}

			if (random.nextInt(8) <= i)
			{
				x = random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = random.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, random, pos.add(x, y, z));
			}
		}

		if (decorator.generateLakes)
		{
			if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				for (i = 0; i < 40; ++i)
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 12) + 10;
					z = random.nextInt(16) + 8;

					liquidLavaGen.generate(worldObj, random, pos.add(x, y, z));
				}
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.WATER))
			{
				for (i = 0; i < 65; ++i)
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(random.nextInt(worldHeight - 16) + 10);
					z = random.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, random, pos.add(x, y, z));
				}
			}
			else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
			{
				for (i = 0; i < 50; ++i)
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(random.nextInt(worldHeight - 16) + 10);
					z = random.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, random, pos.add(x, y, z));
				}

				for (i = 0; i < 20; ++i)
				{
					x = random.nextInt(16) + 8;
					y = random.nextInt(worldHeight / 2);
					z = random.nextInt(16) + 8;

					liquidLavaGen.generate(worldObj, random, pos.add(x, y, z));
				}
			}
		}

		if (decorateVines && (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || BiomeDictionary.isBiomeOfType(biome, Type.MOUNTAIN)) && random.nextInt(6) == 0)
		{
			for (i = 0; i < 50; ++i)
			{
				x = random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 40) + 40;
				z = random.nextInt(16) + 8;

				vinesGen.generate(worldObj, random, pos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, random, pos));
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		BlockFalling.fallInstantly = false;
	}

	@Override
	public boolean func_177460_a(IChunkProvider provider, Chunk chunk, int chunkX, int chunkZ)
	{
		return false;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate progress)
	{
		return true;
	}

	@Override
	public void saveExtraData() {}

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
	public List getPossibleCreatures(EnumCreatureType type, BlockPos pos)
	{
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(pos);

		return biome == null ? null : biome.getSpawnableList(type);
	}

	@Override
	public BlockPos getStrongholdGen(World world, String name, BlockPos pos)
	{
		if (Strings.isNullOrEmpty(name))
		{
			return null;
		}

		switch (name)
		{
			case "Mineshaft":
				return mineshaftGenerator.getClosestStrongholdPos(world, pos);
			case "Stronghold":
				return strongholdGenerator.getClosestStrongholdPos(world, pos);
			default:
				return null;
		}
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(Chunk chunk, int chunkX, int chunkZ)
	{
		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, null);
			}

			if (generateStronghold)
			{
				strongholdGenerator.generate(this, worldObj, chunkX, chunkZ, null);
			}
		}
	}
}