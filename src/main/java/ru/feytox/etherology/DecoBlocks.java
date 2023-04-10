package ru.feytox.etherology;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import ru.feytox.etherology.blocks.signs.EtherSignBlock;
import ru.feytox.etherology.blocks.signs.EtherSignBlockEntity;
import ru.feytox.etherology.blocks.signs.EtherSignType;
import ru.feytox.etherology.blocks.signs.EtherWallSignBlock;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Map;

public class DecoBlocks {
    // various types registries
    public static final SignType PEACH_SIGN_TYPE = SignType.register(new EtherSignType("peach"));
    public static final BlockEntityType<EtherSignBlockEntity> ETHEROLOGY_SIGN;

    // peach wood
    public static final Block PEACH_LOG = register("peach_log", Blocks.createLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN));
    public static final Block STRIPPED_PEACH_LOG = register("stripped_peach_log", Blocks.createLogBlock(MapColor.OAK_TAN, MapColor.OAK_TAN));
    public static final Block PEACH_WOOD = register("peach_wood", new PillarBlock(copy(PEACH_LOG)));
    public static final Block STRIPPED_PEACH_WOOD = register("stripped_peach_wood", new PillarBlock(copy(STRIPPED_PEACH_LOG)));
    public static final Block PEACH_PLANKS = register("peach_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block PEACH_STAIRS = registerStairs("peach_stairs", PEACH_PLANKS);
    public static final Block PEACH_SLAB = registerSlab("peach_slab", PEACH_PLANKS);
    public static final Block PEACH_BUTTON = register("peach_button", Blocks.createWoodenButtonBlock());
    public static final Block PEACH_DOOR = registerWoodenDoor("peach_door", PEACH_PLANKS, 3.0f);
    public static final Block PEACH_FENCE = register("peach_fence", new FenceBlock(copy(PEACH_PLANKS)));
    public static final Block PEACH_FENCE_GATE = register("peach_fence_gate", new FenceGateBlock(copy(PEACH_PLANKS), SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN));
    public static final Block PEACH_PRESSURE_PLATE = register("peach_pressure_plate", new PressurePlateBlock(
            PressurePlateBlock.ActivationRule.EVERYTHING, copy(PEACH_PLANKS).noCollision().strength(0.5f), SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON));
    public static final Block PEACH_SIGN = register("peach_sign", new EtherSignBlock(copy(PEACH_PLANKS).noCollision().strength(1.0f), PEACH_SIGN_TYPE));
    public static final Block PEACH_WALL_SIGN = register("peach_wall_sign", new EtherWallSignBlock(copy(PEACH_PLANKS).noCollision().strength(1.0f), PEACH_SIGN_TYPE));
    public static final Block PEACH_TRAPDOOR = register("peach_trapdoor", new TrapdoorBlock(copy(PEACH_PLANKS)
            .strength(3.0f).nonOpaque().allowsSpawning(DecoBlocks::never), SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN));


    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new EIdentifier(id), block);
    }

    private static Block registerSlab(String id, Block originalBlock) {
        return register(id, new SlabBlock(copy(originalBlock)));
    }

    private static Block registerWoodenDoor(String id, Block originalBlock, float strength) {
        return registerDoor(id, originalBlock, strength, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN);
    }

    private static Block registerDoor(String id, Block originalBlock, float strength, SoundEvent openSound, SoundEvent closeSound) {
        return register(id, new DoorBlock(copy(originalBlock).strength(strength).nonOpaque(), closeSound, openSound));
    }

    private static Block registerStairs(String id, Block originalBlock) {
        return register(id, new StairsBlock(originalBlock.getDefaultState(), copy(originalBlock)));
    }

    private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static AbstractBlock.Settings copy(AbstractBlock original) {
        return FabricBlockSettings.copyOf(original);
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

        fireBlock.registerFlammableBlock(PEACH_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_BUTTON, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_DOOR, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(PEACH_FENCE_GATE, 5, 20);
    }

    // logs registry
    public static final Map<Block, Block> ETHER_LOGS = Map.of(
            PEACH_LOG, STRIPPED_PEACH_LOG,
            PEACH_WOOD, STRIPPED_PEACH_WOOD
    );

    static {
        ETHEROLOGY_SIGN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new EIdentifier("sign"),
                FabricBlockEntityTypeBuilder.create(EtherSignBlockEntity::new, PEACH_SIGN, PEACH_WALL_SIGN).build()
        );
    }
}
