package ru.feytox.etherology.data.item_aspects;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import ru.feytox.etherology.magic.aspects.EtherAspect;
import ru.feytox.etherology.util.nbt.Nbtable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemAspectsContainer implements Nbtable {

    @Getter
    @NonNull
    private final ImmutableMap<EtherAspect, Integer> aspects;

    public ItemAspectsContainer(Map<EtherAspect, Integer> mutableAspects) {
        aspects = ImmutableMap.copyOf(mutableAspects);
    }

    public ItemAspectsContainer combine(ItemAspectsContainer otherContainer) {
        return merge(otherContainer, Math::min);
    }

    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    public ItemAspectsContainer add(ItemAspectsContainer otherContainer) {
        return merge(otherContainer, Integer::sum);
    }

    public ItemAspectsContainer subtract(ItemAspectsContainer otherContainer) {
        return merge(otherContainer, (i1, i2) -> i1 - i2);
    }

    public ItemAspectsContainer merge(ItemAspectsContainer otherContainer, BiFunction<Integer, Integer, Integer> mergeFunction) {
        Map<EtherAspect, Integer> mutableAspects = getMutableAspects();
        otherContainer.aspects.forEach((otherAspect, otherValue) -> mutableAspects.merge(otherAspect, otherValue, mergeFunction));
        return new ItemAspectsContainer(mutableAspects);
    }

    public Map<EtherAspect, Integer> getMutableAspects() {
        return new Object2ObjectOpenHashMap<>(this.aspects);
    }

    public ItemAspectsContainer map(Function<Integer, Integer> mapper) {
        Map<EtherAspect, Integer> mutableAspects = getMutableAspects();
        mutableAspects.replaceAll((ignored, value) -> mapper.apply(value));
        return new ItemAspectsContainer(mutableAspects);
    }

    public static JsonDeserializer<ItemAspectsContainer> deserializer() throws JsonParseException, IllegalArgumentException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<EtherAspect, Integer> aspects = new Object2ObjectOpenHashMap<>();
            root.asMap().forEach((s, jsonElement) -> {
                EtherAspect aspect = EtherAspect.valueOf(s.toUpperCase());
                int value = jsonElement.getAsInt();
                if (value < 0) throw new IllegalArgumentException("The value of the aspect cannot be negative.");
                if (value != 0) aspects.put(aspect, value);
            });

            return new ItemAspectsContainer(aspects);
        };
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound container = new NbtCompound();
        aspects.forEach(((aspect, value) -> container.putInt(aspect.name(), value)));

        nbt.put("aspects", container);
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        Map<EtherAspect, Integer> result = new Object2ObjectOpenHashMap<>();

        NbtCompound nbtContainer = nbt.getCompound("aspects");
        nbtContainer.getKeys().forEach(aspectName -> {
            EtherAspect aspect = EtherAspect.valueOf(aspectName);
            int value = nbtContainer.getInt(aspectName);
            result.put(aspect, value);
        });

        return new ItemAspectsContainer(result);
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeMap(aspects, PacketByteBuf::writeEnumConstant, PacketByteBuf::writeInt);
    }

    public static ItemAspectsContainer readBuf(PacketByteBuf buf) {
        Map<EtherAspect, Integer> aspectMap = buf.readMap(buff -> buff.readEnumConstant(EtherAspect.class), PacketByteBuf::readInt);
        return new ItemAspectsContainer(aspectMap);
    }
}
