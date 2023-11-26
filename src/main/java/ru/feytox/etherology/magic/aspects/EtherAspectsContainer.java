package ru.feytox.etherology.magic.aspects;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.helpers.CheckReturnValue;
import ru.feytox.etherology.util.feyapi.Nbtable;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EtherAspectsContainer implements Nbtable {

    @Getter
    @NonNull
    private final ImmutableMap<EtherAspect, Integer> aspects;

    public EtherAspectsContainer(Map<EtherAspect, Integer> mutableAspects) {
        aspects = ImmutableMap.copyOf(mutableAspects);
    }

    public EtherAspectsContainer(Map<EtherAspect, Integer> mutableAspects, boolean clearZeros) {
        this(clearZeros ? clearZeros(mutableAspects) : mutableAspects);
    }

    @CheckReturnValue
    public EtherAspectsContainer combine(EtherAspectsContainer otherContainer) {
        return merge(otherContainer, Math::min);
    }

    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    @CheckReturnValue
    public EtherAspectsContainer add(EtherAspectsContainer otherContainer) {
        return merge(otherContainer, Integer::sum);
    }

    @CheckReturnValue
    public EtherAspectsContainer subtract(EtherAspectsContainer otherContainer) {
        return merge(otherContainer, (i1, i2) -> i1 - i2);
    }

    @CheckReturnValue
    public EtherAspectsContainer merge(EtherAspectsContainer otherContainer, BiFunction<Integer, Integer, Integer> mergeFunction) {
        Map<EtherAspect, Integer> mutableAspects = getMutableAspects();
        otherContainer.aspects.forEach((otherAspect, otherValue) -> mutableAspects.merge(otherAspect, otherValue, mergeFunction));
        return new EtherAspectsContainer(mutableAspects);
    }

    public Map<EtherAspect, Integer> getMutableAspects() {
        return new Object2ObjectOpenHashMap<>(this.aspects);
    }

    @CheckReturnValue
    public EtherAspectsContainer map(Function<Integer, Integer> mapper) {
        Map<EtherAspect, Integer> mutableAspects = getMutableAspects();
        mutableAspects.replaceAll((ignored, value) -> mapper.apply(value));
        return new EtherAspectsContainer(mutableAspects, true);
    }

    public static Map<EtherAspect, Integer> clearZeros(Map<EtherAspect, Integer> map) {
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static JsonDeserializer<EtherAspectsContainer> deserializer() throws JsonParseException, IllegalArgumentException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<EtherAspect, Integer> aspects = new Object2ObjectOpenHashMap<>();
            root.asMap().forEach((s, jsonElement) -> {
                EtherAspect aspect = EtherAspect.valueOf(s.toUpperCase());
                int value = jsonElement.getAsInt();
                if (value < 0) throw new IllegalArgumentException("The value of the aspect cannot be negative.");
                if (value != 0) aspects.put(aspect, value);
            });

            return new EtherAspectsContainer(aspects);
        };
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound container = new NbtCompound();
        aspects.forEach(((aspect, value) -> container.putInt(aspect.name(), value)));

        nbt.put("aspects", container);
    }

    @Override
    @CheckReturnValue
    public Nbtable readNbt(NbtCompound nbt) {
        Map<EtherAspect, Integer> result = new Object2ObjectOpenHashMap<>();

        NbtCompound nbtContainer = nbt.getCompound("aspects");
        nbtContainer.getKeys().forEach(aspectName -> {
            EtherAspect aspect = EtherAspect.valueOf(aspectName);
            int value = nbtContainer.getInt(aspectName);
            result.put(aspect, value);
        });

        return new EtherAspectsContainer(result);
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeMap(aspects, PacketByteBuf::writeEnumConstant, PacketByteBuf::writeInt);
    }

    public Optional<Integer> max() {
        return aspects.values().stream().max(Comparator.comparingInt(Integer::intValue));
    }

    public Optional<Integer> count() {
        return aspects.values().stream().reduce(Integer::sum);
    }

    public static EtherAspectsContainer readBuf(PacketByteBuf buf) {
        Map<EtherAspect, Integer> aspectMap = buf.readMap(buff -> buff.readEnumConstant(EtherAspect.class), PacketByteBuf::readInt);
        return new EtherAspectsContainer(aspectMap);
    }
}
