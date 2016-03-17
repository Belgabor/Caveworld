package caveworld.core;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.DummyCaveBiome;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveBiomeManager;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public class CaveBiomeManager implements ICaveBiomeManager
{
	private final Map<BiomeGenBase, ICaveBiome> CAVE_BIOMES = Maps.newHashMap();

	@Override
	public Configuration getConfig()
	{
		return Config.biomesCfg;
	}

	@Override
	public int getType()
	{
		return 0;
	}

	@Override
	public boolean addBiome(ICaveBiome biome)
	{
		for (ICaveBiome entry : getCaveBiomes().values())
		{
			if (entry.getBiome().biomeID == biome.getBiome().biomeID)
			{
				entry.setWeight(entry.getWeight() + biome.getWeight());

				return false;
			}
		}

		getCaveBiomes().put(biome.getBiome(), biome);

		return true;
	}

	@Override
	public boolean removeBiome(BiomeGenBase biome)
	{
		return getCaveBiomes().remove(biome) != null;
	}

	@Override
	public int getBiomeCount()
	{
		int i = 0;

		for (ICaveBiome entry : getCaveBiomes().values())
		{
			if (entry.getWeight() > 0)
			{
				++i;
			}
		}

		return i;
	}

	@Override
	public ICaveBiome getBiome(BiomeGenBase biome)
	{
		ICaveBiome ret = getCaveBiomes().get(biome);

		return ret == null ? new DummyCaveBiome(biome) : ret;
	}

	@Override
	public ICaveBiome getRandomBiome(Random random)
	{
		return new DummyCaveBiome();
	}

	@Override
	public Map<BiomeGenBase, ICaveBiome> getCaveBiomes()
	{
		return CAVE_BIOMES;
	}

	@Override
	public Set<ICaveBiome> getBiomes()
	{
		return Sets.newHashSet(getCaveBiomes().values());
	}

	@Override
	public List<BiomeGenBase> getBiomeList()
	{
		return Lists.newArrayList(getCaveBiomes().keySet());
	}

	@Override
	public void clearBiomes()
	{
		getCaveBiomes().clear();
	}
}