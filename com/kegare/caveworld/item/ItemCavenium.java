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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCavenium extends Item
{
	@SideOnly(Side.CLIENT)
	protected IIcon refinedIcon;

	public ItemCavenium(String name)
	{
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:cavenium");
		this.setCreativeTab(Caveworld.tabCaveworld);
		this.setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon(getIconString());
		refinedIcon = iconRegister.registerIcon(getIconString() + ".refined");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		switch (damage)
		{
			case 1:
				return refinedIcon;
			default:
				return itemIcon;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 1:
				return getUnlocalizedName() + ".refined";
			default:
				return getUnlocalizedName();
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		return itemstack.getItemDamage() == 1 ? EnumRarity.rare : super.getRarity(itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i <= 1; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}