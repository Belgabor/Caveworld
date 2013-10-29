package kegare.caveworld.proxy;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderers() {}

	public void addEffect(EntityFX entityFX) {}

	public MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public void addChatMessage(String message)
	{
		getServer().logInfo(StringUtils.stripControlCodes(message));
	}
}