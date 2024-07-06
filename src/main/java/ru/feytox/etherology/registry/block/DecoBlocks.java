package ru.feytox.etherology.registry.block;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import ru.feytox.etherology.block.beamer.BeamerBlock;
import ru.feytox.etherology.block.forestLantern.ForestLanternBlock;
import ru.feytox.etherology.block.peach.PeachSaplingBlock;
import ru.feytox.etherology.block.peach.WeepingPeachLogBlock;
import ru.feytox.etherology.block.signs.EtherSignBlock;
import ru.feytox.etherology.block.signs.EtherSignBlockEntity;
import ru.feytox.etherology.block.signs.EtherWallSignBlock;
import ru.feytox.etherology.block.thuja.ThujaBlock;
import ru.feytox.etherology.block.thuja.ThujaPlantBlock;
import ru.feytox.etherology.util.misc.EBlock;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Map;

@UtilityClass
public class DecoBlocks {
    // various types registries
    private static final BlockSetType PEACH_TYPE = new BlockSetTypeBuilder().register(EIdentifier.of("peach"));
    public static final WoodType PEACH_SIGN_TYPE = new WoodTypeBuilder().register(EIdentifier.of("peach"), PEACH_TYPE);
    public static final BlockEntityType<EtherSignBlockEntity> ETHEROLOGY_SIGN;

