/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.advancedtools;

import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class AdvancedToolsPlugin
{
	public static final String MODID = "AdvancedTools";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Item item = GameRegistry.findItem(MODID, "ugwoodpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugstonepickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugironpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugdiamondpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "uggoldpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}
	}
}