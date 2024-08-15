package dnd.craft;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Objects;

public class DNDMc implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("dndmc");
	public static BlockPos pos1 = new BlockPos(0,0,0);
	public static BlockPos pos2 = new BlockPos(0,0,0);
	public static boolean cycle = false;
	public static int max_size = 25;
	public static World b_world;
	public static final EntityType<Shrunken> SHRUNKEN = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("minecraft", "shrunken"),
			EntityType.Builder.create(Shrunken::new, SpawnGroup.MISC).dimensions(0.0F, 0.0F).maxTrackingRange(0).build()
	);
	public boolean isCovered(BlockPos pos, World world) {

		return !(
				world.isSkyVisible(pos.add(0, 1, 0)) ||
				world.isSkyVisible(pos.add(0, -1, 0)) ||
				world.isSkyVisible(pos.add(1, 0, 0)) ||
				world.isSkyVisible(pos.add(-1, 0, 0)) ||
				world.isSkyVisible(pos.add(0, 0, 1)) ||
				world.isSkyVisible(pos.add(0, 0, -1)) ||
				world.isAir(pos.add(0, 1, 0)) ||
				world.isAir(pos.add(0, -1, 0)) ||
				world.isAir(pos.add(1, 0, 0)) ||
				world.isAir(pos.add(-1, 0, 0)) ||
				world.isAir(pos.add(0, 0, 1)) ||
				world.isAir(pos.add(0, 0, -1))
				);




	}
	public boolean isBox(BlockState block, BlockPos pos1, BlockPos pos2, ArrayList<BlockPos> covered, ArrayList<BlockPos> orgCovered) {

		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());

		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());
		if (block.isAir()) {
			return false;
		}
		ArrayList<BlockPos> coveredAdditions = new ArrayList<BlockPos>();
		for (int ix = minX; ix <= maxX; ix++) {
			for (int iy = minY; iy <= maxY; iy++) {
				for (int iz = minZ; iz <= maxZ; iz++) {
					BlockPos pos = new BlockPos(ix, iy, iz);
					if (b_world.getBlockState(pos) != block || orgCovered.contains(pos)) {
						return false;
					}
					if (!covered.contains(covered)) {
						coveredAdditions.add(pos);
					}

				}
			}
		}
		coveredAdditions.forEach((BlockPos item) -> {
			covered.add(item);
		});
		return true;
	}
	public Vector3f getMaxSize(BlockState block, BlockPos pos, ArrayList<BlockPos> covered) {
		int x = 0;
		int y = 0;
		int z = 0;
		BlockPos pos1 = pos;
		BlockPos pos2 = pos1.add(x, y, z);
		ArrayList<BlockPos> orgCovered = (ArrayList<BlockPos>) covered.clone();
		while (true) {
			x += 1;

			if (!isBox(block, pos, pos2, covered, orgCovered) || x >= max_size) {
				x -= 2 - (max_size == y ? 1 : 0);
				break;
			}
			pos2 = pos1.add(x, y, z);
		}
		pos2 = pos1.add(x, y, z);
		while (true) {
			y += 1;
			if (!isBox(block, pos, pos2, covered, orgCovered) || y >= max_size) {
				y -= 2 - (max_size == y ? 1 : 0);
				break;
			}
			pos2 = pos1.add(x, y, z);
		}
		pos2 = pos1.add(x, y, z);
		while (true) {
			z += 1;
			if (!isBox(block, pos, pos2, covered, orgCovered) || z >= max_size) {
				z -= 2 - (max_size == y ? 1 : 0);
				break;
			}
			pos2 = pos1.add(x, y, z);
		}


		return new Vector3f(x+1, y+1, z+1);
	}
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

