package caveworld.util;

import java.util.concurrent.ForkJoinPool;

import net.minecraft.block.state.IBlockState;

public class CaveUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

	public static int getStateMeta(IBlockState state)
	{
		return state.getBlock().getMetaFromState(state);
	}

	public static boolean equalsBlockState(IBlockState block, IBlockState state)
	{
		if (block == null || state == null)
		{
			return false;
		}

		return block.getBlock() == state.getBlock() && getStateMeta(block) == getStateMeta(state);
	}

	public static int compareWithNull(Object o1, Object o2)
	{
		return (o1 == null ? 1 : 0) - (o2 == null ? 1 : 0);
	}
}