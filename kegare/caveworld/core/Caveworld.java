package kegare.caveworld.core;

import kegare.caveworld.handler.CaveConnectionHandler;
import kegare.caveworld.handler.CaveEventHooks;
import kegare.caveworld.util.Version;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod
(
	modid = "kegare.caveworld"
)
@NetworkMod
(
	clientSideRequired = true,
	serverSideRequired = false,
	connectionHandler = CaveConnectionHandler.class
)
public class Caveworld
{
	@Instance("kegare.caveworld")
	public static Caveworld instance;

	@Metadata("kegare.caveworld")
	public static ModMetadata metadata;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Version.versionCheck();
		Config.buildConfiguration();

		CaveBlock.load();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && Config.versionCheck && Version.isOutdated())
		{
			event.getServer().logInfo("A new Caveworld version is available : " + Version.LATEST);
		}
	}
}