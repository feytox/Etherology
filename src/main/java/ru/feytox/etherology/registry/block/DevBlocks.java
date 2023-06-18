package ru.feytox.etherology.registry.block;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.block.devblocks.UnlimitedEtherStorageBlock;
import ru.feytox.etherology.block.devblocks.UnlimitedEtherStorageBlockEntity;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class DevBlocks {

    public static final UnlimitedEtherStorageBlock UNLIMITED_ETHER_STORAGE_BLOCK = (UnlimitedEtherStorageBlock) new UnlimitedEtherStorageBlock().registerAll();
    public static final BlockEntityType<UnlimitedEtherStorageBlockEntity> UNLIMITED_ETHER_STORAGE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new EIdentifier("unlimited_ether_storage_block_entity"),
            FabricBlockEntityTypeBuilder.create(UnlimitedEtherStorageBlockEntity::new, UNLIMITED_ETHER_STORAGE_BLOCK).build()
    );

    public static void registerAll() {}
}
