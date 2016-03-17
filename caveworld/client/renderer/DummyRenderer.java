package caveworld.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DummyRenderer extends IRenderHandler
{
	public static final DummyRenderer instance = new DummyRenderer();

	@Override
	public void render(float ticks, WorldClient world, Minecraft mc) {}
}