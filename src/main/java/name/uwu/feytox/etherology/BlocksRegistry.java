package name.uwu.feytox.etherology;

import name.uwu.feytox.etherology.blocks.armillar.ArmillaryMatrixBlock;
import name.uwu.feytox.etherology.blocks.armillar.ArmillaryMatrixBlockEntity;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlock;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockEntity;
import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbench;
import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbenchBlockEntity;
import name.uwu.feytox.etherology.blocks.pedestal.PedestalBlock;
import name.uwu.feytox.etherology.blocks.pedestal.PedestalBlockEntity;
import name.uwu.feytox.etherology.blocks.ringMatrix.RingMatrixBlock;
import name.uwu.feytox.etherology.blocks.ringMatrix.RingMatrixBlockEntity;
import name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlock;
import name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlockEntity;
import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneCoreBlock;
import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneCoreBlockEntity;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlocksRegistry {

    public static final CrucibleBlock CRUCIBLE = (CrucibleBlock) new CrucibleBlock().registerAll();
    public static final BlockEntityType<CrucibleBlockEntity> CRUCIBLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("crucible_block_entity"),
            FabricBlockEntityTypeBuilder.create(CrucibleBlockEntity::new, CRUCIBLE).build()
    );

    public static final EtherWorkbench ETHER_WORKBENCH = (EtherWorkbench) new EtherWorkbench().registerAll();
    public static final BlockEntityType<EtherWorkbenchBlockEntity> ETHER_WORKBENCH_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("ether_workbench_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherWorkbenchBlockEntity::new, ETHER_WORKBENCH).build()
    );

    public static final PedestalBlock PEDESTAL_BLOCK = (PedestalBlock) new PedestalBlock().registerAll();
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("pedestal_block_entity"),
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, PEDESTAL_BLOCK).build()
    );

    public static final ArmillaryMatrixBlock ARMILLARY_MATRIX_BASE = (ArmillaryMatrixBlock) new ArmillaryMatrixBlock().registerAll();
    public static final BlockEntityType<ArmillaryMatrixBlockEntity> ARMILLARY_MATRIX_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("armillary_matrix_block_entity"),
            FabricBlockEntityTypeBuilder.create(ArmillaryMatrixBlockEntity::new, ARMILLARY_MATRIX_BASE).build()
    );

    public static final RingMatrixBlock RING_MATRIX_BLOCK = (RingMatrixBlock) new RingMatrixBlock().registerAll();
    public static final BlockEntityType<RingMatrixBlockEntity> RING_MATRIX_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("ring_matrix_block"),
            FabricBlockEntityTypeBuilder.create(RingMatrixBlockEntity::new, RING_MATRIX_BLOCK).build()
    );

    public static final ZoneCoreBlock ZONE_CORE_BLOCK = (ZoneCoreBlock) new ZoneCoreBlock().registerAll();
    public static final BlockEntityType<ZoneCoreBlockEntity> ZONE_CORE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("zone_core_block_entity"),
            FabricBlockEntityTypeBuilder.create(ZoneCoreBlockEntity::new, ZONE_CORE_BLOCK).build()
    );

    public static final SedimentaryBlock SEDIMENTARY_BLOCK = (SedimentaryBlock) new SedimentaryBlock().registerAll();
    public static final BlockEntityType<SedimentaryBlockEntity> SEDIMENTARY_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("sedimentary_block_entity"),
            FabricBlockEntityTypeBuilder.create(SedimentaryBlockEntity::new, SEDIMENTARY_BLOCK).build()
    );

    public static void register() {}
}
