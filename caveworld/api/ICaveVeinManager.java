package caveworld.api;

import java.util.List;

import net.minecraftforge.common.config.Configuration;

public interface ICaveVeinManager
{
	public Configuration getConfig();

	public int getType();

	public boolean addCaveVein(ICaveVein vein);

	public int removeCaveVeins(ICaveVein vein);

	public int removeCaveVeins(BlockMeta blockMeta);

	public List<ICaveVein> getCaveVeins();

	public void clearCaveVeins();
}