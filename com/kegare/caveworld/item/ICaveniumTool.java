/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.kegare.caveworld.api.BlockEntry;

public interface ICaveniumTool
{
	public String getToolClass();

	public List<BlockEntry> getBreakableBlocks();

	public long getHighlightStart();

	public void setHighlightStart(long time);

	public int getRefined(ItemStack itemstack);

	public boolean canBreak(ItemStack itemstack, Block block, int metadata);

	public Item getBase(ItemStack itemstack);

	public IBreakMode getMode(ItemStack itemstack);

	public String getModeName(ItemStack itemstack);

	public String getModeDisplayName(ItemStack itemstack);

	public String getModeInfomation(ItemStack itemstack);

	public Set<Item> getBaseableItems();
}