package caveworld.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.BiomeGenBase;

public interface ICaveBiome
{
	public BiomeGenBase getBiome();

	public int getWeight();

	public int setWeight(int weight);

	public BlockMeta getBlockMeta();

	public IBlockState getBlockState();

	public void setBlockMeta(BlockMeta blockMeta);

	public BlockMeta getTopBlockMeta();

	public IBlockState getTopBlockState();

	public void setTopBlockMeta(BlockMeta blockMeta);
}