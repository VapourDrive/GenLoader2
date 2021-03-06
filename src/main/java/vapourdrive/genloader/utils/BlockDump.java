package vapourdrive.genloader.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameData;

import org.apache.logging.log4j.Level;

import vapourdrive.genloader.api.GenLoaderAPI;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vapourdrive.genloader.utils.json.GsonHelper;

public class BlockDump
{
	public static HashMap<String, ArrayList<BlockInfo>> ModLists = new HashMap<String, ArrayList<BlockInfo>>();

	public static void init(File configPath)
	{
		for (Block block : ForgeRegistries.BLOCKS.getValues())
		{
			ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
			String mod = location.getResourceDomain();
			String name = location.getResourcePath();
			ImmutableList<IBlockState> states = block.getBlockState().getValidStates();
			ArrayList<String> statesStrings = new ArrayList<String>();
			for (IBlockState tempState : states) {
				statesStrings.add(tempState.toString());
			}
			if (ModLists.containsKey(mod))
			{
				ModLists.get(mod).add(new BlockInfo(mod, name, statesStrings));
			}
			else
			{
				ArrayList<BlockInfo> init = new ArrayList<BlockInfo>();
				init.add(new BlockInfo(mod, name, statesStrings));
				ModLists.put(mod, init);
			}
		}

		for (Entry<String, ArrayList<BlockInfo>> entry : ModLists.entrySet()) {
			try {
				File blockDump = new File(configPath + "/genloader/dumps/blocks/", entry.getKey() + ".json");
				blockDump.getParentFile().mkdirs();
				GenLoaderAPI.log.log(Level.INFO, "Created File: " + entry.getKey() + ".json");
				String stream = GsonHelper.getAdaptedGson().toJson(entry.getValue());

				FileWriter writer = new FileWriter(blockDump, false);

				writer.write(stream);
				writer.close();
			} catch (IOException error) {
			}
		}
	}

	public static class BlockInfo
	{
		private final String Mod;
		private final String Name;
		private final ArrayList<String> States;

		public BlockInfo(String mod, String name, ArrayList<String> states)
		{
			this.Mod = mod;
			this.Name = name;
			this.States = states;
		}

		public String getIdentifyier()
		{
			return this.Mod + ":" + this.Name;
		}

		public ArrayList<String> getStates()
		{
			return this.States;
		}
	}
}
