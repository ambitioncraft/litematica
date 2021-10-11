package com.ambitioncraft.willow.litematicahelpers;

import com.google.common.collect.*;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.OverlayType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class AlternateBlockUtils {
    private static AlternateBlockUtils instance;
    public ImmutableMap<Block, AlternateBlockGroup> altBlockGroupsMap = ImmutableMap.of();
    public ImmutableSet<AlternateBlockGroup> alternateBlockGroups = ImmutableSet.of();
    public IdentityHashMap<Block, ItemStack> stackCache = new IdentityHashMap<>();

    public void init() {
        alternateBlockGroups = ImmutableSet.of(
                AlternateBlockGroup.CARPETS,
                AlternateBlockGroup.CONCRETE,
                AlternateBlockGroup.CONCRETE_POWDER,
                AlternateBlockGroup.CORAL_FANS,
                AlternateBlockGroup.CORAL_WALL_FANS,
                AlternateBlockGroup.FENCE_GATES,
                AlternateBlockGroup.GLASS,
                AlternateBlockGroup.GLASS_PANES,
                AlternateBlockGroup.GLAZED_TERRACOTTA,
                AlternateBlockGroup.LEAVES,
                AlternateBlockGroup.PLANKS,
                AlternateBlockGroup.STONE,
                AlternateBlockGroup.SAND,
                AlternateBlockGroup.SIGNS,
                AlternateBlockGroup.STONE,
                AlternateBlockGroup.STONE_SLABS,
                AlternateBlockGroup.TERRACOTTA,
                AlternateBlockGroup.TRAPDOORS,
                AlternateBlockGroup.WALLS,
                AlternateBlockGroup.WALL_SIGNS,
                AlternateBlockGroup.WOOD_SLABS,
                AlternateBlockGroup.WOODEN_FENCES,
                AlternateBlockGroup.WOOL,
                AlternateBlockGroup.DOORS,
                AlternateBlockGroup.STONE_BUTTONS,
                AlternateBlockGroup.WOOD_BUTTONS,
                AlternateBlockGroup.STONE_STAIRS,
                AlternateBlockGroup.WOOD_STAIRS
        );
        stackCache.clear();
        ImmutableMap.Builder<Block, AlternateBlockGroup> builder = ImmutableMap.builder();
        alternateBlockGroups.forEach(a -> {
            if (a.enabled) {
                a.blockList.forEach(b -> {
                    builder.put(b, a);
                    stackCache.put(b, new ItemStack(b));
                });
            }
        });
        altBlockGroupsMap = builder.build();
    }

    public static AlternateBlockUtils getInstance() {
        if (instance == null) {
            instance = new AlternateBlockUtils();
            instance.init();
        }
        return instance;
    }


    public OverlayType getOverlayType(BlockState stateClient, BlockState stateSchematic) {

        if (stateClient == stateSchematic) {
            return OverlayType.NONE;
        }

        Block blockClient = stateClient.getBlock();
        Block blockSchematic = stateSchematic.getBlock();

        AlternateBlockGroup altGroup = null;
        if (Configs.Generic.ALLOW_ALTERNATE_BLOCKS.getBooleanValue()) {
            altGroup = altBlockGroupsMap.get(blockClient);
        }
        boolean isAltBlock = false;
        if (altGroup != null) {
            isAltBlock = altGroup.blockList.contains(blockSchematic);
        }
        if (!isAltBlock && blockClient == blockSchematic) {
            return OverlayType.WRONG_STATE;
        }

        if (isAltBlock) {
            String clientString = stateClient.toString().replace(blockClient.toString(), blockSchematic.toString());
            String schemaString = stateSchematic.toString();
            if (altGroup.ignoreRotation) {
                clientString = removeFacing(clientString);
                schemaString = removeFacing(schemaString);
            }
            if (clientString.equals(schemaString)) {
                return OverlayType.NONE;
            } else {
                return OverlayType.WRONG_STATE;
            }
        }
        return OverlayType.WRONG_BLOCK;
    }

    private String removeFacing(String val) {
        return val.replace("facing=north", "")
                .replace("facing=south", "")
                .replace("facing=east", "")
                .replace("facing=west", "");
    }

    public int findAlternativeStackSlotInInventory(ItemStack stack, PlayerInventory inv) {
        if (!Configs.Generic.ALLOW_ALTERNATE_BLOCKS.getBooleanValue()) {
            return -1;
        }

        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            AlternateBlockGroup bg = altBlockGroupsMap.get(block);
            if (bg != null && bg.enabled) {
                for (Block b : bg.blockList) {
                    int slot = inv.getSlotWithStack(stackCache.get(b));
                    if (slot > -1) {
                        return slot;
                    }
                }
            }
        }
        return -1;
    }

    public boolean isAlternateBlockFor(ItemStack original, ItemStack alt) {
        if (original == null || alt == null || original.isEmpty() || alt.isEmpty()) {
            return false;
        }
        Item origBlock = original.getItem();
        Item altBlock = alt.getItem();
        if(origBlock instanceof BlockItem && altBlock instanceof BlockItem){
            return isAlternateBlockFor(((BlockItem) origBlock).getBlock(), ((BlockItem) altBlock).getBlock());
        }
        return false;
    }

    public boolean isAlternateBlockFor(Block original, Block alt) {
        if (!Configs.Generic.ALLOW_ALTERNATE_BLOCKS.getBooleanValue()) {
            return false;
        }
        AlternateBlockGroup bg = altBlockGroupsMap.get(original);
        return bg != null && bg.enabled && bg.blockList.contains(alt);
    }
}