//		FabricDefaultAttributeRegistry.register(SHRUNKEN, Shrunken.createMobAttributes());
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("filld")
				.then(CommandManager.argument("block_name", StringArgumentType.string())
						.executes(context -> {
							final String block_name = StringArgumentType.getString(context, "block_name");

							int minX = Math.min(pos1.getX(), pos2.getX());
							int minY = Math.min(pos1.getY(), pos2.getY());
							int minZ = Math.min(pos1.getZ(), pos2.getZ());

							int maxX = Math.max(pos1.getX(), pos2.getX());
							int maxY = Math.max(pos1.getY(), pos2.getY());
							int maxZ = Math.max(pos1.getZ(), pos2.getZ());

//				ServerTickEvents.END_SERVER_TICK.register((EndTick) -> b_world.setBlockState(pos1, Registries.BLOCK.get(Identifier.of("minecraft", "air")).getDefaultState()));
							if (b_world == null) {
								context.getSource().sendFeedback(() -> Text.literal("No points selected"), false);
								return 1;
							}
							for (int ix = minX; ix <= maxX; ix++) {
								for (int iy = minY; iy <= maxY; iy++) {
									for (int iz = minZ; iz <= maxZ; iz++) {
										BlockPos pos = new BlockPos(ix, iy, iz);
										b_world.setBlockState(pos, Registries.BLOCK.get(Identifier.of("minecraft", block_name)).getDefaultState());
										int expandX = 0;
										int expandY = 0;
										int expandZ = 0;



									}
								}
							}

							context.getSource().sendFeedback(() -> Text.literal("Saved Structure"), false);

							return 1;
						}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("savearea")
			.then(CommandManager.argument("name", StringArgumentType.string())
			.executes(context -> {
				final String map_name = StringArgumentType.getString(context, "name");
				LOGGER.info(map_name);
				int minX = Math.min(pos1.getX(), pos2.getX());
				int minY = Math.min(pos1.getY(), pos2.getY());
				int minZ = Math.min(pos1.getZ(), pos2.getZ());

				int maxX = Math.max(pos1.getX(), pos2.getX());
				int maxY = Math.max(pos1.getY(), pos2.getY());
				int maxZ = Math.max(pos1.getZ(), pos2.getZ());
//				ServerTickEvents.END_SERVER_TICK.register((EndTick) -> b_world.setBlockState(pos1, Registries.BLOCK.get(Identifier.of("minecraft", "air")).getDefaultState()));
				if (b_world == null) {
					context.getSource().sendFeedback(() -> Text.literal("No points selected"), false);
					return 1;
				}
				ArrayList<BlockPos> covered = new ArrayList<>();
				for (int ix = minX; ix <= maxX; ix++) {
					for (int iy = minY; iy <= maxY; iy++) {
						for (int iz = minZ; iz <= maxZ; iz++) {
							BlockPos pos = new BlockPos(ix, iy, iz);
							if (!isCovered(pos, b_world) && !b_world.isAir(pos) && !covered.contains(pos)) {
								DisplayEntity blockDisplay = new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, b_world);
//								end.setTra
								NbtCompound internal = new NbtCompound();
								NbtCompound internal2 = new NbtCompound();
//								NbtCompound transformations = new NbtCompound();
								Vector3f size = getMaxSize(b_world.getBlockState(pos), pos, covered);
								((BlockDisplayOverview) blockDisplay).invokeSetTransformation(new AffineTransformation(null, null, size, null));
//								transformations.putIntArray("scale", scale);
//								internal.put("transformation", transformations);

//								LOGGER.info(size.toString());
//								LOGGER.info(covered.toString());
								internal2.putString("Name", String.valueOf(b_world.getBlockState(pos).getBlock().getRegistryEntry().getKey().get().getValue()));
								internal.put("block_state", internal2);
//								internal.put("transformation", transformations);
								blockDisplay.readNbt(internal);
								blockDisplay.setPos(pos.getX(), pos.getY(), pos.getZ()); //THIS TOOK 5 FRICKING HOURS TO GET WOKR(ING OMFGDSIOFHSDF


								b_world.spawnEntity(blockDisplay);
							}
						}
					}
				}
				context.getSource().sendFeedback(() -> Text.literal("Saved Structure"), false);
				return 1;
			}))));


		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {



			if (Objects.equals(player.getMainHandStack().getItem().getTranslationKey(), "item.minecraft.breeze_rod") && hand == Hand.MAIN_HAND) {

				if (cycle) {
					player.sendMessage(Text.literal("Second position set!"));
					pos2 = hitResult.getBlockPos();
					cycle = false;
				} else {
					player.sendMessage(Text.literal("First position set!"));
					pos1 = hitResult.getBlockPos();
					cycle = true;
				}

				b_world = world;

			}
//			// Manual spectator check is necessary because AttackBlockCallbacks fire before the spectator check
//			if (!player.isSpectator() && player.getMainHandStack().isEmpty() && state.isToolRequired()) {
//
//				player.damage(world.getDamageSources().generic(), 1.0F);
//			}

			return TypedActionResult.pass(ItemStack.EMPTY).getResult();
		});


	}
}