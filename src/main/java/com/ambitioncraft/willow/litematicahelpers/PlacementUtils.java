package com.ambitioncraft.willow.litematicahelpers;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.LayerRange;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlacementUtils {
    private static final List<PositionCache> EASY_PLACE_POSITIONS = new ArrayList<PositionCache>();
    private static final Map<Class<? extends Block>, FacingData> facingMap = new LinkedHashMap<Class<? extends Block>, FacingData>();
    private static boolean setupFacing = false;
    private static void addFD(final Class<? extends Block> c, FacingData data) {
        facingMap.put(c, data);
    }

    private static void setUpFacingData() {
        setupFacing = true;

        /*
         * 0 = Normal up/down/east/west/south/north directions
         * 1 = Horizontal directions
         * 2 = Wall Attactchable block
         *
         *
         * TODO: THIS CODE MUST BE CLEANED UP.
         */

        // All directions, reverse of what player is facing
        addFD(PistonBlock.class, new FacingData(0, true));
        addFD(DispenserBlock.class, new FacingData(0, true));
        addFD(DropperBlock.class, new FacingData(0, true));

        // All directions, normal direction of player
        addFD(ObserverBlock.class, new FacingData(0, false));


        // Horizontal directions, normal direction
        addFD(StairsBlock.class, new FacingData(1, false));
        addFD(DoorBlock.class, new FacingData(1, false));
        addFD(BedBlock.class, new FacingData(1, false));
        addFD(FenceGateBlock.class, new FacingData(1, false));

        // Horizontal directions, reverse of what player is facing
        addFD(ChestBlock.class, new FacingData(1, true));
        addFD(RepeaterBlock.class, new FacingData(1, true));
        addFD(ComparatorBlock.class, new FacingData(1, true));
        addFD(EnderChestBlock.class, new FacingData(1, true));
        addFD(FurnaceBlock.class, new FacingData(1, true));
        addFD(LecternBlock.class, new FacingData(1, true));
        addFD(LoomBlock.class, new FacingData(1, true));
        addFD(BeehiveBlock.class, new FacingData(1, true));
        addFD(StonecutterBlock.class, new FacingData(1, true));
        addFD(CarvedPumpkinBlock.class, new FacingData(1, true));
        addFD(PumpkinBlock.class, new FacingData(1, true));
        addFD(EndPortalFrameBlock.class, new FacingData(1, true));

        // Top/bottom placable side mountable blocks
        addFD(LeverBlock.class, new FacingData(2, false));
        addFD(AbstractButtonBlock.class, new FacingData(2, false));
        //addFD(BellBlock.class, new FacingData(2, false));
        //addFD(GrindstoneBlock.class, new FacingData(2, false));

    }

    /*
     * Checks if the block can be placed in the correct orientation if player is
     * facing a certain direction Dont place block if orientation will be wrong
     */
    public static boolean canPlaceFace(FacingData facedata, BlockState stateSchematic, PlayerEntity player,
                                       Direction primaryFacing, Direction horizontalFacing) {
        Direction facing = BlockUtils.getFirstPropertyFacingValue(stateSchematic);
        if (facing != null && facedata != null) {

            switch (facedata.type) {
                case 0: // All directions (ie, observers and pistons)
                    if (facedata.isReversed) {
                        return facing.getOpposite() == primaryFacing;
                    } else {
                        return facing == primaryFacing;
                    }
                case 1: // Only Horizontal directions (ie, repeaters and comparators)
                    if (facedata.isReversed) {
                        return facing.getOpposite() == horizontalFacing;
                    } else {
                        return facing == horizontalFacing;
                    }
                case 2: // Wall mountable, such as a lever, only use player direction if not on wall.
                    return stateSchematic.get(WallMountedBlock.FACE) == WallMountLocation.WALL
                            || facing == horizontalFacing;
                default: // Ignore rest -> TODO: Other blocks like anvils, etc...
                    return true;
            }
        } else {
            return true;
        }
    }

    public static void cacheEasyPlacePosition(BlockPos pos, boolean useClicked) {
        PositionCache item = new PositionCache(pos, System.nanoTime(), 2000000000);
        if(useClicked){
            item.hasClicked = true;
        }
        EASY_PLACE_POSITIONS.add(item);
    }

    public static FacingData getFacingData(BlockState state) {
        if (!setupFacing) {
            setUpFacingData();
        }
        Block block = state.getBlock();
        for (final Class<? extends Block> c : facingMap.keySet()) {
            if (c.isInstance(block)) {
                return facingMap.get(c);
            }
        }
        return null;
    }

    public static boolean isPositionCached(BlockPos pos, boolean useClicked) {
        long currentTime = System.nanoTime();
        boolean cached = false;

        for (int i = 0; i < EASY_PLACE_POSITIONS.size(); ++i) {
            PositionCache val = EASY_PLACE_POSITIONS.get(i);
            boolean expired = val.hasExpired(currentTime);

            if (expired) {
                EASY_PLACE_POSITIONS.remove(i);
                --i;
            } else if (val.getPos().equals(pos)) {

                // Item placement and "using"/"clicking" (changing delay for repeaters) are
                // different
                if (!useClicked || val.hasClicked) {
                    cached = true;
                }

                // Keep checking and removing old entries if there are a fair amount
                if (EASY_PLACE_POSITIONS.size() < 16) {
                    break;
                }
            }
        }

        return cached;
    }

    public static class PositionCache {
        private final BlockPos pos;
        private final long time;
        private final long timeout;
        public boolean hasClicked = false;

        private PositionCache(BlockPos pos, long time, long timeout) {
            this.pos = pos;
            this.time = time;
            this.timeout = timeout;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public boolean hasExpired(long currentTime) {
            return currentTime - this.time > this.timeout;
        }
    }

    public static class FacingData {
        public int type;
        public boolean isReversed;

        FacingData(int type, boolean isrev) {
            this.type = type;
            this.isReversed = isrev;
        }
    }


    public static class Precise {

        /*
         * Gets the direction necessary to build the block oriented correctly. TODO:
         * Need a better way to do this.
         */
        public static Direction applyPrecisePlacementFacing(BlockState stateSchematic, Direction side, BlockState stateClient) {
            Block blockSchematic = stateSchematic.getBlock();
            Block blockClient = stateClient.getBlock();

            if (blockSchematic instanceof SlabBlock) {
                if (stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE && blockClient instanceof SlabBlock
                        && stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
                    if (stateClient.get(SlabBlock.TYPE) == SlabType.TOP) {
                        return Direction.DOWN;
                    } else {
                        return Direction.UP;
                    }
                }
                // Single slab
                else {
                    return Direction.NORTH;
                }
            } else if (/*blockSchematic instanceof LogBlock ||*/ blockSchematic instanceof PillarBlock) {
                Direction.Axis axis = stateSchematic.get(PillarBlock.AXIS);
                // Logs and pillars only have 3 directions that are important
                if (axis == Direction.Axis.X) {
                    return Direction.WEST;
                } else if (axis == Direction.Axis.Y) {
                    return Direction.DOWN;
                } else if (axis == Direction.Axis.Z) {
                    return Direction.NORTH;
                }

            } else if (blockSchematic instanceof WallSignBlock) {
                return stateSchematic.get(WallSignBlock.FACING);
            } else if (blockSchematic instanceof SignBlock) {
                return Direction.UP;
            } else if (blockSchematic instanceof WallMountedBlock) {
                WallMountLocation location = stateSchematic.get(WallMountedBlock.FACE);
                if (location == WallMountLocation.FLOOR) {
                    return Direction.UP;
                } else if (location == WallMountLocation.CEILING) {
                    return Direction.DOWN;
                } else {
                    return stateSchematic.get(WallMountedBlock.FACING);

                }

            } else if (blockSchematic instanceof HopperBlock) {
                return stateSchematic.get(HopperBlock.FACING).getOpposite();
            } else if (blockSchematic instanceof TorchBlock) {

                if (blockSchematic instanceof WallTorchBlock) {
                    return stateSchematic.get(WallTorchBlock.FACING);
                } else if (blockSchematic instanceof WallRedstoneTorchBlock) {
                    return stateSchematic.get(WallRedstoneTorchBlock.FACING);
                } else {
                    return Direction.UP;
                }
            } else if (blockSchematic instanceof LadderBlock) {
                return stateSchematic.get(LadderBlock.FACING);
            } else if (blockSchematic instanceof TrapdoorBlock) {
                return stateSchematic.get(TrapdoorBlock.FACING);
            } else if (blockSchematic instanceof TripwireHookBlock) {
                return stateSchematic.get(TripwireHookBlock.FACING);
            } else if (blockSchematic instanceof EndRodBlock) {
                return stateSchematic.get(EndRodBlock.FACING);
            }

            // TODO: Add more for other blocks
            return side;
        }

        @Environment(EnvType.CLIENT)
        public static boolean doSchematicWorldPickBlockPrinter(boolean closest, MinecraftClient mc, BlockState preference,
                                                        BlockPos pos) {

            World world = SchematicWorldHandler.getSchematicWorld();

            ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(preference, world, pos);

            if (stack.isEmpty() == false) {
                PlayerInventory inv = mc.player.inventory;

                if (mc.player.abilities.creativeMode) {
                    mc.interactionManager.clickCreativeStack(stack, 36 + inv.selectedSlot);
                    return true;
                } else {

                    int slot = inv.getSlotWithStack(stack);

                    boolean shouldPick = inv.selectedSlot != slot;
                    boolean canPick = slot != -1;

                    if (shouldPick && canPick) {
                        inv.addPickBlock(stack);
                        //InventoryUtils.setPickedItemToHand(stack, mc);
                    }

                    // return shouldPick == false || canPick;
                }
            }

            return true;
        }

        /**
         * Apply hit vectors (used to be Carpet hit vec protocol, but I think it is
         * uneccessary now with orientation/states programmed in)
         *
         * @param pos
         * @param state
         * @param hitVecIn
         * @return
         */
        public static Vec3d applyHitVec(BlockPos pos, BlockState state, Vec3d hitVecIn, Direction side) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            double dx = hitVecIn.getX();
            double dy = hitVecIn.getY();
            double dz = hitVecIn.getZ();
            Block block = state.getBlock();

            /*
             * I dont know if this is needed, just doing to mimick client According to the
             * MC protocol wiki, the protocol expects a 1 on a side that is clicked
             */
            if (side == Direction.UP) {
                dy = 1;
            } else if (side == Direction.DOWN) {
                dy = 0;
            } else if (side == Direction.EAST) {
                dx = 1;
            } else if (side == Direction.WEST) {
                dx = 0;
            } else if (side == Direction.SOUTH) {
                dz = 1;
            } else if (side == Direction.NORTH) {
                dz = 0;
            }

            if (block instanceof StairsBlock) {
                if (state.get(StairsBlock.HALF) == BlockHalf.TOP) {
                    dy = 0.9;
                } else {
                    dy = 0;
                }
            } else if (block instanceof SlabBlock && state.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
                if (state.get(SlabBlock.TYPE) == SlabType.TOP) {
                    dy = 0.9;
                } else {
                    dy = 0;
                }
            } else if (block instanceof TrapdoorBlock) {
                if (state.get(TrapdoorBlock.HALF) == BlockHalf.TOP) {
                    dy = 0.9;
                } else {
                    dy = 0;
                }
            }
            return new Vec3d(x + dx, y + dy, z + dz);
        }

    }

    public static class Vanilla{

        public static Direction applyPlacementFacing(BlockState stateSchematic, Direction side, BlockState stateClient)
        {
            Block blockSchematic = stateSchematic.getBlock();
            Block blockClient = stateClient.getBlock();

            if (blockSchematic instanceof SlabBlock)
            {
                if (stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE &&
                        blockClient instanceof SlabBlock &&
                        stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE)
                {
                    if (stateClient.get(SlabBlock.TYPE) == SlabType.TOP)
                    {
                        return Direction.DOWN;
                    }
                    else
                    {
                        return Direction.UP;
                    }
                }
                // Single slab
                else
                {
                    return Direction.NORTH;
                }
            }

            return side;
        }


        public static Vec3d applyCarpetProtocolHitVec(BlockPos pos, BlockState state, Vec3d hitVecIn) {
            double x = hitVecIn.x;
            double y = hitVecIn.y;
            double z = hitVecIn.z;
            Block block = state.getBlock();
            Direction facing = BlockUtils.getFirstPropertyFacingValue(state);
            double relX = hitVecIn.x - (double)pos.getX();
            if (facing != null) {
                x = (double)pos.getX() + relX + 2.0D + (double)(facing.getId() * 2);
            }

            if (block instanceof RepeaterBlock) {
                x += (double)(((Integer)state.get(RepeaterBlock.DELAY) - 1) * 16);
            } else if (block instanceof TrapdoorBlock && state.get(TrapdoorBlock.HALF) == BlockHalf.TOP) {
                x += 16.0D;
            } else if (block instanceof ComparatorBlock && state.get(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
                x += 16.0D;
            } else if (block instanceof StairsBlock && state.get(StairsBlock.HALF) == BlockHalf.TOP) {
                x += 16.0D;
            } else if (block instanceof SlabBlock && state.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
                if (state.get(SlabBlock.TYPE) == SlabType.TOP) {
                    y = (double)pos.getY() + 0.9D;
                } else {
                    y = (double)pos.getY();
                }
            }

            return new Vec3d(x, y, z);
        }

        /**
         * Does placement restriction checks for the targeted position.
         * If the targeted position is outside of the current layer range, or should be air
         * in the schematic, or the player is holding the wrong item in hand, then true is returned
         * to indicate that the use action should be cancelled.
         * @param mc
         * @return true if the use action should be cancelled
         */
        public static boolean placementRestrictionInEffect(MinecraftClient mc)
        {
            HitResult trace = mc.crosshairTarget;

            ItemStack stack = mc.player.getMainHandStack();

            if (stack.isEmpty())
            {
                stack = mc.player.getOffHandStack();
            }

            if (stack.isEmpty())
            {
                return false;
            }

            if (trace != null && trace.getType() == HitResult.Type.BLOCK)
            {
                BlockHitResult blockHitResult = (BlockHitResult) trace;
                BlockPos pos = blockHitResult.getBlockPos();
                ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(mc.player, Hand.MAIN_HAND, blockHitResult));

                // Get the possibly offset position, if the targeted block is not replaceable
                pos = ctx.getBlockPos();

                BlockState stateClient = mc.world.getBlockState(pos);

                World worldSchematic = SchematicWorldHandler.getSchematicWorld();
                LayerRange range = DataManager.getRenderLayerRange();
                boolean schematicHasAir = worldSchematic.isAir(pos);

                // The targeted position is outside the current render range
                if (schematicHasAir == false && range.isPositionWithinRange(pos) == false)
                {
                    return true;
                }

                // There should not be anything in the targeted position,
                // and the position is within or close to a schematic sub-region
                if (schematicHasAir && WorldUtils.isPositionWithinRangeOfSchematicRegions(pos, 2))
                {
                    return true;
                }

                blockHitResult = new BlockHitResult(blockHitResult.getPos(), blockHitResult.getSide(), pos, false);
                ctx = new ItemPlacementContext(new ItemUsageContext(mc.player, Hand.MAIN_HAND, (BlockHitResult) trace));

                // Placement position is already occupied
                if (stateClient.canReplace(ctx) == false)
                {
                    return true;
                }

                BlockState stateSchematic = worldSchematic.getBlockState(pos);
                stack = MaterialCache.getInstance().getRequiredBuildItemForState(stateSchematic);

                // The player is holding the wrong item for the targeted position
                if (stack.isEmpty() == false && EntityUtils.getUsedHandForItem(mc.player, stack) == null)
                {
                    return true;
                }
            }

            return false;
        }

        public static boolean easyPlaceBlockChecksCancel(BlockState stateSchematic, BlockState stateClient,
                                                         PlayerEntity player, HitResult trace)
        {
            Block blockSchematic = stateSchematic.getBlock();

            if (blockSchematic instanceof SlabBlock && stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE)
            {
                Block blockClient = stateClient.getBlock();

                if (blockClient instanceof SlabBlock && stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE)
                {
                    return blockSchematic != blockClient;
                }
            }

            if (trace.getType() != HitResult.Type.BLOCK)
            {
                return false;
            }

            BlockHitResult hitResult = (BlockHitResult) trace;
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, Hand.MAIN_HAND, hitResult));

            if (stateClient.canReplace(ctx) == false)
            {
                return true;
            }

            return false;
        }
    }
}
