package caveworld.core;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import caveworld.api.BlockMeta;
import caveworld.api.ICaveVein;
import caveworld.api.ICaveVeinManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameData;

public class CaveVeinManager implements ICaveVeinManager
{
	private final List<ICaveVein> CAVE_VEINS = Lists.newArrayList();

	@Override
	public Configuration getConfig()
	{
		return Config.veinsCfg;
	}

	@Override
	public int getType()
	{
		return 0;
	}

	@Override
	public boolean addCaveVein(ICaveVein vein)
	{
		String block = vein == null ? null : vein.getBlockMeta().getBlockName();
		String blockMeta = vein == null ? null : vein.getBlockMeta().getMetaName();
		int count = vein == null ? -1 : vein.getBlockCount();
		int weight = vein == null ? -1 : vein.getWeight();
		int rate = vein == null ? -1 : vein.getRate();
		int min = vein == null ? -1 : vein.getMinHeight();
		int max = vein == null ? -1 : vein.getMaxHeight();
		String target = vein == null ? null : vein.getTargetBlockMeta().getBlockName();
		String targetMeta = vein == null ? null : vein.getTargetBlockMeta().getMetaName();
		int[] biomes = vein == null ? null : vein.getBiomes();

		String name = Integer.toString(getCaveVeins().size());

		if (vein == null && !getConfig().hasCategory(name))
		{
			return false;
		}

		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = getConfig().get(name, "block", GameData.getBlockRegistry().getNameForObject(Blocks.stone).toString());
		prop.setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(block)) prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		if (!GameData.getBlockRegistry().containsKey(new ResourceLocation(block))) return false;

		prop = getConfig().get(name, "blockMeta", Integer.toString(0));
		prop.setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(blockMeta)) prop.set(blockMeta);
		propOrder.add(prop.getName());
		blockMeta = prop.getString();

		prop = getConfig().get(name, "blockCount", 1);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (count >= 0) prop.set(MathHelper.clamp_int(count, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "weight", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (weight >= 0) prop.set(MathHelper.clamp_int(weight, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "rate", 100);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (rate >= 0) prop.set(MathHelper.clamp_int(rate, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		rate = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "minHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (min >= 0) prop.set(MathHelper.clamp_int(min, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "maxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (max >= 0) prop.set(MathHelper.clamp_int(max, min + 1, Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), min + 1, Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "targetBlock", GameData.getBlockRegistry().getNameForObject(Blocks.stone).toString());
		prop.setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(target)) prop.set(target);
		if (!GameData.getBlockRegistry().containsKey(new ResourceLocation(prop.getString()))) prop.setToDefault();
		propOrder.add(prop.getName());
		target = prop.getString();

		prop = getConfig().get(name, "targetMeta", Integer.toString(0));
		prop.setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(targetMeta)) prop.set(targetMeta);
		propOrder.add(prop.getName());
		targetMeta = prop.getString();

		prop = getConfig().get(name, "biomes", new int[0]);
		prop.setLanguageKey("caveworld." + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (biomes != null) prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		getConfig().setCategoryPropertyOrder(name, propOrder);

		if (vein == null)
		{
			vein = new CaveVein(new BlockMeta(block, blockMeta), count, weight, rate, min, max, new BlockMeta(target, targetMeta), biomes);
		}

		return getCaveVeins().add(vein);
	}

	@Override
	public int removeCaveVeins(ICaveVein vein)
	{
		int prev = getCaveVeins().size();

		for (int i = getCaveVeins().indexOf(vein); i >= 0;)
		{
			getCaveVeins().remove(i);

			getConfig().removeCategory(getConfig().getCategory(Integer.toString(i)));
		}

		return Math.max(getCaveVeins().size(), prev);
	}

	@Override
	public int removeCaveVeins(BlockMeta blockMeta)
	{
		ICaveVein vein;
		int prev = getCaveVeins().size();

		for (int i = 0; i < prev; ++i)
		{
			vein = getCaveVeins().get(i);

			if (vein.getBlockMeta().equals(blockMeta))
			{
				getCaveVeins().remove(i);

				getConfig().removeCategory(getConfig().getCategory(Integer.toString(i)));
			}
		}

		return Math.max(getCaveVeins().size(), prev);
	}

	@Override
	public List<ICaveVein> getCaveVeins()
	{
		return CAVE_VEINS;
	}

	@Override
	public void clearCaveVeins()
	{
		getCaveVeins().clear();
	}
}