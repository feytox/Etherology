package ru.feytox.etherology.registry.block;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import ru.feytox.etherology.block.beamer.BeamerBlock;
import ru.feytox.etherology.block.forestLantern.ForestLanternBlock;
import ru.feytox.etherology.block.peach.PeachSaplingBlock;
import ru.feytox.etherology.block.peach.WeepingPeachLogBlock;
import ru.feytox.etherology.block.signs.*;
import ru.feytox.etherology.block.thuja.ThujaBlock;
import ru.feytox.etherology.block.thuja.ThujaPlantBlock;
import ru.feytox.etherology.mixin.BlocksAccessor;
import ru.feytox.etherology.util.misc.EBlock;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Map;

import static net.minecraft.block.Blocks.*;

@UtilityClass
public class DecoBlocks {
    // various types registries
    private static final BlockSetType PEACH_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(EIdentifier.of("peach"));
    private static final BlockSetType SLITHERITE_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.STONE).register(EIdentifier.of("slitherite"));
    public static final WoodType PEACH_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK).register(EIdentifier.of("peach"), PEACH_TYPE);

    // peach wood
    public static final Block PEACH_LOG = register("peach_log", createLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN)).withItem();
    public static final Block STRIPPED_PEACH_LOG = register("stripped_peach_log", createLogBlock(MapColor.OAK_TAN, MapColor.OAK_TAN)).withItem();
    public static final Block PEACH_WOOD = register("peach_wood", new PillarBlock(copy(PEACH_LOG))).withItem();
    public static final Block STRIPPED_PEACH_WOOD = register("stripped_peach_wood", new PillarBlock(copy(STRIPPED_PEACH_LOG))).withItem();
    public static final Block WEEPING_PEACH_LOG = register("weeping_peach_log", new WeepingPeachLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN)).withItem(false);
    public static final Block PEACH_STAIRS = registerStairs("peach_stairs", ExtraBlocksRegistry.PEACH_PLANKS).withItem();
    public static final Block PEACH_SLAB = register("peach_slab", new SlabBlock(copy(OAK_SLAB))).withItem();
    public static final Block PEACH_BUTTON = register("peach_button", createWoodenButtonBlock(PEACH_TYPE)).withItem();
    public static final Block PEACH_DOOR = register("peach_door", new DoorBlock(PEACH_TYPE, copy(OAK_DOOR))).withoutItem();
    public static final Block PEACH_FENCE = register("peach_fence", new FenceBlock(copy(OAK_FENCE))).withItem();
    public static final Block PEACH_FENCE_GATE = register("peach_fence_gate", new FenceGateBlock(PEACH_WOOD_TYPE, copy(OAK_FENCE_GATE))).withItem();
    public static final Block PEACH_PRESSURE_PLATE = register("peach_pressure_plate", new PressurePlateBlock(PEACH_TYPE, copy(OAK_PRESSURE_PLATE))).withItem();
    public static final Block PEACH_SIGN = register("peach_sign", new EtherSignBlock(PEACH_WOOD_TYPE, copy(OAK_SIGN))).withoutItem();
    public static final Block PEACH_WALL_SIGN = register("peach_wall_sign", new EtherWallSignBlock(PEACH_WOOD_TYPE, copy(OAK_WALL_SIGN).dropsLike(PEACH_SIGN))).withoutItem();
    public static final Block PEACH_HANGING_SIGN = register("peach_hanging_sign", new EtherHangingSignBlock(PEACH_WOOD_TYPE, copy(OAK_HANGING_SIGN))).withoutItem();
    public static final Block PEACH_WALL_HANGING_SIGN = register("peach_wall_hanging_sign", new EtherWallHangingSignBlock(PEACH_WOOD_TYPE, copy(OAK_WALL_HANGING_SIGN).dropsLike(PEACH_HANGING_SIGN))).withoutItem();
    public static final Block PEACH_TRAPDOOR = register("peach_trapdoor", new TrapdoorBlock(PEACH_TYPE, copy(OAK_TRAPDOOR))).withItem();
    public static final Block PEACH_LEAVES = register("peach_leaves", createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES)).withItem(false);

    // slitherite
    public static final Block SLITHERITE = registerSimple("slitherite", copy(STONE)).withItem();
    public static final Block SLITHERITE_STAIRS = registerStairs("slitherite_stairs", SLITHERITE).withItem();
    public static final Block SLITHERITE_SLAB = register("slitherite_slab", new SlabBlock(copy(STONE_STAIRS))).withItem();
    public static final Block SLITHERITE_BUTTON = register("slitherite_button", createStoneButtonBlock()).withItem();
    public static final Block SLITHERITE_PRESSURE_PLATE = register("slitherite_pressure_plate", new PressurePlateBlock(SLITHERITE_TYPE, copy(STONE_PRESSURE_PLATE))).withItem();
    public static final Block SLITHERITE_WALL = register("slitherite_wall", new WallBlock(copy(STONE_BRICK_WALL))).withItem();

    // slitherite bricks
    public static final Block SLITHERITE_BRICKS = registerSimple("slitherite_bricks", copy(STONE_BRICKS)).withItem();
    public static final Block SLITHERITE_BRICK_SLAB = register("slitherite_brick_slab", new SlabBlock(copy(STONE_BRICKS))).withItem();
    public static final Block SLITHERITE_BRICK_STAIRS = registerStairs("slitherite_brick_stairs", SLITHERITE_BRICKS).withItem();
    public static final Block SLITHERITE_BRICK_WALL = register("slitherite_brick_wall", new WallBlock(copy(STONE_BRICK_WALL))).withItem();

    // chiseled slitherite
    public static final Block CHISELED_SLITHERITE_BRICKS = registerSimple("chiseled_slitherite_bricks", copy(CHISELED_STONE_BRICKS)).withItem();

    // cracked slitherite
    public static final Block CRACKED_SLITHERITE_BRICKS = registerSimple("cracked_slitherite_bricks", copy(CRACKED_STONE_BRICKS)).withItem();
    public static final Block CRACKED_SLITHERITE_BRICK_SLAB = register("cracked_slitherite_brick_slab", new SlabBlock(copy(STONE_SLAB))).withItem();
    public static final Block CRACKED_SLITHERITE_BRICK_STAIRS = registerStairs("cracked_slitherite_brick_stairs", CRACKED_SLITHERITE_BRICKS).withItem();

    // polished slitherite
    public static final Block POLISHED_SLITHERITE = registerSimple("polished_slitherite", copy(SMOOTH_STONE)).withItem();
    public static final Block POLISHED_SLITHERITE_SLAB = register("polished_slitherite_slab", new SlabBlock(copy(SMOOTH_STONE_SLAB))).withItem();
    public static final Block POLISHED_SLITHERITE_STAIRS = registerStairs("polished_slitherite_stairs", POLISHED_SLITHERITE).withItem();
    public static final Block POLISHED_SLITHERITE_WALL = register("polished_slitherite_wall", new WallBlock(copy(STONE_BRICK_WALL))).withItem();

    // plants
    public static final BeamerBlock BEAMER = (BeamerBlock) new BeamerBlock().registerBlock();
    public static final ThujaBlock THUJA = (ThujaBlock) new ThujaBlock().registerBlock();
    public static final ThujaPlantBlock THUJA_PLANT = (ThujaPlantBlock) new ThujaPlantBlock().registerBlock();
    public static final ForestLanternBlock FOREST_LANTERN = (ForestLanternBlock) new ForestLanternBlock().registerAll();
    public static final Block LIGHTELET = register("lightelet", new ShortPlantBlock(AbstractBlock.Settings.copy(SHORT_GRASS).emissiveLighting((a, b, c) -> true))).withItem(false);

    // saplings
    public static final PeachSaplingBlock PEACH_SAPLING = (PeachSaplingBlock) new PeachSaplingBlock().registerAll();

    // potted blocks
    public static final Block POTTED_BEAMER = register("potted_beamer", createFlowerPotBlock(BEAMER)).withoutItem();
    public static final Block POTTED_THUJA = register("potted_thuja", createFlowerPotBlock(THUJA)).withoutItem();
    public static final Block POTTED_PEACH_SAPLING = register("potted_peach_sapling", createFlowerPotBlock(PEACH_SAPLING)).withoutItem();

    // metals
    public static final Block AZEL_BLOCK = registerSimple("azel_block", copy(IRON_BLOCK).mapColor(MapColor.LAPIS_BLUE)).withItem();
    public static final Block ETHRIL_BLOCK = registerSimple("ethril_block", copy(GOLD_BLOCK)).withItem();
    public static final Block EBONY_BLOCK = registerSimple("ebony_block", copy(DIAMOND_BLOCK).mapColor(MapColor.ORANGE)).withItem();

    // attrahite
    public static final Block ATTRAHITE = registerSimple("attrahite", copy(STONE).sounds(BlockSoundGroup.GILDED_BLACKSTONE)).withItem(false);
    public static final Block ATTRAHITE_BRICKS = registerSimple("attrahite_bricks", copy(STONE_BRICKS)).withItem();
    public static final Block ATTRAHITE_BRICK_SLAB = register("attrahite_brick_slab", new SlabBlock(copy(STONE_SLAB))).withItem();
    public static final Block ATTRAHITE_BRICK_STAIRS = registerStairs("attrahite_brick_stairs", ATTRAHITE_BRICKS).withItem();

    // sign block entity
    public static final BlockEntityType<SignBlockEntity> ETHEROLOGY_SIGN = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("sign_block_entity"),
            BlockEntityType.Builder.create(SignBlockEntity::new, PEACH_SIGN, PEACH_WALL_SIGN).build()
    );
    public static final BlockEntityType<EtherHangingSignBlockEntity> ETHEROLOGY_HANGING_SIGN = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("hanging_sign_block_entity"),
            BlockEntityType.Builder.create(EtherHangingSignBlockEntity::new, PEACH_HANGING_SIGN, PEACH_WALL_HANGING_SIGN).build()
    );

    private static EBlock register(String id, Block block) {
        Block registredBlock = Registry.register(Registries.BLOCK, EIdentifier.of(id), block);
        return new EBlock(registredBlock);
    }

    private static EBlock registerSimple(String id, AbstractBlock.Settings settings) {
        return register(id, new Block(settings));
    }

    private static EBlock registerStairs(String id, Block baseBlock) {
        return register(id, BlocksAccessor.callCreateOldStairsBlock(baseBlock));
    }

    private static AbstractBlock.Settings copy(AbstractBlock original) {
        return AbstractBlock.Settings.copy(original);
    }

    public static void registerAll() {
        registerFlammables();
    }

    public static void registerFlammables() {
        FireBlock fireBlock = (FireBlock) FIRE;
        fireBlock.registerFlammableBlock(PEACH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_PEACH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(PEACH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_PEACH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(WEEPING_PEACH_LOG, 5, 5);

        fireBlock.registerFlammableBlock(ExtraBlocksRegistry.PEACH_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_BUTTON, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_DOOR, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_LEAVES, 60, 30);
        fireBlock.registerFlammableBlock(LIGHTELET, 60, 100);
    }

    // logs registry
    public static final Map<Block, Block> ETHER_LOGS = Map.of(
            PEACH_LOG, STRIPPED_PEACH_LOG,
            PEACH_WOOD, STRIPPED_PEACH_WOOD,
            WEEPING_PEACH_LOG, STRIPPED_PEACH_LOG
    );
}
