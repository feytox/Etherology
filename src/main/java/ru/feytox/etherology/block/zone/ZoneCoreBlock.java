package ru.feytox.etherology.block.zone;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;

@Getter
public class ZoneCoreBlock extends Block implements BlockEntityProvider {

    private final EssenceZoneType zoneType;

    public ZoneCoreBlock(EssenceZoneType zoneType) {
        super(Settings.copy(Blocks.BEDROCK).noBlockBreakParticles().noCollision());
        this.zoneType = zoneType;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZoneCoreBlockEntity(pos, state, zoneType);
    }

    @Override
    public String getTranslationKey() {
        return "block.etherology.zone_core";
    }

    public static String createId(EssenceZoneType zoneType) {
        return "zone_core_" + zoneType.asString();
    }
}
