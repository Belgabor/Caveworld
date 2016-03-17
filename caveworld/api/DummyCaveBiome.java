package caveworld.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public final class DummyCaveBiome implements ICaveBiome
{
	private final BiomeGenBase biome;

	public DummyCaveBiome()
	{
		this(BiomeGenBase.plains);
	}

	public DummyCaveBiome(BiomeGenBase biome)
	{
		this.biome = biome;
	}

	@Override
	public BiomeGenBase getBiome()
	{
		return biome;
	}

	@Override
	public int getWeight()
	{
		return 0;
	}

	@Override
	public int setWeight(int weight)
	{
		return 0;
	}

	@Override
	public BlockMeta getBlockMeta()
	{
		return new BlockMeta(Blocks.stone, 0);
	}

	@Override
	public IBlockState getBlockState()
	{
		return getBlockMeta().getBlockState();
	}

	@Override
	public void setBlockMeta(BlockMeta blockMeta) {}

	@Override
	public BlockMeta getTopBlockMeta()
	{
		return getBlockMeta();
	}

	@Override
	public IBlockState getTopBlockState()
	{
		return getTopBlockMeta().getBlockState();
	}

	@Override
	public void setTopBlockMeta(BlockMeta blockMeta) {}
}