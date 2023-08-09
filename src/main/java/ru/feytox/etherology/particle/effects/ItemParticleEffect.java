package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class ItemParticleEffect extends FeyParticleEffect<ItemParticleEffect> {

    private final ParticleArg<String> itemIdArg = SimpleArgs.STRING.get();
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();

    public ItemParticleEffect(ParticleType<ItemParticleEffect> type, ItemStack stack, Vec3d moveVec) {
        this(type, Registries.ITEM.getId(stack.getItem()).toString(), moveVec);
    }

    public ItemParticleEffect(ParticleType<ItemParticleEffect> type, String itemId, Vec3d moveVec) {
        this(type);
        itemIdArg.setValue(itemId);
        moveVecArg.setValue(moveVec);
    }

    public ItemParticleEffect(ParticleType<ItemParticleEffect> type) {
        super(type);
    }

    @Override
    public ItemParticleEffect read(ParticleType<ItemParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        String itemId = itemIdArg.read(reader);
        reader.expect(' ');
        return new ItemParticleEffect(type, itemId, moveVecArg.read(reader));
    }

    @Override
    public ItemParticleEffect read(ParticleType<ItemParticleEffect> type, PacketByteBuf buf) {
        String itemId = itemIdArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new ItemParticleEffect(type, itemId, moveVec);
    }

    @Override
    public String write() {
        return itemIdArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<ItemParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                itemIdArg.getCodec("item_id").forGetter(ItemParticleEffect::getItemId),
                moveVecArg.getCodec("move_vec").forGetter(ItemParticleEffect::getMoveVec)
        ).apply(instance, (itemId, moveVec) -> new ItemParticleEffect(type, itemId, moveVec)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        itemIdArg.write(buf);
        moveVecArg.write(buf);
    }

    public String getItemId() {
        return itemIdArg.getValue();
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }
}
