package com.ambitioncraft.willow.litematicahelpers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class AlternateBlockGroup {
    public String name;
    public ImmutableSet<Block> blockList = ImmutableSet.of();
    public Boolean ignoreRotation = false;
    public Boolean enabled = true;
    private static AlternateBlockGroup from(String name, Boolean ignoreRotation, ImmutableSet<Block> blockList,boolean enabled) {
        AlternateBlockGroup bg = new AlternateBlockGroup();
        bg.name = name;
        bg.ignoreRotation = ignoreRotation;
        bg.blockList = blockList;
        bg.enabled = enabled;
        return bg;
    }

    public static AlternateBlockGroup GLAZED_TERRACOTTA = AlternateBlockGroup.from(
            "Glazed Terracotta",
            true,
            ImmutableSet.of(
                    Blocks.BLACK_GLAZED_TERRACOTTA,
                    Blocks.BLUE_GLAZED_TERRACOTTA,
                    Blocks.BROWN_GLAZED_TERRACOTTA,
                    Blocks.CYAN_GLAZED_TERRACOTTA,
                    Blocks.GRAY_GLAZED_TERRACOTTA,
                    Blocks.GREEN_GLAZED_TERRACOTTA,
                    Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
                    Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
                    Blocks.LIME_GLAZED_TERRACOTTA,
                    Blocks.MAGENTA_GLAZED_TERRACOTTA,
                    Blocks.ORANGE_GLAZED_TERRACOTTA,
                    Blocks.PINK_GLAZED_TERRACOTTA,
                    Blocks.PURPLE_GLAZED_TERRACOTTA,
                    Blocks.RED_GLAZED_TERRACOTTA,
                    Blocks.WHITE_GLAZED_TERRACOTTA,
                    Blocks.YELLOW_GLAZED_TERRACOTTA
            ),
            true
    );

    public static AlternateBlockGroup GLASS = AlternateBlockGroup.from(
            "Glass",
            false,
            ImmutableSet.of(
                    Blocks.GLASS,
                    Blocks.BLACK_STAINED_GLASS,
                    Blocks.BLUE_STAINED_GLASS,
                    Blocks.BROWN_STAINED_GLASS,
                    Blocks.CYAN_STAINED_GLASS,
                    Blocks.GRAY_STAINED_GLASS,
                    Blocks.GREEN_STAINED_GLASS,
                    Blocks.LIGHT_BLUE_STAINED_GLASS,
                    Blocks.LIGHT_GRAY_STAINED_GLASS,
                    Blocks.LIME_STAINED_GLASS,
                    Blocks.MAGENTA_STAINED_GLASS,
                    Blocks.ORANGE_STAINED_GLASS,
                    Blocks.PINK_STAINED_GLASS,
                    Blocks.PURPLE_STAINED_GLASS,
                    Blocks.RED_STAINED_GLASS,
                    Blocks.WHITE_STAINED_GLASS,
                    Blocks.YELLOW_STAINED_GLASS
            ),
            true
    );

    public static AlternateBlockGroup GLASS_PANES = AlternateBlockGroup.from(
            "Glass Panes",
            false,
            ImmutableSet.of(
                    Blocks.GLASS_PANE,
                    Blocks.BLACK_STAINED_GLASS_PANE,
                    Blocks.BLUE_STAINED_GLASS_PANE,
                    Blocks.BROWN_STAINED_GLASS_PANE,
                    Blocks.CYAN_STAINED_GLASS_PANE,
                    Blocks.GRAY_STAINED_GLASS_PANE,
                    Blocks.GREEN_STAINED_GLASS_PANE,
                    Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                    Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                    Blocks.LIME_STAINED_GLASS_PANE,
                    Blocks.MAGENTA_STAINED_GLASS_PANE,
                    Blocks.ORANGE_STAINED_GLASS_PANE,
                    Blocks.PINK_STAINED_GLASS_PANE,
                    Blocks.PURPLE_STAINED_GLASS_PANE,
                    Blocks.RED_STAINED_GLASS_PANE,
                    Blocks.WHITE_STAINED_GLASS_PANE,
                    Blocks.YELLOW_STAINED_GLASS_PANE
            ),
            true
    );

    public static AlternateBlockGroup CORAL_FANS = AlternateBlockGroup.from(
            "Coral Fans",
            true,
            ImmutableSet.of(
                    Blocks.BRAIN_CORAL_FAN,
                    Blocks.BUBBLE_CORAL_FAN,
                    Blocks.DEAD_BRAIN_CORAL_FAN,
                    Blocks.FIRE_CORAL_FAN,
                    Blocks.HORN_CORAL_FAN,
                    Blocks.TUBE_CORAL_FAN,
                    Blocks.DEAD_BRAIN_CORAL_FAN,
                    Blocks.DEAD_BUBBLE_CORAL_FAN,
                    Blocks.DEAD_FIRE_CORAL_FAN,
                    Blocks.DEAD_HORN_CORAL_FAN,
                    Blocks.DEAD_TUBE_CORAL_FAN
            ),
            true
    );

    public static AlternateBlockGroup CORAL_WALL_FANS = AlternateBlockGroup.from(
            "Coral Wall Fans",
            true,
            ImmutableSet.of(
                    Blocks.BRAIN_CORAL_WALL_FAN,
                    Blocks.BUBBLE_CORAL_WALL_FAN,
                    Blocks.FIRE_CORAL_WALL_FAN,
                    Blocks.HORN_CORAL_WALL_FAN,
                    Blocks.TUBE_CORAL_WALL_FAN,
                    Blocks.DEAD_BRAIN_CORAL_WALL_FAN,
                    Blocks.DEAD_BUBBLE_CORAL_WALL_FAN,
                    Blocks.DEAD_FIRE_CORAL_WALL_FAN,
                    Blocks.DEAD_HORN_CORAL_WALL_FAN,
                    Blocks.DEAD_TUBE_CORAL_WALL_FAN
            ),
            true
    );

    public static AlternateBlockGroup FENCE_GATES = AlternateBlockGroup.from(
            "Fence Gates",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_FENCE_GATE,
                    Blocks.BIRCH_FENCE_GATE,
                    Blocks.CRIMSON_FENCE_GATE,
                    Blocks.DARK_OAK_FENCE_GATE,
                    Blocks.OAK_FENCE_GATE,
                    Blocks.JUNGLE_FENCE_GATE,
                    Blocks.SPRUCE_FENCE_GATE,
                    Blocks.WARPED_FENCE_GATE
            ),
            true
    );

    public static AlternateBlockGroup WOODEN_FENCES = AlternateBlockGroup.from(
            "Wooden FENCES",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_FENCE,
                    Blocks.BIRCH_FENCE,
                    Blocks.CRIMSON_FENCE,
                    Blocks.DARK_OAK_FENCE,
                    Blocks.OAK_FENCE,
                    Blocks.JUNGLE_FENCE,
                    Blocks.SPRUCE_FENCE,
                    Blocks.WARPED_FENCE
            ),
            true
    );

    public static AlternateBlockGroup TRAPDOORS = AlternateBlockGroup.from(
            "Trapdoors",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_TRAPDOOR,
                    Blocks.BIRCH_TRAPDOOR,
                    Blocks.CRIMSON_TRAPDOOR,
                    Blocks.DARK_OAK_TRAPDOOR,
                    Blocks.OAK_TRAPDOOR,
                    Blocks.JUNGLE_TRAPDOOR,
                    Blocks.SPRUCE_TRAPDOOR,
                    Blocks.WARPED_TRAPDOOR
            ),
            true
    );

    public static AlternateBlockGroup WOOL = AlternateBlockGroup.from(
            "Wool",
            false,
            ImmutableSet.of(
                    Blocks.BLACK_WOOL,
                    Blocks.BLUE_WOOL,
                    Blocks.BROWN_WOOL,
                    Blocks.CYAN_WOOL,
                    Blocks.GRAY_WOOL,
                    Blocks.GREEN_WOOL,
                    Blocks.LIGHT_BLUE_WOOL,
                    Blocks.LIGHT_GRAY_WOOL,
                    Blocks.LIME_WOOL,
                    Blocks.MAGENTA_WOOL,
                    Blocks.ORANGE_WOOL,
                    Blocks.PINK_WOOL,
                    Blocks.PURPLE_WOOL,
                    Blocks.RED_WOOL,
                    Blocks.WHITE_WOOL,
                    Blocks.YELLOW_WOOL
            ),
            true
    );


    public static AlternateBlockGroup CARPETS = AlternateBlockGroup.from(
            "Carpet",
            false,
            ImmutableSet.of(
                    Blocks.BLACK_CARPET,
                    Blocks.BLUE_CARPET,
                    Blocks.BROWN_CARPET,
                    Blocks.CYAN_CARPET,
                    Blocks.GRAY_CARPET,
                    Blocks.GREEN_CARPET,
                    Blocks.LIGHT_BLUE_CARPET,
                    Blocks.LIGHT_GRAY_CARPET,
                    Blocks.LIME_CARPET,
                    Blocks.MAGENTA_CARPET,
                    Blocks.ORANGE_CARPET,
                    Blocks.PINK_CARPET,
                    Blocks.PURPLE_CARPET,
                    Blocks.RED_CARPET,
                    Blocks.WHITE_CARPET,
                    Blocks.YELLOW_CARPET
            ),
            true
    );

    public static AlternateBlockGroup SIGNS = AlternateBlockGroup.from(
            "Signs",
            true,
            ImmutableSet.of(
                    Blocks.ACACIA_SIGN,
                    Blocks.BIRCH_SIGN,
                    Blocks.CRIMSON_SIGN,
                    Blocks.DARK_OAK_SIGN,
                    Blocks.OAK_SIGN,
                    Blocks.JUNGLE_SIGN,
                    Blocks.SPRUCE_SIGN,
                    Blocks.WARPED_SIGN
            ),
            true
    );

    public static AlternateBlockGroup WALL_SIGNS = AlternateBlockGroup.from(
            "Wall Signs",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_WALL_SIGN,
                    Blocks.BIRCH_WALL_SIGN,
                    Blocks.CRIMSON_WALL_SIGN,
                    Blocks.DARK_OAK_WALL_SIGN,
                    Blocks.OAK_WALL_SIGN,
                    Blocks.JUNGLE_WALL_SIGN,
                    Blocks.SPRUCE_WALL_SIGN,
                    Blocks.WARPED_WALL_SIGN
            ),
            true
    );


    public static AlternateBlockGroup PLANKS = AlternateBlockGroup.from(
            "PLANKS",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_PLANKS,
                    Blocks.BIRCH_PLANKS,
                    Blocks.CRIMSON_PLANKS,
                    Blocks.DARK_OAK_PLANKS,
                    Blocks.OAK_PLANKS,
                    Blocks.JUNGLE_PLANKS,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.WARPED_PLANKS
            ),
            true
    );

    public static AlternateBlockGroup WOOD_SLABS = AlternateBlockGroup.from(
            "Wood Slabs",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_SLAB,
                    Blocks.BIRCH_SLAB,
                    Blocks.CRIMSON_SLAB,
                    Blocks.DARK_OAK_SLAB,
                    Blocks.OAK_SLAB,
                    Blocks.JUNGLE_SLAB,
                    Blocks.SPRUCE_SLAB,
                    Blocks.WARPED_SLAB
            ),
            true
    );

    public static AlternateBlockGroup STONE_SLABS = AlternateBlockGroup.from(
            "Wood Slabs",
            false,
            ImmutableSet.of(
                    Blocks.ANDESITE_SLAB,
                    Blocks.BLACKSTONE_SLAB,
                    Blocks.BRICK_SLAB,
                    Blocks.COBBLESTONE_SLAB,
                    Blocks.DIORITE_SLAB,
                    Blocks.GRANITE_SLAB,
                    Blocks.MOSSY_COBBLESTONE_SLAB,
                    Blocks.MOSSY_STONE_BRICK_SLAB,
                    Blocks.NETHER_BRICK_SLAB,
                    Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
                    Blocks.POLISHED_BLACKSTONE_SLAB,
                    Blocks.PRISMARINE_SLAB,
                    Blocks.RED_NETHER_BRICK_SLAB,
                    Blocks.STONE_BRICK_SLAB,
                    Blocks.POLISHED_ANDESITE_SLAB,
                    Blocks.POLISHED_DIORITE_SLAB,
                    Blocks.POLISHED_GRANITE_SLAB,
                    Blocks.STONE_SLAB,
                    Blocks.PURPUR_SLAB
            ),
            true
    );

    public static AlternateBlockGroup STONE = AlternateBlockGroup.from(
            "Stone",
            false,
            ImmutableSet.of(
                    Blocks.ANDESITE,
                    Blocks.BLACKSTONE,
                    Blocks.BRICKS,
                    Blocks.COBBLESTONE,
                    Blocks.DIORITE,
                    Blocks.GRANITE,
                    Blocks.MOSSY_COBBLESTONE,
                    Blocks.MOSSY_STONE_BRICKS,
                    Blocks.NETHER_BRICKS,
                    Blocks.POLISHED_BLACKSTONE_BRICKS,
                    Blocks.POLISHED_BLACKSTONE,
                    Blocks.POLISHED_GRANITE,
                    Blocks.POLISHED_DIORITE,
                    Blocks.POLISHED_ANDESITE,
                    Blocks.PRISMARINE,
                    Blocks.RED_NETHER_BRICKS,
                    Blocks.STONE,
                    Blocks.STONE_BRICKS
            ),
            true
    );

    public static AlternateBlockGroup CONCRETE = AlternateBlockGroup.from(
            "CONCRETE",
            false,
            ImmutableSet.of(
                    Blocks.BLACK_CONCRETE,
                    Blocks.BLUE_CONCRETE,
                    Blocks.BROWN_CONCRETE,
                    Blocks.CYAN_CONCRETE,
                    Blocks.GRAY_CONCRETE,
                    Blocks.GREEN_CONCRETE,
                    Blocks.LIGHT_BLUE_CONCRETE,
                    Blocks.LIGHT_GRAY_CONCRETE,
                    Blocks.LIME_CONCRETE,
                    Blocks.MAGENTA_CONCRETE,
                    Blocks.ORANGE_CONCRETE,
                    Blocks.PINK_CONCRETE,
                    Blocks.PURPLE_CONCRETE,
                    Blocks.RED_CONCRETE,
                    Blocks.WHITE_CONCRETE,
                    Blocks.YELLOW_CONCRETE
            ),
            true
    );

    public static AlternateBlockGroup CONCRETE_POWDER = AlternateBlockGroup.from(
            "Concrete Powder",
            false,
            ImmutableSet.of(
                    Blocks.BLACK_CONCRETE_POWDER,
                    Blocks.BLUE_CONCRETE_POWDER,
                    Blocks.BROWN_CONCRETE_POWDER,
                    Blocks.CYAN_CONCRETE_POWDER,
                    Blocks.GRAY_CONCRETE_POWDER,
                    Blocks.GREEN_CONCRETE_POWDER,
                    Blocks.LIGHT_BLUE_CONCRETE_POWDER,
                    Blocks.LIGHT_GRAY_CONCRETE_POWDER,
                    Blocks.LIME_CONCRETE_POWDER,
                    Blocks.MAGENTA_CONCRETE_POWDER,
                    Blocks.ORANGE_CONCRETE_POWDER,
                    Blocks.PINK_CONCRETE_POWDER,
                    Blocks.PURPLE_CONCRETE_POWDER,
                    Blocks.RED_CONCRETE_POWDER,
                    Blocks.WHITE_CONCRETE_POWDER,
                    Blocks.YELLOW_CONCRETE_POWDER
            ),
            true
    );

    public static AlternateBlockGroup TERRACOTTA = AlternateBlockGroup.from(
            "CONCRETE",
            false,
            ImmutableSet.of(
                    Blocks.TERRACOTTA,
                    Blocks.BLACK_TERRACOTTA,
                    Blocks.BLUE_TERRACOTTA,
                    Blocks.BROWN_TERRACOTTA,
                    Blocks.CYAN_TERRACOTTA,
                    Blocks.GRAY_TERRACOTTA,
                    Blocks.GREEN_TERRACOTTA,
                    Blocks.LIGHT_BLUE_TERRACOTTA,
                    Blocks.LIGHT_GRAY_TERRACOTTA,
                    Blocks.LIME_TERRACOTTA,
                    Blocks.MAGENTA_TERRACOTTA,
                    Blocks.ORANGE_TERRACOTTA,
                    Blocks.PINK_TERRACOTTA,
                    Blocks.PURPLE_TERRACOTTA,
                    Blocks.RED_TERRACOTTA,
                    Blocks.WHITE_TERRACOTTA,
                    Blocks.YELLOW_TERRACOTTA
            ),
            true
    );

    public static AlternateBlockGroup WALLS = AlternateBlockGroup.from(
            "Walls",
            false,
            ImmutableSet.of(
                    Blocks.ANDESITE_WALL,
                    Blocks.BLACKSTONE_WALL,
                    Blocks.BRICK_WALL,
                    Blocks.COBBLESTONE_WALL,
                    Blocks.DIORITE_WALL,
                    Blocks.GRANITE_WALL,
                    Blocks.MOSSY_COBBLESTONE_WALL,
                    Blocks.MOSSY_STONE_BRICK_WALL,
                    Blocks.NETHER_BRICK_WALL,
                    Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
                    Blocks.POLISHED_BLACKSTONE_WALL,
                    Blocks.PRISMARINE_WALL,
                    Blocks.RED_NETHER_BRICK_WALL,
                    Blocks.STONE_BRICK_WALL
            ),
            true
    );


    public static AlternateBlockGroup LEAVES = AlternateBlockGroup.from(
            "Leaves",
            false,
            ImmutableSet.of(
                    Blocks.ACACIA_LEAVES,
                    Blocks.BIRCH_LEAVES,
                    Blocks.DARK_OAK_LEAVES,
                    Blocks.OAK_LEAVES,
                    Blocks.JUNGLE_LEAVES,
                    Blocks.SPRUCE_LEAVES
            ),
            true
    );

    public static AlternateBlockGroup SAND = AlternateBlockGroup.from(
            "SAND",
            false,
            ImmutableSet.of(
                    Blocks.SAND,
                    Blocks.RED_SAND
            ),
            true
    );



}
