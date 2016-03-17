package caveworld.core;

import java.io.File;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.Level;

import caveworld.util.CaveConfiguration;
import caveworld.util.CaveLog;
import caveworld.util.CaveUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class Config
{
	public static Configuration generalCfg;
	public static Configuration dimensionsCfg;
	public static Configuration biomesCfg;
	public static Configuration veinsCfg;

	public static File getConfigDir()
	{
		return new File(Loader.instance().getConfigDir(), "caveworld");
	}

	public static File getConfigFile(String name)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, "caveworld-" + name + ".cfg");
	}

	public static Configuration loadConfig(String name)
	{
		File file = getConfigFile(name);
		Configuration config = new CaveConfiguration(file, true);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}

		return config;
	}

	public static File getConfigFile(String name, String category)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, name + "-" + category + ".cfg");
	}

	public static Configuration loadConfig(String name, String category)
	{
		File file = getConfigFile(name, category);
		Configuration config = new CaveConfiguration(file, true);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}

		return config;
	}

	public static void saveConfig(final Configuration config)
	{
		if (config.hasChanged())
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					config.save();
				}
			});
		}
	}

	public static void syncDimensionCfg()
	{

	}
}