package com.kegare.caveworld.world.gen;

import java.security.SecureRandom;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineCaveworld extends MapGenRavine
{
	private final float[] field_75046_d = new float[1024];

	@Override
	protected void func_151540_a(long ravineSeed, int chunkX, int chunkZ, Block[] blocks, double blockX, double blockY, double blockZ, float scale, float leftRightRadian, float upDownRadian, int currentY, int targetY, double scaleHeight)
	{
		int worldHeight = blocks.length / 256;
		Random random = new SecureRandom();
		random.setSeed(ravineSeed);

		double centerX = (chunkX << 4) + 8;
		double centerZ = (chunkZ << 4) + 8;
		float leftRightChange = 0.0F;
		float upDownChange = 0.0F;

		if (targetY <= 0)
		{
			int blockRangeY = range * 16 - 16;
			targetY = blockRangeY - random.nextInt(blockRangeY / 4);
		}

		boolean createFinalRoom = false;

		if (currentY == -1)
		{
			currentY = targetY / 2;
			createFinalRoom = true;
		}

		float nextInterHeight = 1.0F;

		for (int y = 0; y < worldHeight; ++y)
		{
			if (y == 0 || random.nextInt(3) == 0)
			{
				nextInterHeight = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
			}

			field_75046_d[y] = nextInterHeight * nextInterHeight;
		}

		for (; currentY < targetY; ++currentY)
		{
			double roomWidth = 1.5D + MathHelper.sin(currentY * (float)Math.PI / targetY) * scale * 1.0F;
			double roomHeight = roomWidth * scaleHeight;
			roomWidth *= random.nextFloat() * 0.25D + 0.75D;
			roomHeight *= random.nextFloat() * 0.25D + 0.75D;
			float moveHorizontal = MathHelper.cos(upDownRadian);
			float moveVertical = MathHelper.sin(upDownRadian);
			blockX += MathHelper.cos(leftRightRadian) * moveHorizontal;
			blockY += moveVertical;
			blockZ += MathHelper.sin(leftRightRadian) * moveHorizontal;
			upDownRadian *= 0.7F;
			upDownRadian += upDownChange * 0.05F;
			leftRightRadian += leftRightChange * 0.05F;
			upDownChange *= 0.8F;
			leftRightChange *= 0.5F;
			upDownChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			leftRightChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

			if (createFinalRoom || random.nextInt(4) != 0)
			{
				double distanceX = blockX - centerX;
				double distanceZ = blockZ - centerZ;
				double distanceY = targetY - currentY;
				double maxDistance = scale + 2.0F + 16.0F;

				if (distanceX * distanceX + distanceZ * distanceZ - distanceY * distanceY > maxDistance * maxDistance)
				{
					return;
				}

				if (blockX >= centerX - 16.0D - roomWidth * 2.0D && blockZ >= centerZ - 16.0D - roomWidth * 2.0D && blockX <= centerX + 16.0D + roomWidth * 2.0D && blockZ <= centerZ + 16.0D + roomWidth * 2.0D)
				{
					int xLow = Math.max(MathHelper.floor_double(blockX - roomWidth) - (chunkX << 4) - 1, 0);
					int xHigh = Math.min(MathHelper.floor_double(blockX + roomWidth) - (chunkX << 4) + 1, 16);
					int yLow = Math.max(MathHelper.floor_double(blockY - roomHeight) - 1, 1);
					int yHigh = Math.min(MathHelper.floor_double(blockY + roomHeight) + 1, worldHeight - 4);
					int zLow = Math.max(MathHelper.floor_double(blockZ - roomWidth) - (chunkZ << 4) - 1, 0);
					int zHigh = Math.min(MathHelper.floor_double(blockZ + roomWidth) - (chunkZ << 4) + 1, 16);

					for (int x = xLow; x < xHigh; ++x)
					{
						double xScale = (x + (chunkX << 4) + 0.5D - blockX) / roomWidth;

						for (int z = zLow; z < zHigh; ++z)
						{
							double zScale = (z + (chunkZ << 4) + 0.5D - blockZ) / roomWidth;
							int index = (x * 16 + z) * worldHeight + yHigh;

							if (xScale * xScale + zScale * zScale < 1.0D)
							{
								for (int y = yHigh - 1; y >= yLow; --y)
								{
									double yScale = (y + 0.5D - blockY) / roomHeight;

									if ((xScale * xScale + zScale * zScale) * field_75046_d[y] + yScale * yScale / 6.0D < 1.0D)
									{
										if (y >= 10)
										{
											blocks[index] = null;
										}
										else
										{
											blocks[index] = Blocks.lava;
										}
									}

									--index;
								}
							}
						}
					}

					if (createFinalRoom)
					{
						break;
					}
				}
			}
		}
	}

	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		if (rand.nextInt(50) == 0)
		{
			int worldHeight = world.provider.getActualHeight();
			double blockX = (x << 4) + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(40) + 8) + 20;
			double blockZ = (z << 4) + rand.nextInt(16);
			float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
			float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
			float scale = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;

			func_151540_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 3.0D);
		}
	}
}