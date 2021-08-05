package com.ambitioncraft.willow.litematicahelpers;

import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PrecisePlacement {

    @Environment(EnvType.CLIENT)
    public static ActionResult easyPlace(MinecraftClient mc) {
        // get the block right away
        RayTraceUtils.RayTraceWrapper traceWrapper = RayTraceUtils.getGenericTrace(mc.world, mc.player, 6, true);

        if(traceWrapper == null){
            return ActionResult.PASS;
        }

        if (traceWrapper.getHitType() == RayTraceUtils.RayTraceWrapper.HitType.SCHEMATIC_BLOCK){
            BlockHitResult trace = traceWrapper.getBlockHitResult();
            HitResult traceVanilla = RayTraceUtils.getRayTraceFromEntity(mc.world, mc.player, false, 6);
            BlockPos pos = trace.getBlockPos();
            World world = SchematicWorldHandler.getSchematicWorld();
            BlockState stateSchematic = world.getBlockState(pos);
            ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(stateSchematic);

            if(stack.isEmpty()){
                return ActionResult.SUCCESS;
            }

            BlockState stateClient = mc.world.getBlockState(pos);
            if(stateSchematic == stateClient){
                return ActionResult.FAIL;
            }

            if(PlacementUtils.Vanilla.easyPlaceBlockChecksCancel(stateSchematic, stateClient, mc.player, traceVanilla)){
                checkMultipleClick(pos, stateSchematic, stateClient, mc);
                return ActionResult.FAIL;
            }

            if (PlacementUtils.isPositionCached(pos, false)){
                return ActionResult.FAIL;
            }

            // Try and pick the block from the schematic, placing the stack in the players hand.
            stack = WorldUtils.doSchematicWorldPickBlock(true,mc);
            if(stack == null){
                return ActionResult.FAIL;
            }

            Hand hand = EntityUtils.getUsedHandForItem(mc.player, stack);

            // Abort if a wrong item is in the player's hand
            if (hand == null)
            {
                return ActionResult.FAIL;
            }

//            if(!mc.player.abilities.creativeMode && mc.player.inventory.getSlotWithStack(stack) == -1){
//                return ActionResult.PASS;
//            }

            Block schemaBlock = stateSchematic.getBlock();



            /*
            blocks to not use precise placement for
            Observers
            Pistons
            Dropper
            Dispenser
             */

            boolean useVanillaPlace = false;
            if(schemaBlock instanceof ObserverBlock){
                useVanillaPlace = true;
            }else if(schemaBlock instanceof PistonBlock){
                useVanillaPlace = true;
            }else if(schemaBlock instanceof  DropperBlock){
                useVanillaPlace = true;
            }else if(schemaBlock instanceof DispenserBlock){
                useVanillaPlace = true;
            }
            else if(schemaBlock instanceof GlazedTerracottaBlock){
                useVanillaPlace = true;
            }
            else if(schemaBlock instanceof SignBlock){
                useVanillaPlace = true;
            }


            Vec3d hitPos;
            Direction side;
            Direction sideOrig;
            BlockPos npos = pos;
            if(useVanillaPlace) {
                hitPos = trace.getPos();
                sideOrig = trace.getSide();
                if (traceVanilla.getType() == HitResult.Type.BLOCK)
                {
                    BlockHitResult hitResult = (BlockHitResult) traceVanilla;
                    BlockPos posVanilla = hitResult.getBlockPos();
                    Direction sideVanilla = hitResult.getSide();
                    BlockState stateVanilla = mc.world.getBlockState(posVanilla);
                    Vec3d hit = traceVanilla.getPos();
                    ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(mc.player, hand, hitResult));

                    if (!stateVanilla.canReplace(ctx))
                    {
                        posVanilla = posVanilla.offset(sideVanilla);

                        if (pos.equals(posVanilla))
                        {
                            hitPos = hit;
                            sideOrig = sideVanilla;
                        }
                    }
                }
                side = PlacementUtils.Vanilla.applyPlacementFacing(stateSchematic, sideOrig, stateClient);
                hitPos = PlacementUtils.Vanilla.applyCarpetProtocolHitVec(pos, stateSchematic, hitPos);
            }
            else {
                Direction[] facingSides = Direction.getEntityFacingOrder(mc.player);
                Direction primaryFacing = facingSides[0];
                Direction horizontalFacing = primaryFacing;

                int index = 0;
                while (horizontalFacing.getAxis() == Direction.Axis.Y && index < facingSides.length) {
                    horizontalFacing = facingSides[index++];
                }
                Direction facing = fi.dy.masa.malilib.util.BlockUtils.getFirstPropertyFacingValue(stateSchematic);
                if (facing != null) {
                    PlacementUtils.FacingData facedata = PlacementUtils.getFacingData(stateSchematic);
                    if (!PlacementUtils.canPlaceFace(facedata, stateSchematic, mc.player, primaryFacing, horizontalFacing)) {
                        //System.out.println("Cannot place face");
                        return ActionResult.FAIL;
                    }

                    if ((schemaBlock instanceof DoorBlock
                            && stateSchematic.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                            || (schemaBlock instanceof BedBlock
                            && stateSchematic.get(BedBlock.PART) == BedPart.HEAD)
                    ) {
                        return ActionResult.FAIL;
                    }
                }

                sideOrig = Direction.NORTH;
                side = PlacementUtils.Precise.applyPrecisePlacementFacing(stateSchematic, sideOrig, stateClient);

                if (schemaBlock instanceof WallMountedBlock || schemaBlock instanceof TorchBlock
                        || schemaBlock instanceof LadderBlock || schemaBlock instanceof TrapdoorBlock
                        || schemaBlock instanceof TripwireHookBlock || schemaBlock instanceof SignBlock || schemaBlock instanceof EndRodBlock) {

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

                    if (clientStateItem == null || clientStateItem.isAir()) {
                        if (!(schemaBlock instanceof TrapdoorBlock)) {
                            return ActionResult.FAIL;
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
                                    || (schematicTItem != null && !schematicTItem.isAir())) {
                                return ActionResult.FAIL;
                            }
                            npos = pos;
                        } else
                            npos = testPos;

                        // If trapdoor is placed from top or bottom, directionality is decided by player
                        // direction
                        if (stateSchematic.get(TrapdoorBlock.FACING).getOpposite() != horizontalFacing) {
                            return ActionResult.FAIL;
                        }

                    }
                }

                double offX = 0.5;
                double offY = 0.5;
                double offZ = 0.5;
                hitPos = new Vec3d(offX, offY, offZ);
                hitPos = PlacementUtils.Precise.applyHitVec(npos, stateSchematic, hitPos, side);
            }

            PlacementUtils.cacheEasyPlacePosition(pos, false);

            BlockHitResult hitResult = new BlockHitResult(hitPos, side, npos, false);
            mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
            if (stateSchematic.getBlock() instanceof SlabBlock
                    && stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
                stateClient = mc.world.getBlockState(npos);

                if (stateClient.getBlock() instanceof SlabBlock
                        && stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
                    side = PlacementUtils.Precise.applyPrecisePlacementFacing(stateSchematic, sideOrig, stateClient);
                    hitResult = new BlockHitResult(hitPos, side, npos, false);
                    mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
                }
            }
            return ActionResult.SUCCESS;

        }
        else if (traceWrapper.getHitType() == RayTraceUtils.RayTraceWrapper.HitType.VANILLA_BLOCK)
        {
            return PlacementUtils.Vanilla.placementRestrictionInEffect(mc) ? ActionResult.FAIL : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    private static void checkMultipleClick(BlockPos pos, BlockState stateSchematic, BlockState stateClient, MinecraftClient mc){
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
                    }

                    if (clickTimes > 0) {
                        PlacementUtils.cacheEasyPlacePosition(pos, true);
                    }

                }
            }
        }
    }
}