    // peach wood
    public static final Block PEACH_LOG = register("peach_log", Blocks.createLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN)).withItem();
    public static final Block STRIPPED_PEACH_LOG = register("stripped_peach_log", Blocks.createLogBlock(MapColor.OAK_TAN, MapColor.OAK_TAN)).withItem();
    public static final Block PEACH_WOOD = register("peach_wood", new PillarBlock(copy(PEACH_LOG))).withItem();
    public static final Block STRIPPED_PEACH_WOOD = register("stripped_peach_wood", new PillarBlock(copy(STRIPPED_PEACH_LOG))).withItem();
    public static final Block WEEPING_PEACH_LOG = register("weeping_peach_log", new WeepingPeachLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN)).withItem(false);
    public static final Block PEACH_PLANKS = register("peach_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))).withItem();
    public static final Block PEACH_STAIRS = registerStairs("peach_stairs", PEACH_PLANKS).withItem();
    public static final Block PEACH_SLAB = registerSlab("peach_slab", PEACH_PLANKS).withItem();
    public static final Block PEACH_BUTTON = register("peach_button", Blocks.createWoodenButtonBlock()).withItem();
    public static final Block PEACH_DOOR = registerWoodenDoor("peach_door", PEACH_PLANKS, 3.0f).withoutItem();
    public static final Block PEACH_FENCE = register("peach_fence", new FenceBlock(copy(PEACH_PLANKS))).withItem();
    public static final Block PEACH_FENCE_GATE = register("peach_fence_gate", new FenceGateBlock(copy(PEACH_PLANKS), SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN)).withItem();
    public static final Block PEACH_PRESSURE_PLATE = registerWoodenPressurePlate("peach_pressure_plate", PEACH_PLANKS).withItem();
    public static final Block PEACH_SIGN = register("peach_sign", new SignBlock(PEACH_SIGN_TYPE, copy(PEACH_PLANKS).noCollision().strength(1.0f))).withoutItem();
    public static final Block PEACH_WALL_SIGN = register("peach_wall_sign", new WallSignBlock(PEACH_SIGN_TYPE, copy(PEACH_PLANKS).noCollision().strength(1.0f))).withItem(false);
    public static final Block PEACH_TRAPDOOR = register("peach_trapdoor", new TrapdoorBlock(PEACH_TYPE, copy(PEACH_PLANKS)
            .strength(3.0f).nonOpaque().allowsSpawning(DecoBlocks::never))).withItem();
    public static final Block PEACH_LEAVES = register("peach_leaves", Blocks.createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES)).withItem(false);

    // slitherite
    public static final Block SLITHERITE = registerSimple("slitherite", copy(Blocks.STONE)).withItem();
    public static final Block SLITHERITE_STAIRS = registerStairs("slitherite_stairs", SLITHERITE).withItem();
    public static final Block SLITHERITE_SLAB = registerSlab("slitherite_slab", SLITHERITE).withItem();
    public static final Block SLITHERITE_BUTTON = register("slitherite_button", Blocks.createStoneButtonBlock()).withItem();
    public static final Block SLITHERITE_PRESSURE_PLATE = registerStonePressurePlate("slitherite_pressure_plate", SLITHERITE).withItem();
    public static final Block SLITHERITE_WALL = registerWall("slitherite_wall", SLITHERITE).withItem();

    // slitherite bricks
    public static final Block SLITHERITE_BRICKS = registerSimple("slitherite_bricks", copy(Blocks.STONE_BRICKS)).withItem();
    public static final Block SLITHERITE_BRICK_SLAB = registerSlab("slitherite_brick_slab", SLITHERITE_BRICKS).withItem();
    public static final Block SLITHERITE_BRICK_STAIRS = registerStairs("slitherite_brick_stairs", SLITHERITE_BRICKS).withItem();
    public static final Block SLITHERITE_BRICK_WALL = registerWall("slitherite_brick_wall", SLITHERITE_BRICKS).withItem();

    // chiseled slitherite
    public static final Block CHISELED_SLITHERITE_BRICKS = registerSimple("chiseled_slitherite_bricks", copy(Blocks.CHISELED_STONE_BRICKS)).withItem();

    // cracked slitherite
    public static final Block CRACKED_SLITHERITE_BRICKS = registerSimple("cracked_slitherite_bricks", copy(Blocks.CRACKED_STONE_BRICKS)).withItem();
    public static final Block CRACKED_SLITHERITE_BRICK_SLAB = registerSlab("cracked_slitherite_brick_slab", CRACKED_SLITHERITE_BRICKS).withItem();
    public static final Block CRACKED_SLITHERITE_BRICK_STAIRS = registerStairs("cracked_slitherite_brick_stairs", CRACKED_SLITHERITE_BRICKS).withItem();

    // polished slitherite
    public static final Block POLISHED_SLITHERITE = registerSimple("polished_slitherite", copy(Blocks.SMOOTH_STONE)).withItem();
    public static final Block POLISHED_SLITHERITE_SLAB = registerSlab("polished_slitherite_slab", POLISHED_SLITHERITE).withItem();
    public static final Block POLISHED_SLITHERITE_STAIRS = registerStairs("polished_slitherite_stairs", POLISHED_SLITHERITE).withItem();
    public static final Block POLISHED_SLITHERITE_WALL = registerWall("polished_slitherite_wall", POLISHED_SLITHERITE).withItem();

    // plants
    public static final BeamerBlock BEAMER = (BeamerBlock) new BeamerBlock().registerBlock();
    public static final ThujaBlock THUJA = (ThujaBlock) new ThujaBlock().registerBlock();
    public static final ThujaPlantBlock THUJA_PLANT = (ThujaPlantBlock) new ThujaPlantBlock().registerBlock();
    public static final ForestLanternBlock FOREST_LANTERN = (ForestLanternBlock) new ForestLanternBlock().registerAll();
    public static final Block LIGHTELET = register("lightelet", new ShortPlantBlock(AbstractBlock.Settings.copy(Blocks.SHORT_GRASS).emissiveLighting((a, b, c) -> true))).withItem(false);

    // saplings
    public static final PeachSaplingBlock PEACH_SAPLING = (PeachSaplingBlock) new PeachSaplingBlock().registerAll();

    // potted blocks
    public static final Block POTTED_BEAMER = register("potted_beamer", new FlowerPotBlock(BEAMER, AbstractBlock.Settings.create(Material.DECORATION).breakInstantly().nonOpaque())).withoutItem();
    public static final Block POTTED_THUJA = register("potted_thuja", new FlowerPotBlock(THUJA, AbstractBlock.Settings.create(Material.DECORATION).breakInstantly().nonOpaque())).withoutItem();
    public static final Block POTTED_PEACH_SAPLING = register("potted_peach_sapling", new FlowerPotBlock(PEACH_SAPLING, AbstractBlock.Settings.create(Material.DECORATION).breakInstantly().nonOpaque())).withoutItem();

    // metals
    public static final Block AZEL_BLOCK = registerSimple("azel_block", copy(Blocks.IRON_BLOCK).mapColor(MapColor.LAPIS_BLUE)).withItem();
    public static final Block ETHRIL_BLOCK = registerSimple("ethril_block", copy(Blocks.GOLD_BLOCK)).withItem();
    public static final Block EBONY_BLOCK = registerSimple("ebony_block", copy(Blocks.DIAMOND_BLOCK).mapColor(MapColor.ORANGE)).withItem();

    // attrahite
    public static final Block ATTRAHITE = registerSimple("attrahite", copy(Blocks.STONE).sounds(BlockSoundGroup.GILDED_BLACKSTONE)).withItem(false);
    public static final Block ATTRAHITE_BRICKS = registerSimple("attrahite_bricks", copy(Blocks.STONE_BRICKS)).withItem();
    public static final Block ATTRAHITE_BRICK_SLAB = registerSlab("attrahite_brick_slab", ATTRAHITE_BRICKS).withItem();
    public static final Block ATTRAHITE_BRICK_STAIRS = registerStairs("attrahite_brick_stairs", ATTRAHITE_BRICKS).withItem();


    private static EBlock register(String id, Block block) {
        Block registredBlock = Registry.register(Registries.BLOCK, EIdentifier.of(id), block);
        return new EBlock(registredBlock);
    }

    private static EBlock registerSimple(String id, AbstractBlock.Settings settings) {
        return register(id, new Block(settings));
    }

    private static EBlock registerSlab(String id, Block originalBlock) {
        return register(id, new SlabBlock(copy(originalBlock)));
    }

    public static EBlock registerWall(String id, Block originalBlock) {
        return register(id, new WallBlock(copy(originalBlock)));
    }

    private static EBlock registerWoodenDoor(String id, Block originalBlock, float strength) {
        return registerDoor(id, originalBlock, strength, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE);
    }

    private static EBlock registerWoodenPressurePlate(String id, Block originalBlock) {
        return register(id, new PressurePlateBlock(
                PressurePlateBlock.ActivationRule.EVERYTHING, copy(originalBlock).noCollision().strength(0.5f),
                SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON));
    }

    private static EBlock registerStonePressurePlate(String id, Block originalBlock) {
        return register(id, new PressurePlateBlock(
                PressurePlateBlock.ActivationRule.MOBS, copy(originalBlock).noCollision().strength(0.5f),
                SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON));
    }

    private static EBlock registerDoor(String id, Block originalBlock, float strength, SoundEvent openSound, SoundEvent closeSound) {
        return register(id, new DoorBlock(copy(originalBlock).strength(strength).nonOpaque(), closeSound, openSound));
    }

    private static EBlock registerStairs(String id, Block originalBlock) {
        return register(id, new StairsBlock(originalBlock.getDefaultState(), copy(originalBlock)));
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static AbstractBlock.Settings copy(AbstractBlock original) {
        return FabricBlockSettings.copy(original);
    }

    public static void registerAll() {
        registerFlammables();
    }

    public static void registerFlammables() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.registerFlammableBlock(PEACH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_PEACH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(PEACH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_PEACH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(WEEPING_PEACH_LOG, 5, 5);

        fireBlock.registerFlammableBlock(PEACH_PLANKS, 5, 20);
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

    static {
        ETHEROLOGY_SIGN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                EIdentifier.of("sign"),
                FabricBlockEntityTypeBuilder.create(EtherSignBlockEntity::new, PEACH_SIGN, PEACH_WALL_SIGN).build()
        );
    }
}
