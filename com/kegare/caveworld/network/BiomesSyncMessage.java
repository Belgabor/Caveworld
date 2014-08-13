/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.network;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BiomesSyncMessage implements IMessage, IMessageHandler<BiomesSyncMessage, IMessage>
{
	private String data;

	public BiomesSyncMessage() {}

	public BiomesSyncMessage(Collection<ICaveBiome> biomes)
	{
		List<String> dat = Lists.newArrayList();
		List<String> list = Lists.newArrayList();

		for (ICaveBiome biome : biomes)
		{
			if (biome.getGenWeight() <= 0)
			{
				continue;
			}

			list.clear();
			list.add(Integer.toString(biome.getBiome().biomeID));
			list.add(Integer.toString(biome.getGenWeight()));
			list.add(Block.blockRegistry.getNameForObject(biome.getTerrainBlock().getBlock()));
			list.add(Integer.toString(biome.getTerrainBlock().getMetadata()));

			dat.add(Joiner.on(',').join(list));
		}

		this.data = Joiner.on('&').join(dat);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, data);
	}

	@Override
	public IMessage onMessage(BiomesSyncMessage message, MessageContext ctx)
	{
		CaveworldAPI.clearCaveBiomes();

		try
		{
			List<String> list;
			BiomeGenBase biome;
			int weight;
			int metadata;

			for (String entry : Splitter.on('&').splitToList(message.data))
			{
				list = Splitter.on(',').splitToList(entry);
				biome = BiomeGenBase.getBiome(NumberUtils.toInt(list.get(0), BiomeGenBase.plains.biomeID));
				weight = NumberUtils.toInt(list.get(1));
				metadata = NumberUtils.toInt(list.get(3));

				if (biome == null)
				{
					continue;
				}

				CaveworldAPI.addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(list.get(2), metadata)));
			}

			CaveLog.info("Loaded %d cave biomes from server", CaveworldAPI.getActiveBiomeCount());
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave biomes from server");
		}

		return null;
	}
}