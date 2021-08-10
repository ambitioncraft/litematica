package com.ambitioncraft.willow.litematicahelpers;


import com.google.common.collect.ImmutableMap;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager.PlacementPart;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement.RequiredEnabled;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.util.RayTraceUtils.RayTraceWrapper;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.IntBoundingBox;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.malilib.util.SubChunkPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static fi.dy.masa.litematica.config.Configs.Generic.*;


public class Printer {
    @Environment(EnvType.CLIENT)
    public static EasyPlaceResult replaceLiquids(MinecraftClient mc) {


        RayTraceWrapper traceWrapper = RayTraceUtils.getGenericTrace(mc.world, mc.player, 6, true);
        if (traceWrapper == null) {
            return EasyPlaceResult.FAIL;
        }
        BlockHitResult trace = traceWrapper.getBlockHitResult();
        BlockPos tracePos = trace.getBlockPos();
        int posX = tracePos.getX();
        int posY = tracePos.getY();
        int posZ = tracePos.getZ();

        SubChunkPos cpos = new SubChunkPos(tracePos);

        int maxInteract = EASY_PLACE_MODE_MAX_BLOCKS.getIntegerValue();
        int rangeX = EASY_PLACE_MODE_RANGE_X.getIntegerValue();
        int rangeY = EASY_PLACE_MODE_RANGE_Y.getIntegerValue();
        int rangeZ = EASY_PLACE_MODE_RANGE_Z.getIntegerValue();

        int interact = 0;
        boolean hasPicked = false;
        Text pickedBlock = null;

        int fromX = posX - rangeX;
        int fromY = posY - rangeY;
        int fromZ = posZ - rangeZ;

        int toX = posX + rangeX;
        int toY = posY + rangeY;
        int toZ = posZ + rangeZ;

        toY = Math.max(0, Math.min(toY, 255));
        fromY = Math.max(0, Math.min(fromY, 255));

        fromX = Math.max(fromX,(int)mc.player.getX() - 8);
        fromY = Math.max(fromY,(int)mc.player.getY() - 8);
        fromZ = Math.max(fromZ,(int)mc.player.getZ() - 8);

        toX = Math.min(toX,(int)mc.player.getX() + 8);
        toY = Math.min(toY,(int)mc.player.getY() + 8);
        toZ = Math.min(toZ,(int)mc.player.getZ() + 8);


        ItemStack playerStack = mc.player.getMainHandStack();
        List<String> whiteList = FLUID_REPLACE_WHITELIST.getStrings();

        String item = playerStack.getItem().getTranslationKey().replace("block.","").replace(".",":");
        if(!whiteList.contains(item)){
            return EasyPlaceResult.FAIL;
        }

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {

                    double dx = mc.player.getX() - x - 0.5;
                    double dy = mc.player.getY() - y - 0.5;
                    double dz = mc.player.getZ() - z - 0.5;

                    if (dx * dx + dy * dy + dz * dz > 1024.0) // Check if within reach distance
                        continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    boolean cached = PlacementUtils.isPositionCached(pos,false);
                    if(cached){continue;}

                    FluidState fluidState = mc.world.getFluidState(pos);
                    if(fluidState != null && !fluidState.isEmpty()){
                        if(fluidState.getLevel() == 8) {
                            System.out.println("replace fluid");
                            Vec3d hitPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                            BlockHitResult hit = new BlockHitResult(hitPos, Direction.NORTH, pos, false);
                            mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, hit);
                            PlacementUtils.cacheEasyPlacePosition(pos, false);
                        }
                    }
                }
            }
        }
        return EasyPlaceResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    public static EasyPlaceResult doPrinterAction(MinecraftClient mc) {
        RayTraceWrapper traceWrapper = RayTraceUtils.getGenericTrace(mc.world, mc.player, 6, true);
        if (traceWrapper == null) {
            return EasyPlaceResult.FAIL;
        }
        BlockHitResult trace = traceWrapper.getBlockHitResult();
        BlockPos tracePos = trace.getBlockPos();
        int posX = tracePos.getX();
        int posY = tracePos.getY();
        int posZ = tracePos.getZ();

        SubChunkPos cpos = new SubChunkPos(tracePos);
        List<PlacementPart> list = DataManager.getSchematicPlacementManager().getAllPlacementsTouchingSubChunk(cpos);

        if (list.isEmpty()) {
            return EasyPlaceResult.PASS;
        }
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
        int minX = 0;
        int minY = 0;
        int minZ = 0;

        boolean foundBox = false;
        for (PlacementPart part : list) {
            IntBoundingBox pbox = part.getBox();
            if (pbox.containsPos(tracePos)) {

                ImmutableMap<String, Box> boxes = part.getPlacement()
                        .getSubRegionBoxes(RequiredEnabled.PLACEMENT_ENABLED);

                for (Box box : boxes.values()) {

                    final int boxXMin = Math.min(box.getPos1().getX(), box.getPos2().getX());
                    final int boxYMin = Math.min(box.getPos1().getY(), box.getPos2().getY());
                    final int boxZMin = Math.min(box.getPos1().getZ(), box.getPos2().getZ());
                    final int boxXMax = Math.max(box.getPos1().getX(), box.getPos2().getX());
                    final int boxYMax = Math.max(box.getPos1().getY(), box.getPos2().getY());
                    final int boxZMax = Math.max(box.getPos1().getZ(), box.getPos2().getZ());

                    if (posX < boxXMin || posX > boxXMax || posY < boxYMin || posY > boxYMax || posZ < boxZMin
                            || posZ > boxZMax)
                        continue;
                    minX = boxXMin;
                    maxX = boxXMax;
                    minY = boxYMin;
                    maxY = boxYMax;
                    minZ = boxZMin;
                    maxZ = boxZMax;
                    foundBox = true;

                    break;
                }

                break;
            }
        }

        if (!foundBox) {
            return EasyPlaceResult.PASS;
        }

        int rangeX = EASY_PLACE_MODE_RANGE_X.getIntegerValue();
        int rangeY = EASY_PLACE_MODE_RANGE_Y.getIntegerValue();
        int rangeZ = EASY_PLACE_MODE_RANGE_Z.getIntegerValue();
        boolean breakBlocks = EASY_PLACE_MODE_BREAK_BLOCKS.getBooleanValue();
        Direction[] facingSides = Direction.getEntityFacingOrder(mc.player);
        Direction primaryFacing = facingSides[0];
        Direction horizontalFacing = primaryFacing; // For use in blocks with only horizontal rotation

        int index = 0;
        while (horizontalFacing.getAxis() == Direction.Axis.Y && index < facingSides.length) {
            horizontalFacing = facingSides[index++];
        }

        World world = SchematicWorldHandler.getSchematicWorld();

        /*
         * TODO: THIS IS REALLY BAD IN TERMS OF EFFICIENCY. I suggest using some form of
         * search with a built in datastructure first Maybe quadtree? (I dont know how
         * MC works)
         */

        int maxInteract = EASY_PLACE_MODE_MAX_BLOCKS.getIntegerValue();
        int interact = 0;
        boolean hasPicked = false;
        Text pickedBlock = null;

        int fromX = Math.max(posX - rangeX, minX);
        int fromY = Math.max(posY - rangeY, minY);
        int fromZ = Math.max(posZ - rangeZ, minZ);

        int toX = Math.min(posX + rangeX, maxX);
        int toY = Math.min(posY + rangeY, maxY);
        int toZ = Math.min(posZ + rangeZ, maxZ);

        toY = Math.max(0, Math.min(toY, 255));
        fromY = Math.max(0, Math.min(fromY, 255));

        fromX = Math.max(fromX,(int)mc.player.getX() - 8);
        fromY = Math.max(fromY,(int)mc.player.getY() - 8);
        fromZ = Math.max(fromZ,(int)mc.player.getZ() - 8);

        toX = Math.min(toX,(int)mc.player.getX() + 8);
        toY = Math.min(toY,(int)mc.player.getY() + 8);
        toZ = Math.min(toZ,(int)mc.player.getZ() + 8);

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {

                    double dx = mc.player.getX() - x - 0.5;
                    double dy = mc.player.getY() - y - 0.5;
                    double dz = mc.player.getZ() - z - 0.5;

                    if (dx * dx + dy * dy + dz * dz > 1024.0) // Check if within reach distance
                        continue;

                    BlockPos pos = new BlockPos(x, y, z);

                    BlockState stateSchematic = world.getBlockState(pos);
                    BlockState stateClient = mc.world.getBlockState(pos);

                    // if force lookat
                    boolean forceLookAtPosition = false;
                    if(forceLookAtPosition && (pos.getX() != posX || pos.getY() != posY || pos.getZ() != posZ)){
                     continue;
                    }
                    LayerRange range = DataManager.getRenderLayerRange();
                    // The targeted position is outside the current render range
                    if (!range.isPositionWithinRange(pos))
                    {
                        continue;
                    }


                    if (breakBlocks && stateSchematic != null && !stateClient.isAir()) {
                        if (!stateClient.getBlock().getName().equals(stateSchematic.getBlock().getName()) && dx * dx + Math.pow(dy + 1.5,2) + dz * dz <= 36.0) {
                            mc.interactionManager.attackBlock(pos, Direction.DOWN);
                            interact++;

                            if (interact >= maxInteract) {
                                return EasyPlaceResult.SUCCESS;
                            }
                        }
                    }
                    if (stateSchematic.isAir())
                        continue;

                    // Abort if there is already a block in the target position
                    if (PlacementUtils.Vanilla.easyPlaceBlockChecksCancel(stateSchematic, stateClient, mc.player, trace)) {

                        /*
                         * Sometimes, blocks have other states like the delay on a repeater. So, this
                         * code clicks the block until the state is the same I don't know if Schematica
                         * does this too, I just did it because I work with a lot of redstone
                         */
                        if (!stateClient.isAir() && !mc.player.isSneaking() && !PlacementUtils.isPositionCached(pos, true)) {
                            Block cBlock = stateClient.getBlock();
                            Block sBlock = stateSchematic.getBlock();

                            if (cBlock.getName().equals(sBlock.getName())) {
                                Direction facingSchematic = fi.dy.masa.malilib.util.BlockUtils
                                        .getFirstPropertyFacingValue(stateSchematic);
                                Direction facingClient = fi.dy.masa.malilib.util.BlockUtils
                                        .getFirstPropertyFacingValue(stateClient);

                                if (facingSchematic == facingClient) {
                                    int clickTimes = 0;
                                    Direction side = Direction.NORTH;
                                    if (sBlock instanceof RepeaterBlock) {
                                        int clientDelay = stateClient.get(RepeaterBlock.DELAY);
                                        int schematicDelay = stateSchematic.get(RepeaterBlock.DELAY);
                                        if (clientDelay != schematicDelay) {

                                            if (clientDelay < schematicDelay) {
                                                clickTimes = schematicDelay - clientDelay;
                                            } else if (clientDelay > schematicDelay) {
                                                clickTimes = schematicDelay + (4 - clientDelay);
                                            }
                                        }
                                        side = Direction.UP;
                                    } else if (sBlock instanceof ComparatorBlock) {
                                        if (stateSchematic.get(ComparatorBlock.MODE) != stateClient
                                                .get(ComparatorBlock.MODE))
                                            clickTimes = 1;
                                        side = Direction.UP;
                                    } else if (sBlock instanceof LeverBlock) {
                                        if (stateSchematic.get(LeverBlock.POWERED) != stateClient
                                                .get(LeverBlock.POWERED))
                                            clickTimes = 1;

                                        /*
                                         * I dont know if this direction code is needed. I am just doing it anyway to
                                         * make it "make sense" to the server (I am emulating what the client does so
                                         * the server isn't confused)
                                         */
                                        if (stateClient.get(LeverBlock.FACE) == WallMountLocation.CEILING) {
                                            side = Direction.DOWN;
                                        } else if (stateClient.get(LeverBlock.FACE) == WallMountLocation.FLOOR) {
                                            side = Direction.UP;
                                        } else {
                                            side = stateClient.get(LeverBlock.FACING);
                                        }

                                    } else if (sBlock instanceof TrapdoorBlock) {
                                        if (stateSchematic.getMaterial() != Material.METAL && stateSchematic
                                                .get(TrapdoorBlock.OPEN) != stateClient.get(TrapdoorBlock.OPEN))
                                            clickTimes = 1;
                                    } else if (sBlock instanceof FenceGateBlock) {
                                        if (stateSchematic.get(FenceGateBlock.OPEN) != stateClient
                                                .get(FenceGateBlock.OPEN))
                                            clickTimes = 1;
                                    } else if (sBlock instanceof DoorBlock) {
                                        if (stateClient.getMaterial() != Material.METAL && stateSchematic
                                                .get(DoorBlock.OPEN) != stateClient.get(DoorBlock.OPEN))
                                            clickTimes = 1;
                                    } else if (sBlock instanceof NoteBlock) {
                                        int note = stateClient.get(NoteBlock.NOTE);
                                        int targetNote = stateSchematic.get(NoteBlock.NOTE);
                                        if (note != targetNote) {

                                            if (note < targetNote) {
                                                clickTimes = targetNote - note;
                                            } else if (note > targetNote) {
                                                clickTimes = targetNote + (25 - note);
                                            }
                                        }
                                    }

                                    for (int i = 0; i < clickTimes; i++) // Click on the block a few times
                                    {
                                        Hand hand = Hand.MAIN_HAND;

                                        Vec3d hitPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                                        BlockHitResult hitResult = new BlockHitResult(hitPos, side, pos, false);

                                        mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
                                        interact++;

                                        if (interact >= maxInteract) {
                                            return EasyPlaceResult.SUCCESS;
                                        }
                                    }

                                    if (clickTimes > 0) {
                                        PlacementUtils.cacheEasyPlacePosition(pos, true);
                                    }

                                }
                            }
                        }
                        continue;
                    }
                    if (PlacementUtils.isPositionCached(pos, false)) {
                        continue;
                    }

                    ItemStack stack = ((MaterialCache) MaterialCache.getInstance()).getRequiredBuildItemForState((BlockState)stateSchematic);
                    if (stack.isEmpty() == false && (mc.player.abilities.creativeMode || mc.player.inventory.getSlotWithStack(stack) != -1)) {

                        if (stateSchematic == stateClient) {
                            continue;
                        }

                        Direction facing = fi.dy.masa.malilib.util.BlockUtils.getFirstPropertyFacingValue(stateSchematic);
                        if (facing != null) {
                            PlacementUtils.FacingData facedata = PlacementUtils.getFacingData(stateSchematic);
                            if (!PlacementUtils.canPlaceFace(facedata, stateSchematic, mc.player, primaryFacing, horizontalFacing))
                                continue;

                            if ((stateSchematic.getBlock() instanceof DoorBlock
                                    && stateSchematic.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                                    || (stateSchematic.getBlock() instanceof BedBlock
                                    && stateSchematic.get(BedBlock.PART) == BedPart.HEAD)

                            ) {
                                continue;
                            }
                        }

                        // Exception for signs (edge case)
                        if (stateSchematic.getBlock() instanceof SignBlock
                                && !(stateSchematic.getBlock() instanceof WallSignBlock)) {
                            if ((MathHelper.floor((double) ((180.0F + mc.player.yaw) * 16.0F / 360.0F) + 0.5D)
                                    & 15) != stateSchematic.get(SignBlock.ROTATION))
                                continue;

                        }
                        double offX = 0.5; // We dont really need this. But I did it anyway so that I could experiment
                        // easily.
                        double offY = 0.5;
                        double offZ = 0.5;

                        Direction sideOrig = Direction.NORTH;
                        BlockPos npos = pos;
                        Direction side = PlacementUtils.Precise.applyPrecisePlacementFacing(stateSchematic, sideOrig, stateClient);
                        Block blockSchematic = stateSchematic.getBlock();
                        if (blockSchematic instanceof WallMountedBlock || blockSchematic instanceof TorchBlock
                                || blockSchematic instanceof LadderBlock || blockSchematic instanceof TrapdoorBlock
                                || blockSchematic instanceof TripwireHookBlock || blockSchematic instanceof SignBlock || blockSchematic instanceof EndRodBlock) {

                            /*
                             * Some blocks, especially wall mounted blocks must be placed on another for
                             * directionality to work Basically, the block pos sent must be a "clicked"
                             * block.
                             */
                            int px = pos.getX();
                            int py = pos.getY();
                            int pz = pos.getZ();

                            if (side == Direction.DOWN) {
                                py += 1;
                            } else if (side == Direction.UP) {
                                py += -1;
                            } else if (side == Direction.NORTH) {
                                pz += 1;
                            } else if (side == Direction.SOUTH) {
                                pz += -1;
                            } else if (side == Direction.EAST) {
                                px += -1;
                            } else if (side == Direction.WEST) {
                                px += 1;
                            }

                            npos = new BlockPos(px, py, pz);

                            BlockState clientStateItem = mc.world.getBlockState(npos);
                            FluidState clientStateFluid = mc.world.getFluidState(npos);
                            if (clientStateItem == null || clientStateItem.isAir() || clientStateFluid != null) {
                                if (!(blockSchematic instanceof TrapdoorBlock)) {
                                    continue;
                                }
                                BlockPos testPos;

                                /*
                                 * Trapdoors are special. They can also be placed on top, or below another block
                                 */
                                if (stateSchematic.get(TrapdoorBlock.HALF) == BlockHalf.TOP) {
                                    testPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
                                    side = Direction.DOWN;
                                } else {
                                    testPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                                    side = Direction.UP;
                                }
                                BlockState clientStateItemTest = mc.world.getBlockState(testPos);

                                if (clientStateItemTest == null || clientStateItemTest.isAir()) {
                                    BlockState schematicNItem = world.getBlockState(npos);

                                    BlockState schematicTItem = world.getBlockState(testPos);

                                    /*
                                     * If possible, it is always best to attatch the trapdoor to an actual block
                                     * that exists on the world But other times, it can't be helped
                                     */
                                    if ((schematicNItem != null && !schematicNItem.isAir())
                                            || (schematicTItem != null && !schematicTItem.isAir()))
                                        continue;
                                    npos = pos;
                                } else
                                    npos = testPos;

                                // If trapdoor is placed from top or bottom, directionality is decided by player
                                // direction
                                if (stateSchematic.get(TrapdoorBlock.FACING).getOpposite() != horizontalFacing) {
                                    continue;
                                }

                            }

                        }

                        // Abort if the required item was not able to be pick-block'd
                        if (!hasPicked) {

                            if (PlacementUtils.Precise.doSchematicWorldPickBlockPrinter(true, mc, stateSchematic, pos) == false) {
                                return EasyPlaceResult.FAIL;
                            }
                            hasPicked = true;
                            pickedBlock = stateSchematic.getBlock().getName();
                        } else if (pickedBlock != null && !pickedBlock.equals(stateSchematic.getBlock().getName())) {
                            continue;
                        }

                        Hand hand = EntityUtils.getUsedHandForItem(mc.player, stack);

                        // Abort if a wrong item is in the player's hand
                        if (hand == null) {
                            continue;
                        }

                        Vec3d hitPos = new Vec3d(offX, offY, offZ);
                        // Carpet Accurate Placement protocol support, plus BlockSlab support
                        hitPos = PlacementUtils.Precise.applyHitVec(npos, stateSchematic, hitPos, side);

                        // Mark that this position has been handled (use the non-offset position that is
                        // checked above)
                        PlacementUtils.cacheEasyPlacePosition(pos, false);

                        BlockHitResult hitResult = new BlockHitResult(hitPos, side, npos, false);

                        // System.out.printf("pos: %s side: %s, hit: %s\n", pos, side, hitPos);
                        // pos, side, hitPos

                        mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
                        interact++;
                        if (stateSchematic.getBlock() instanceof SlabBlock
                                && stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
                            stateClient = mc.world.getBlockState(npos);

                            if (stateClient.getBlock() instanceof SlabBlock
                                    && stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
                                side = PlacementUtils.Precise.applyPrecisePlacementFacing(stateSchematic, sideOrig, stateClient);
                                hitResult = new BlockHitResult(hitPos, side, npos, false);
                                mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
                                interact++;
                            }
                        }

                        if (interact >= maxInteract) {
                            return EasyPlaceResult.SUCCESS;
                        }

                    }

                }
            }

        }

        return (interact > 0) ? EasyPlaceResult.SUCCESS : EasyPlaceResult.FAIL;
    }


}
