package caveworld.core;

import caveworld.api.BlockMeta;
import caveworld.api.ICaveBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;

public class CaveBiome extends WeightedRandom.Item implements ICaveBiome
{
	private BiomeGenBase biome;
	private BlockMeta blockMeta;
	private BlockMeta topBlockMeta;

	public CaveBiome()
	{
		super(0);
	}

	public CaveBiome(BiomeGenBase biome, int weight)
	{
		this(biome, weight, new BlockMeta(Blocks.stone, 0), null);
	}

	public CaveBiome(BiomeGenBase biome, int weight, BlockMeta filler, BlockMeta top)
	{
		super(weight);
		this.biome = biome;
		this.blockMeta = filler;
		this.topBlockMeta = top;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof ICaveBiome))
		{
			return false;
		}

		ICaveBiome biome = (ICaveBiome)obj;

		return getBiome().biomeID == biome.getBiome().biomeID;
	}

	@Override
	public int hashCode()
	{
		return getBiome().biomeID;
	}

	@Override
	public BiomeGenBase getBiome()
	{
		return biome == null ? BiomeGenBase.plains : biome;
	}

	@Override
	public int getWeight()
	{
		return itemWeight;
	}

	@Override
	public int setWeight(int weight)
	{
		return itemWeight = weight;
	}

	@Override
	public BlockMeta getBlockMeta()
	{
		return blockMeta == null ? new BlockMeta(Blocks.stone, 0) : blockMeta;
	}

	@Override
	public IBlockState getBlockState()
	{
		return getBlockMeta().getBlockState();
	}

	@Override
	public void setBlockMeta(BlockMeta block)
	{
		blockMeta = block;
	}

	@Override
	public BlockMeta getTopBlockMeta()
	{
		return topBlockMeta == null ? getBlockMeta() : topBlockMeta;
	}

	@Override
	public IBlockState getTopBlockState()
	{
		return getTopBlockMeta().getBlockState();
	}

	@Override
	public void setTopBlockMeta(BlockMeta block)
	{
		topBlockMeta = block;
	}
}