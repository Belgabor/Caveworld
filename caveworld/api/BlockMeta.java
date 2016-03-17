package caveworld.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public class BlockMeta
{
	private Block block;
	private int meta;

	public BlockMeta()
	{
		this.block = null;
		this.meta = -1;
	}

	public BlockMeta(Block block, int meta)
	{
		this.block = block;
		this.meta = meta;
	}

	public BlockMeta(IBlockState state)
	{
		this(state.getBlock(), state.getBlock().getMetaFromState(state));
	}

	public BlockMeta(String name, int meta)
	{
		this(GameData.getBlockRegistry().getObject(new ResourceLocation(name)), meta);
	}

	public BlockMeta(String name, String meta)
	{
		this(name, -1);
		this.meta = BlockMeta.getMetaFromString(block, meta);
	}

	public Block getBlock()
	{
		return block;
	}

	public int getMeta()
	{
		return meta;
	}

	public IBlockState getBlockState()
	{
		return block.getStateFromMeta(meta);
	}

	/**
	 * @return The block registry name
	 */
	public String getBlockName()
	{
		return GameData.getBlockRegistry().getNameForObject(block).toString();
	}

	public String getMetaName()
	{
		return BlockMeta.getMetaName(block, meta);
	}

	@Override
	public String toString()
	{
		if (block == null)
		{
			return "null";
		}

		String name = block.getUnlocalizedName();

		if (meta >= 0)
		{
			return name + ",meta=" + meta;
		}

		return name + ",meta=all";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj == null || !(obj instanceof BlockMeta))
		{
			return false;
		}

		BlockMeta blockMeta = (BlockMeta)obj;

		if (block != blockMeta.block)
		{
			return false;
		}

		if (meta < 0 || blockMeta.meta < 0)
		{
			return true;
		}

		return meta == blockMeta.meta;
	}

	public static final Pattern numberPattern = Pattern.compile("^[0-9]+$");

	public static int getMetaFromString(Block block, String str)
	{
		if (block == null || Strings.isNullOrEmpty(str) || str.equalsIgnoreCase("all") || str.equalsIgnoreCase("null"))
		{
			return -1;
		}

		str = str.trim();

		if (numberPattern.matcher(str).matches())
		{
			try
			{
				return Integer.parseInt(str, 10);
			}
			catch (Exception e) {}
		}

		Class<?> clazz = null;

		for (Field field : block.getClass().getDeclaredFields())
		{
			if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
			{
				if (field.getType() == PropertyEnum.class)
				{
					try
					{
						clazz = ((PropertyEnum)field.get(null)).getValueClass();
					}
					catch (Exception e) {}
				}
			}
		}

		if (clazz == null)
		{
			return -1;
		}

		for (Object obj : clazz.getEnumConstants())
		{
			if (obj instanceof IStringSerializable)
			{
				String name = ((IStringSerializable)obj).getName();

				if (str.equalsIgnoreCase(name))
				{
					for (Method method : obj.getClass().getDeclaredMethods())
					{
						if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
						{
							try
							{
								return ((Integer)method.invoke(obj, new Object[0])).intValue();
							}
							catch (Exception e) {}
						}
					}
				}
			}
		}

		return -1;
	}

	public static String getMetaName(Block block, int meta)
	{
		if (block == null)
		{
			return null;
		}

		if (meta < 0)
		{
			return "all";
		}

		Class<?> clazz = null;

		for (Field field : block.getClass().getDeclaredFields())
		{
			if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
			{
				if (field.getType() == PropertyEnum.class)
				{
					try
					{
						clazz = ((PropertyEnum)field.get(null)).getValueClass();
					}
					catch (Exception e) {}
				}
			}
		}

		if (clazz == null)
		{
			return "null";
		}

		for (Object obj : clazz.getEnumConstants())
		{
			if (obj instanceof IStringSerializable)
			{
				String name = ((IStringSerializable)obj).getName();

				for (Method method : obj.getClass().getDeclaredMethods())
				{
					if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
					{
						try
						{
							if (((Integer)method.invoke(obj, new Object[0])).intValue() == meta)
							{
								return name;
							}
						}
						catch (Exception e) {}
					}
				}
			}
		}

		return "null";
	}
}