package caveworld.api;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ICaveVein
{
	public BlockMeta getBlockMeta();

	public IBlockState getBlockState();

	public void setBlockMeta(BlockMeta blockMeta);

	public int getBlockCount();

	public void setBlockCount(int count);

	public int getWeight();

	public void setWeight(int weight);

	public int getRate();

	public void setRate(int rate);

	public int getMinHeight();

	public void setMinHeight(int min);

	public int getMaxHeight();

	public void setMaxHeight(int max);

	public BlockMeta getTargetBlockMeta();

	public IBlockState getTargetBlockState();

	public void setTargetBlockMeta(BlockMeta target);

	public int[] getBiomes();

	public void setBiomes(int[] biomes);

	public void generateVeins(World world, Random random, BlockPos pos);
}