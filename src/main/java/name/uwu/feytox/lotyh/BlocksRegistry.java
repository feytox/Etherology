package name.uwu.feytox.lotyh;

import name.uwu.feytox.lotyh.blocks.armillar.ArmillaryMatrixBlock;
import name.uwu.feytox.lotyh.blocks.armillar.ArmillaryMatrixBlockEntity;
import name.uwu.feytox.lotyh.blocks.crucible.CrucibleBlock;
import name.uwu.feytox.lotyh.blocks.crucible.CrucibleBlockEntity;
import name.uwu.feytox.lotyh.blocks.etherWorkbench.EtherWorkbench;
import name.uwu.feytox.lotyh.blocks.etherWorkbench.EtherWorkbenchBlockEntity;
import name.uwu.feytox.lotyh.blocks.pedestal.PedestalBlock;
import name.uwu.feytox.lotyh.blocks.pedestal.PedestalBlockEntity;
import name.uwu.feytox.lotyh.blocks.ringMatrix.RingMatrixBlock;
import name.uwu.feytox.lotyh.blocks.ringMatrix.RingMatrixBlockEntity;
import name.uwu.feytox.lotyh.util.LIdentifier;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlocksRegistry {

    public static final CrucibleBlock CRUCIBLE = (CrucibleBlock) new CrucibleBlock().registerBlock();
    public static final BlockEntityType<CrucibleBlockEntity> CRUCIBLE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new LIdentifier("crucible_block_entity"),
            FabricBlockEntityTypeBuilder.create(CrucibleBlockEntity::new, CRUCIBLE).build()
    );

    public static final EtherWorkbench ETHER_WORKBENCH = (EtherWorkbench) new EtherWorkbench().registerAll();
    public static final BlockEntityType<EtherWorkbenchBlockEntity> ETHER_WORKBENCH_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new LIdentifier("ether_workbench_block_entity"),
            FabricBlockEntityTypeBuilder.create(EtherWorkbenchBlockEntity::new, ETHER_WORKBENCH).build()
    );

    public static final PedestalBlock PEDESTAL_BLOCK = (PedestalBlock) new PedestalBlock().registerAll();
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new LIdentifier("pedestal_block_entity"),
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, PEDESTAL_BLOCK).build()
    );

    public static final ArmillaryMatrixBlock ARMILLARY_MATRIX_BASE = (ArmillaryMatrixBlock) new ArmillaryMatrixBlock().registerAll();
    public static final BlockEntityType<ArmillaryMatrixBlockEntity> ARMILLARY_MATRIX_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new LIdentifier("armillary_matrix_block_entity"),
            FabricBlockEntityTypeBuilder.create(ArmillaryMatrixBlockEntity::new, ARMILLARY_MATRIX_BASE).build()
    );

    public static final RingMatrixBlock RING_MATRIX_BLOCK = (RingMatrixBlock) new RingMatrixBlock().registerAll();
    public static final BlockEntityType<RingMatrixBlockEntity> RING_MATRIX_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new LIdentifier("ring_matrix_block"),
            FabricBlockEntityTypeBuilder.create(RingMatrixBlockEntity::new, RING_MATRIX_BLOCK).build()
    );


    public static void register() {
    }
}
