/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import org.apache.logging.log4j.Level;

import caveworld.api.CaveworldAPI;
import caveworld.core.Caveworld;
import caveworld.network.client.AquaCavernAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import caveworld.util.CaveLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

public class WorldProviderAquaCavern extends WorldProviderCaveworld
{
	private static NBTTagCompound dimData;
	private static long dimensionSeed;
	private static int subsurfaceHeight;

	public static NBTTagCompound getDimData()
	{
		if (dimData == null)
		{
			dimData = readDimData();
		}

		return dimData;
	}

	public static File getDimDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, new WorldProviderAquaCavern().getSaveFolder());

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	private static NBTTagCompound readDimData()
	{
		NBTTagCompound data;
		File dir = getDimDir();

		if (dir == null)
		{
			data = null;
		}
		else
		{
			File file = new File(dir, "aquacavern.dat");

			if (!file.exists() || !file.isFile() || !file.canRead())
			{
				data = null;
			}
			else try (FileInputStream input = new FileInputStream(file))
			{
				data = CompressedStreamTools.readCompressed(input);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Aqua Cavern dimension data");

				data = null;
			}
		}

		return data == null ? new NBTTagCompound() : data;
	}

	private static void writeDimData()
	{
		File dir = getDimDir();

		if (dir == null)
		{
			return;
		}

		try (FileOutputStream output = new FileOutputStream(new File(dir, "aquacavern.dat")))
		{
			CompressedStreamTools.writeCompressed(getDimData(), output);
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Aqua Cavern dimension data");
		}
	}

	public static void loadDimData(NBTTagCompound data)
	{
		if (!data.hasKey("Seed"))
		{
			data.setLong("Seed", new SecureRandom().nextLong());
		}

		if (!data.hasKey("SubsurfaceHeight"))
		{
			data.setInteger("SubsurfaceHeight", ChunkProviderAquaCavern.subsurfaceHeight);
		}

		dimensionSeed = data.getLong("Seed");
		subsurfaceHeight = data.getInteger("SubsurfaceHeight");
	}

	public static void saveDimData()
	{
		if (dimData != null)
		{
			writeDimData();

			dimData = null;
		}
	}

	public WorldProviderAquaCavern()
	{
		this.dimensionId = CaveworldAPI.getAquaCavernDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj, CaveworldAPI.biomeAquaCavernManager);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderAquaCavern(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Aqua Cavern";
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			Caveworld.network.sendToAll(new AquaCavernAdjustMessage(dimensionId, getDimData()));
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			Caveworld.network.sendToAll(new AquaCavernAdjustMessage(dimensionId, getDimData()));
		}

		return subsurfaceHeight + 1;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--ambientTickCountdown <= 0)
			{
				ambientTickCountdown = worldObj.rand.nextInt(5000) + 10000;

				Caveworld.network.sendToDimension(new CaveMusicMessage("cavemusic.aqua"), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}