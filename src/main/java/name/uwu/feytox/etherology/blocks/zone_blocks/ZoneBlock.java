package name.uwu.feytox.etherology.blocks.zone_blocks;

import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_BLOCK_ENTITY;

public class ZoneBlock extends AirBlock implements BlockEntityProvider {
    private final String blockId;

    public ZoneBlock(String blockId) {
        super(FabricBlockSettings.of(Material.AIR).noCollision().dropsNothing().blockVision(getPredicate()));
        this.blockId = blockId;
    }

    public ZoneBlock(String blockId, Settings settings) {
        super(settings);
        this.blockId = blockId;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ZONE_BLOCK_ENTITY) return null;

        return world.isClient ? ZoneBlockEntity::clientTick : null;
    }

    // TODO: implement as SimpleBlock methods
    public ZoneBlock registerAll() {
        Registry.register(Registries.BLOCK, new EIdentifier(this.blockId), this);
        Registry.register(Registries.ITEM, new EIdentifier(this.blockId + "_item"), new BlockItem(this, new FabricItemSettings()));
        return this;
    }

    public static ContextPredicate getPredicate() {
        return (state, world, pos) -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return false;

            return player.getStackInHand(player.getActiveHand()).isItemEqual(Items.ACACIA_LEAVES.getDefaultStack());
        };
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        String name = blockId.split("_")[0];
        return new ZoneBlockEntity(EssenceZones.getByName(name), pos, state);
    }
}
