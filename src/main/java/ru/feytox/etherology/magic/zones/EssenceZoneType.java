package ru.feytox.etherology.magic.zones;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.CodecUtil;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RGBColor;

import java.util.Optional;
import java.util.function.Supplier;

@Getter
public enum EssenceZoneType implements StringIdentifiable {
    EMPTY(null, null, null),
    KETA(() -> EItems.PRIMOSHARD_KETA, new RGBColor(128, 205, 247), new RGBColor(105, 128, 231)),
    RELLA(() -> EItems.PRIMOSHARD_RELLA, new RGBColor(177, 229,106), new RGBColor(106, 182, 81)),
    VIA(() -> EItems.PRIMOSHARD_VIA, new RGBColor(248, 122, 95), new RGBColor(205, 58, 76)),
    CLOS(() -> EItems.PRIMOSHARD_CLOS, new RGBColor(106, 182, 81), new RGBColor(208, 158, 89));

    public static final Codec<EssenceZoneType> CODEC = StringIdentifiable.createBasicCodec(EssenceZoneType::values);
    public static final PacketCodec<ByteBuf, EssenceZoneType> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Nullable
    private final Supplier<Item> shardGetter;
    @Nullable
    private final RGBColor startColor;
    @Nullable
    private final RGBColor endColor;
    @Nullable
    private final Identifier textureId;
    @Nullable
    private final Identifier textureLightId;

    EssenceZoneType(Supplier<Item> shardGetter, RGBColor startColor, RGBColor endColor) {
        this.shardGetter = shardGetter;
        this.startColor = startColor;
        this.endColor = endColor;

        boolean isZone = shardGetter != null;
        this.textureId = isZone ? EIdentifier.of("textures/block/%s_seal.png".formatted(asString())) : null;
        this.textureLightId = isZone ? EIdentifier.of("textures/block/%s_seal_light.png".formatted(asString())) : null;
    }

    public Optional<Item> getPrimoShard() {
        return Optional.ofNullable(shardGetter).map(Supplier::get);
    }

    public boolean isZone() {
        return this != EMPTY;
    }

    public Block getBlock() {
        return EBlocks.ZONE_CORES[ordinal()-1];
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
