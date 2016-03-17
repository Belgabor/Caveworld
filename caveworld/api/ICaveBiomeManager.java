package caveworld.api;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public interface ICaveBiomeManager
{
	public Configuration getConfig();

	public int getType();

	public boolean addBiome(ICaveBiome biome);

	public boolean removeBiome(BiomeGenBase biome);

	public int getBiomeCount();

	public ICaveBiome getBiome(BiomeGenBase biome);

	public ICaveBiome getRandomBiome(Random random);

	public Map<BiomeGenBase, ICaveBiome> getCaveBiomes();

	public Set<ICaveBiome> getBiomes();

	public List<BiomeGenBase> getBiomeList();

	public void clearBiomes();
}