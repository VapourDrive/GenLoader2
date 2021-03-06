package vapourdrive.genloader.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vapourdrive.genloader.api.generation.EnumGenerationType;
import vapourdrive.genloader.api.generation.Generation;
import vapourdrive.genloader.utils.IBlockStateHelper;

public class GL_WorldGenerator implements IWorldGenerator
{
	public GL_WorldGenerator(File configPath)
	{
		GameRegistry.registerWorldGenerator(this, 0);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		for (Generation generation : GenerationManager.finalGenerators) {
			if (random.nextFloat() < generation.getChance()) {
				if (generation.getDimensions().contains(world.provider.getDimension())) {
					if (noSetBiomeFilter(generation)
							|| isBiomeValid(world, chunkX, chunkZ, generation.getBiomeTypes(), generation.getBiomeNames())) {
						IBlockState toReplace = generation.getBlockToReplace();

						if (generation.getGeneratorType() == EnumGenerationType.STANDARDVARIABLECLUSTER) {
							WeightedWorldGenMinable generator = new WeightedWorldGenMinable(generation.getWeightedBlocks(),
									generation.getSize(), new IBlockStateHelper(toReplace));
							generateStandardOre(random, chunkX, chunkZ, world, generation.getFrequency(), generator, generation.getMinY(),
									generation.getMaxY());
						} else if (generation.getGeneratorType() == EnumGenerationType.WEIGHTEDVARIABLECLUSTER) {
							WeightedWorldGenMinable generator = new WeightedWorldGenMinable(generation.getWeightedBlocks(),
									generation.getSize(), new IBlockStateHelper(toReplace));
							generateWeightedOre(random, chunkX, chunkZ, world, generation.getFrequency(), generator, generation.getMinY(),
									generation.getMaxY());
						}
					}
				}
			}
		}
	}

	private boolean noSetBiomeFilter(Generation generation)
	{
		return generation.getBiomeTypes() == null && generation.getBiomeNames() == null;
	}

	private boolean isBiomeValid(World world, int chunkX, int chunkZ, ArrayList<Type> biomeTypes, ArrayList<String> biomeNames)
	{
		if (biomeTypes != null)
		{
			for (Type type : BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16))))
			{
				if (biomeTypes.contains(type))
				{
					return true;
				}
			}
		}
		if (biomeNames != null)
		{
			Biome biome = world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16));
			if (biomeNames.contains(biome.getBiomeName()))
			{
				return true;
			}
		}
		return false;
	}

	private void generateStandardOre(Random rand, int chunkX, int chunkZ, World world, int iterations, WorldGenerator gen, int minY,
			int maxY)
	{
		for (int i = 0; i < iterations; i++)
		{
			int x = chunkX * 16 + rand.nextInt(16);
			int y = rand.nextInt(maxY - minY) + minY;
			int z = chunkZ * 16 + rand.nextInt(16);

			gen.generate(world, rand, new BlockPos(x, y, z));
		}
	}

	private void generateWeightedOre(Random rand, int chunkX, int chunkZ, World world, int iterations, WorldGenerator gen, int minY,
			int maxY)
	{
		int centerHeight = (maxY + minY) / 2;
		int spread = (maxY - centerHeight);

		for (int i = 0; i < iterations; i++)
		{
			int x = chunkX * 16 + rand.nextInt(16);
			int y = rand.nextInt(spread) - rand.nextInt(spread) + centerHeight;
			int z = chunkZ * 16 + rand.nextInt(16);

			gen.generate(world, rand, new BlockPos(x, y, z));
		}
	}
}
