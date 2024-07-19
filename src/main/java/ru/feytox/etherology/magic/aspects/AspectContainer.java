package ru.feytox.etherology.magic.aspects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import org.slf4j.helpers.CheckReturnValue;
import ru.feytox.etherology.util.misc.CodecUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class AspectContainer {

    public static final Codec<AspectContainer> CODEC;
    public static final MapCodec<AspectContainer> MAP_CODEC;
    public static final PacketCodec<ByteBuf, AspectContainer> PACKET_CODEC;

    @NonNull
    private final ImmutableMap<Aspect, Integer> aspects;

    public AspectContainer(Map<Aspect, Integer> mutableAspects) {
        aspects = ImmutableMap.copyOf(mutableAspects);
    }

    public AspectContainer(Map<Aspect, Integer> mutableAspects, boolean clearZeros) {
        this(clearZeros ? clearZeros(mutableAspects) : mutableAspects);
    }

    public AspectContainer() {
        this(new Object2ObjectOpenHashMap<>());
    }

    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    @CheckReturnValue
    public AspectContainer add(AspectContainer otherContainer) {
        return merge(otherContainer, Integer::sum);
    }

    @CheckReturnValue
    public AspectContainer subtract(AspectContainer otherContainer) {
        return merge(otherContainer, (i1, i2) -> i1 - i2);
    }

    @CheckReturnValue
    private AspectContainer merge(AspectContainer otherContainer, BiFunction<Integer, Integer, Integer> mergeFunction) {
        Map<Aspect, Integer> mutableAspects = getMutableAspects();
        otherContainer.aspects.forEach((otherAspect, otherValue) -> mutableAspects.merge(otherAspect, otherValue, mergeFunction));
        return new AspectContainer(mutableAspects);
    }

    public Object2IntOpenHashMap<Aspect> getMutableAspects() {
        return new Object2IntOpenHashMap<>(this.aspects);
    }

    @CheckReturnValue
    public AspectContainer map(Function<Integer, Integer> mapper) {
        Map<Aspect, Integer> mutableAspects = getMutableAspects();
        mutableAspects.replaceAll((ignored, value) -> mapper.apply(value));
        return new AspectContainer(mutableAspects, true);
    }

    public static Map<Aspect, Integer> clearZeros(Map<Aspect, Integer> map) {
        map.entrySet().removeIf(entry -> entry.getValue() <= 0);
        return map;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound container = new NbtCompound();
        aspects.forEach(((aspect, value) -> container.putInt(aspect.name(), value)));

        nbt.put("aspects", container);
    }

    @CheckReturnValue
    public AspectContainer readNbt(NbtCompound nbt) {
        Map<Aspect, Integer> result = new Object2ObjectOpenHashMap<>();

        NbtCompound nbtContainer = nbt.getCompound("aspects");
        nbtContainer.getKeys().forEach(aspectName -> {
            Aspect aspect = Aspect.valueOf(aspectName);
            int value = nbtContainer.getInt(aspectName);
            result.put(aspect, value);
        });

        // this.parents should not be written to nbt
        return new AspectContainer(result);
    }

    public Optional<Integer> max() {
        return aspects.values().stream().max(Comparator.comparingInt(Integer::intValue));
    }

    public Optional<Integer> sum() {
        return aspects.values().stream().reduce(Integer::sum);
    }

    public List<Pair<Aspect, Integer>> sorted(boolean reverse, int limit) {
        Comparator<Map.Entry<Aspect, Integer>> comparator = Map.Entry.comparingByValue();
        comparator = comparator.thenComparing(Map.Entry.comparingByKey());
        if (reverse) comparator = comparator.reversed();

        ObjectArrayList<Pair<Aspect, Integer>> sortedAspects = aspects.entrySet().stream()
                .sorted(comparator)
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ObjectArrayList::new));

        if (limit < 0 || sortedAspects.size() <= limit) return sortedAspects;

        return sortedAspects.subList(0, limit);
    }

    public static <T> AspectContainer parse(DynamicOps<T> ops, Stream<Pair<T, T>> input) {
        return new AspectContainer(input.map(pair -> pair.mapFirst(first -> Aspect.CODEC.parse(ops, first)).mapSecond(second -> Codec.INT.parse(ops, second)))
                .map(pair -> pair.mapFirst(DataResult::getOrThrow).mapSecond(DataResult::getOrThrow))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, Integer::min)));
    }

    public static <T> void encodeStart(RecordBuilder<T> builder, DynamicOps<T> ops, AspectContainer container) {
        container.getAspects().forEach((aspect, value) -> builder.add(Aspect.CODEC.encodeStart(ops, aspect), Codec.INT.encodeStart(ops, value)));
    }

    static {
        CODEC = Codec.unboundedMap(Aspect.CODEC, Codec.INT).xmap(AspectContainer::new, AspectContainer::getAspects).stable();
        MAP_CODEC = Codec.simpleMap(Aspect.CODEC, Codec.INT, StringIdentifiable.toKeyable(Aspect.values())).xmap(AspectContainer::new, AspectContainer::getAspects);
        PACKET_CODEC = CodecUtil.map(Object2IntOpenHashMap::new, Aspect.PACKET_CODEC, PacketCodecs.VAR_INT).xmap(AspectContainer::new, AspectContainer::getAspects);
    }
}
