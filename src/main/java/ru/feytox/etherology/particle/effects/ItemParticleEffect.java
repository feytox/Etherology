package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.util.misc.CodecUtil;

@Getter
public class ItemParticleEffect extends FeyParticleEffect<ItemParticleEffect> {

    private final Item item;
    private final Vec3d moveVec;

    public ItemParticleEffect(ParticleType<ItemParticleEffect> type, Item item, Vec3d moveVec) {
        super(type);
        this.item = item;
        this.moveVec = moveVec;
    }

    public ItemParticleEffect(ParticleType<ItemParticleEffect> type) {
        this(type, null, null);
    }

    @Override
    public MapCodec<ItemParticleEffect> createCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemParticleEffect::getItem),
                Vec3d.CODEC.fieldOf("moveVec").forGetter(ItemParticleEffect::getMoveVec)
                ).apply(instance, biFactory(ItemParticleEffect::new)));
    }

    @Override
    public PacketCodec<RegistryByteBuf, ItemParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(CodecUtil.ITEM_PACKET, ItemParticleEffect::getItem,
                CodecUtil.VEC3D_PACKET, ItemParticleEffect::getMoveVec, biFactory(ItemParticleEffect::new));
    }
}
