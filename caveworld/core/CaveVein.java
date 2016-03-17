package caveworld.core;

import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import caveworld.api.BlockMeta;
import caveworld.api.ICaveVein;
import caveworld.util.CaveUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CaveVein extends WeightedRandom.Item implements ICaveVein, Predicate<IBlockState>
{
	public BlockMeta blockMeta;
	private int blockCount;
	private int rate;
	private int minHeight;
	private int maxHeight;
	private BlockMeta targetBlockMeta;
	private int[] biomes;

	public CaveVein()
	{
		super(0);
	}

	public CaveVein(BlockMeta blockMeta, int blockCount, int weight, int rate, int min, int max)
	{
		super(weight);
		this.blockMeta = blockMeta;
		this.blockCount = blockCount;
		this.rate = rate;
		this.minHeight = min;
		this.maxHeight = max;
	}

	public CaveVein(BlockMeta blockMeta, int blockCount, int weight, int rate, int min, int max, BlockMeta target)
	{
		this(blockMeta, blockCount, weight, rate, min, max);
		this.targetBlockMeta = target;
	}

	public CaveVein(BlockMeta blockMeta, int blockCount, int weight, int rate, int min, int max, BlockMeta target, int[] biomes)
	{
		this(blockMeta, blockCount, weight, rate, min, max, target);
		this.biomes = biomes;
	}

	public CaveVein(BlockMeta blockMeta, int blockCount, int weight, int rate, int min, int max, BlockMeta target, Object... biomes)
	{
		this(blockMeta, blockCount, weight, rate, min, max, target);
		this.biomes = convertBiomes(biomes);
	}

	private int[] convertBiomes(Object... objects)
	{
		Set<Integer> biomes = Sets.newTreeSet();

		for (Object element : objects)
		{
			if (element instanceof BiomeGenBase)
			{
				biomes.add(((BiomeGenBase)element).biomeID);
			}
			else if (element instanceof Integer)
			{
				BiomeGenBase biome = BiomeGenBase.getBiome((Integer)element);

				if (biome != null)
				{
					biomes.add(biome.biomeID);
				}
			}
			else if (element instanceof Type)
			{
				Type type = (Type)element;

				for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
				{
					biomes.add(biome.biomeID);
				}
			}
		}

		return Ints.toArray(biomes);
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
	public int getBlockCount()
	{
		return MathHelper.clamp_int(blockCount, 1, 100);
	}

	@Override
	public void setBlockCount(int count)
	{
		blockCount = count;
	}

	@Override
	public int getWeight()
	{
		return MathHelper.clamp_int(itemWeight, 1, 100);
	}

	@Override
	public void setWeight(int weight)
	{
		itemWeight = weight;
	}

	@Override
	public int getRate()
	{
		return MathHelper.clamp_int(rate, 1, 100);
	}

	@Override
	public void setRate(int value)
	{
		rate = value;
	}

	@Override
	public int getMinHeight()
	{
		return MathHelper.clamp_int(minHeight, 0, 254);
	}

	@Override
	public void setMinHeight(int min)
	{
		minHeight = min;
	}

	@Override
	public int getMaxHeight()
	{
		return MathHelper.clamp_int(maxHeight, 1, 255);
	}

	@Override
	public void setMaxHeight(int max)
	{
		maxHeight = max;
	}

	@Override
	public BlockMeta getTargetBlockMeta()
	{
		return targetBlockMeta == null ? new BlockMeta(Blocks.stone, 0) : targetBlockMeta;
	}

	@Override
	public IBlockState getTargetBlockState()
	{
		return getTargetBlockMeta().getBlockState();
	}

	@Override
	public void setTargetBlockMeta(BlockMeta target)
	{
		targetBlockMeta = target;
	}

	@Override
	public int[] getBiomes()
	{
		return biomes == null ? new int[0] : biomes;
	}

	@Override
	public void setBiomes(int[] ids)
	{
		biomes = ids;
	}

	@Override
	public void generateVeins(World world, Random random, BlockPos pos)
	{
		int count = getBlockCount();
		int weight = getWeight();
		int rate = getRate();
		int min = getMinHeight();
		int max = getMaxHeight();
		int height = world.getActualHeight();

		if (pos.getY() < min || pos.getY() > max || min >= height || max >= height || min >= max)
		{
			return;
		}

		float f0 = random.nextFloat() * (float)Math.PI;
		double d0 = pos.getX() + 8 + MathHelper.sin(f0) * count / 8.0F;
		double d1 = pos.getX() + 8 - MathHelper.sin(f0) * count / 8.0F;
		double d2 = pos.getZ() + 8 + MathHelper.cos(f0) * count / 8.0F;
		double d3 = pos.getZ() + 8 - MathHelper.cos(f0) * count / 8.0F;
		double d4 = pos.getY() + random.nextInt(3) - 2;
		double d5 = pos.getY() + random.nextInt(3) - 2;

		outside: for (int i = 0; i < weight; ++i)
		{
			if (random.nextInt(100) + 1 > rate)
			{
				continue;
			}

			float f1 = (float)i / (float)count;
			double d6 = d0 + (d1 - d0) * f1;
			double d7 = d4 + (d5 - d4) * f1;
			double d8 = d2 + (d3 - d2) * f1;
			double d9 = random.nextDouble() * count / 16.0D;
			double d10 = (MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
			int j = MathHelper.floor_double(d6 - d10 / 2.0D);
			int k = MathHelper.floor_double(d7 - d11 / 2.0D);
			int l = MathHelper.floor_double(d8 - d10 / 2.0D);
			int m = MathHelper.floor_double(d6 + d10 / 2.0D);
			int n = MathHelper.floor_double(d7 + d11 / 2.0D);
			int o = MathHelper.floor_double(d8 + d10 / 2.0D);
			int gen = 0;

			for (int x = j; x <= m; ++x)
			{
				double xScale = (x + 0.5D - d6) / (d10 / 2.0D);

				if (xScale * xScale < 1.0D)
				{
					for (int y = k; y <= n; ++y)
					{
						double yScale = (y + 0.5D - d7) / (d11 / 2.0D);

						if (xScale * xScale + yScale * yScale < 1.0D)
						{
							for (int z = l; z <= o; ++z)
							{
								double zScale = (z + 0.5D - d8) / (d10 / 2.0D);

								if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
								{
									BlockPos blockpos = new BlockPos(x, y, z);

									if (world.getBlockState(blockpos).getBlock().isReplaceableOreGen(world, blockpos, this))
									{
										if (biomes == null || biomes.length <= 0 || ArrayUtils.contains(biomes, world.getBiomeGenForCoords(blockpos).biomeID))
										{
											if (world.setBlockState(blockpos, getBlockState(), 2) && ++gen >= count)
											{
												continue outside;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean apply(IBlockState state)
	{
		return CaveUtils.equalsBlockState(state, getTargetBlockState());
	}
}