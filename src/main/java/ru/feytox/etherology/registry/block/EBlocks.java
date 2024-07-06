package ru.feytox.etherology.registry.block;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.block.armillary.ArmillaryMatrixBlock;
import ru.feytox.etherology.block.armillary.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlock;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity;
import ru.feytox.etherology.block.closet.ClosetSlabBlock;
import ru.feytox.etherology.block.crate.CrateBlock;
import ru.feytox.etherology.block.crate.CrateBlockEntity;
import ru.feytox.etherology.block.empowerTable.EmpowerTableBlock;
import ru.feytox.etherology.block.empowerTable.EmpowerTableBlockEntity;
import ru.feytox.etherology.block.essenceDetector.EssenceDetectorBlock;
import ru.feytox.etherology.block.essenceDetector.EssenceDetectorBlockEntity;
import ru.feytox.etherology.block.etherealChannel.EtherealChannel;
import ru.feytox.etherology.block.etherealChannel.EtherealChannelBlockEntity;
import ru.feytox.etherology.block.etherealChannel.EtherealChannelCase;
import ru.feytox.etherology.block.etherealFork.EtherealForkBlock;
import ru.feytox.etherology.block.etherealFork.EtherealForkBlockEntity;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnace;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceBlockEntity;
import ru.feytox.etherology.block.etherealGenerators.metronome.EtherealMetronomeBlock;
import ru.feytox.etherology.block.etherealGenerators.metronome.EtherealMetronomeBlockEntity;
import ru.feytox.etherology.block.etherealGenerators.spinner.EtherealSpinnerBlock;
import ru.feytox.etherology.block.etherealGenerators.spinner.EtherealSpinnerBlockEntity;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketBlock;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketBlockEntity;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageBlock;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageBlockEntity;
import ru.feytox.etherology.block.furniture.FurSlabBlock;
import ru.feytox.etherology.block.furniture.FurSlabBlockEntity;
import ru.feytox.etherology.block.inventorTable.InventorTable;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.block.jewelryTable.JewelryTable;
import ru.feytox.etherology.block.jug.JugBlock;
import ru.feytox.etherology.block.jug.JugBlockEntity;
import ru.feytox.etherology.block.jug.JugType;
import ru.feytox.etherology.block.levitator.LevitatorBlock;
import ru.feytox.etherology.block.levitator.LevitatorBlockEntity;
import ru.feytox.etherology.block.pedestal.PedestalBlock;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.block.samovar.SamovarBlock;
import ru.feytox.etherology.block.sedimentary.SedimentaryStone;
import ru.feytox.etherology.block.sedimentary.SedimentaryStoneBlockEntity;
import ru.feytox.etherology.block.shelf.ShelfSlabBlock;
import ru.feytox.etherology.block.spill_barrel.SpillBarrelBlock;
import ru.feytox.etherology.block.spill_barrel.SpillBarrelBlockEntity;
import ru.feytox.etherology.block.tuningFork.TuningFork;
import ru.feytox.etherology.block.tuningFork.TuningForkBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EBlocks {
    public static final BrewingCauldronBlock BREWING_CAULDRON = (BrewingCauldronBlock) new BrewingCauldronBlock().registerAll();
    public static final BlockEntityType<BrewingCauldronBlockEntity> BREWING_CAULDRON_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("brewing_cauldron_block_entity"),
            FabricBlockEntityTypeBuilder.create(BrewingCauldronBlockEntity::new, BREWING_CAULDRON).build()
    );

    public static final PedestalBlock PEDESTAL_BLOCK = (PedestalBlock) new PedestalBlock().registerAll();
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("pedestal_block_entity"),
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, PEDESTAL_BLOCK).build()
    );

    public static final SedimentaryStone SEDIMENTARY_BLOCK = (SedimentaryStone) new SedimentaryStone().registerAll();
    public static final BlockEntityType<SedimentaryStoneBlockEntity> SEDIMENTARY_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("sedimentary_stone_block_entity"),
            FabricBlockEntityTypeBuilder.create(SedimentaryStoneBlockEntity::new, SEDIMENTARY_BLOCK).build()
    );

    public static final EssenceDetectorBlock ESSENCE_DETECTOR_BLOCK = (EssenceDetectorBlock) new EssenceDetectorBlock().registerAll();
    public static final BlockEntityType<EssenceDetectorBlockEntity> ESSENCE_DETECTOR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("essence_detector_block_entity"),
            FabricBlockEntityTypeBuilder.create(EssenceDetectorBlockEntity::new, ESSENCE_DETECTOR_BLOCK).build()
    );

    public static final FurSlabBlock FURNITURE_SLAB = (FurSlabBlock) new FurSlabBlock().registerAll();
    public static final ClosetSlabBlock CLOSET_SLAB = (ClosetSlabBlock) new ClosetSlabBlock().registerAll();
    public static final ShelfSlabBlock SHELF_SLAB = (ShelfSlabBlock) new ShelfSlabBlock().registerAll();
    public static final BlockEntityType<FurSlabBlockEntity> FURNITURE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("furniture_block_entity"),
            FabricBlockEntityTypeBuilder.create(FurSlabBlockEntity::new, FURNITURE_SLAB, CLOSET_SLAB, SHELF_SLAB).build()
    );

    public static final EtherealStorageBlock ETHEREAL_STORAGE = (EtherealStorageBlock) new EtherealStorageBlock().registerAll();
    public static final BlockEntityType<EtherealStorageBlockEntity> ETHEREAL_STORAGE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_storage_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealStorageBlockEntity::new, ETHEREAL_STORAGE).build()
    );

    public static final EtherealChannel ETHEREAL_CHANNEL = (EtherealChannel) new EtherealChannel().registerAll(false);
    public static final BlockEntityType<EtherealChannelBlockEntity> ETHEREAL_CHANNEL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_channel_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealChannelBlockEntity::new, ETHEREAL_CHANNEL).build()
    );

    public static final EtherealForkBlock ETHEREAL_FORK = (EtherealForkBlock) new EtherealForkBlock().registerAll();
    public static final BlockEntityType<EtherealForkBlockEntity> ETHEREAL_FORK_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_fork_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealForkBlockEntity::new, ETHEREAL_FORK).build()
    );

    public static final EtherealSocketBlock ETHEREAL_SOCKET = (EtherealSocketBlock) new EtherealSocketBlock().registerAll();
    public static final BlockEntityType<EtherealSocketBlockEntity> ETHEREAL_SOCKET_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_socket_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealSocketBlockEntity::new, ETHEREAL_SOCKET).build()
    );

    public static final EtherealFurnace ETHEREAL_FURNACE = (EtherealFurnace) new EtherealFurnace().registerAll();
    public static final BlockEntityType<EtherealFurnaceBlockEntity> ETHEREAL_FURNACE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_furnace_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealFurnaceBlockEntity::new, ETHEREAL_FURNACE).build()
    );

    public static final EtherealSpinnerBlock ETHEREAL_SPINNER = (EtherealSpinnerBlock) new EtherealSpinnerBlock().registerAll();
    public static final BlockEntityType<EtherealSpinnerBlockEntity> ETHEREAL_SPINNER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_spinner_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealSpinnerBlockEntity::new, ETHEREAL_SPINNER).build()
    );

    public static final EtherealMetronomeBlock ETHEREAL_METRONOME = (EtherealMetronomeBlock) new EtherealMetronomeBlock().registerAll();
    public static final BlockEntityType<EtherealMetronomeBlockEntity> ETHEREAL_METRONOME_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("ethereal_metronome_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherealMetronomeBlockEntity::new, ETHEREAL_METRONOME).build()
    );

    public static final EmpowerTableBlock EMPOWERMENT_TABLE = (EmpowerTableBlock) new EmpowerTableBlock().registerAll();
    public static final BlockEntityType<EmpowerTableBlockEntity> EMPOWERMENT_TABLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("empowerment_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(EmpowerTableBlockEntity::new, EMPOWERMENT_TABLE).build()
    );

    public static final SamovarBlock SAMOVAR_BLOCK = (SamovarBlock) new SamovarBlock().registerAll();

    public static final SpillBarrelBlock SPILL_BARREL = (SpillBarrelBlock) new SpillBarrelBlock().registerBlock();
    public static final BlockEntityType<SpillBarrelBlockEntity> SPILL_BARREL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("spill_barrel_block_entity"),
            FabricBlockEntityTypeBuilder.create(SpillBarrelBlockEntity::new, SPILL_BARREL).build()
    );

    public static final JugBlock JUG = (JugBlock) new JugBlock("jug", JugType.BURN).registerAll();
    public static final JugBlock CLAY_JUG = (JugBlock) new JugBlock("clay_jug", JugType.RAW, FabricBlockSettings.copy(Blocks.CLAY)).registerAll();
    public static final BlockEntityType<JugBlockEntity> JUG_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("jug_block_entity"),
            FabricBlockEntityTypeBuilder.create(JugBlockEntity::new, JUG).build()
    );

    public static final CrateBlock CRATE = (CrateBlock) new CrateBlock().registerAll();
    public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("crate_block_entity"),
            FabricBlockEntityTypeBuilder.create(CrateBlockEntity::new, CRATE).build()
    );

    public static final LevitatorBlock LEVITATOR = (LevitatorBlock) new LevitatorBlock().registerAll();
    public static final BlockEntityType<LevitatorBlockEntity> LEVITATOR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("levitator_block_entity"),
            FabricBlockEntityTypeBuilder.create(LevitatorBlockEntity::new, LEVITATOR).build()
    );

    public static final ArmillaryMatrixBlock ARMILLARY_MATRIX = (ArmillaryMatrixBlock) new ArmillaryMatrixBlock().registerAll();
    public static final BlockEntityType<ArmillaryMatrixBlockEntity> ARMILLARY_MATRIX_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("armillary_matrix_block_entity"),
            FabricBlockEntityTypeBuilder.create(ArmillaryMatrixBlockEntity::new, ARMILLARY_MATRIX).build()
    );

    public static final InventorTable INVENTOR_TABLE = (InventorTable) new InventorTable().registerAll();

    public static final JewelryTable JEWELRY_TABLE = (JewelryTable) new JewelryTable().registerAll();
    public static final BlockEntityType<JewelryBlockEntity> JEWELRY_TABLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("jewelry_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(JewelryBlockEntity::new, JEWELRY_TABLE).build()
    );

    public static final EtherealChannelCase ETHEREAL_CHANNEL_CASE = (EtherealChannelCase) new EtherealChannelCase().registerAll();

    public static final TuningFork TUNING_FORK = (TuningFork) new TuningFork().registerAll();
    public static final BlockEntityType<TuningForkBlockEntity> TUNING_FORK_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            EIdentifier.of("tuning_fork_block_entity"),
            FabricBlockEntityTypeBuilder.create(TuningForkBlockEntity::new, TUNING_FORK).build()
    );

    public static void registerAll() {
        DecoBlocks.registerAll();
        DevBlocks.registerAll();
    }
}
