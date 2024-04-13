package ru.feytox.etherology.magic.aspects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.CheckReturnValue;
import ru.feytox.etherology.util.feyapi.Nbtable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class AspectContainer implements Nbtable {

    @NonNull
    private final ImmutableMap<Aspect, Integer> aspects;

    @Nullable // while reloading only
    private final ImmutableSet<AspectContainerId> parents;

    public AspectContainer(Map<Aspect, Integer> mutableAspects, Set<AspectContainerId> mutableParents) {
        aspects = ImmutableMap.copyOf(mutableAspects);
        parents = mutableParents == null || mutableParents.isEmpty() ? null : ImmutableSet.copyOf(mutableParents);
    }

    public AspectContainer(Map<Aspect, Integer> mutableAspects, boolean clearZeros, Set<AspectContainerId> mutableParents) {
        this(clearZeros ? clearZeros(mutableAspects) : mutableAspects, mutableParents);
    }

    public AspectContainer(Map<Aspect, Integer> mutableAspects) {
        this(mutableAspects, null);
    }

    public AspectContainer() {
        this(new Object2ObjectOpenHashMap<>(), null);
    }

    public AspectContainer(Map<Aspect, Integer> mutableAspects, boolean clearZeros) {
        this(mutableAspects, clearZeros, null);
    }

    @CheckReturnValue
    public AspectContainer combineOnReload(AspectContainer otherContainer) {
        return merge(otherContainer, true, Math::min);
    }

    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    @CheckReturnValue
    public AspectContainer applyParents(Map<AspectContainerId, AspectContainer> registryMap) {
        if (parents == null || parents.isEmpty()) return this;
        Optional<AspectContainer> sumParent = parents.stream()
                .map(registryMap::get)
                .map(container -> container.applyParents(registryMap))
                .reduce(AspectContainer::add);
        return sumParent.get().add(this);
    }

    @CheckReturnValue
    public AspectContainer add(AspectContainer otherContainer) {
        return merge(otherContainer, false, Integer::sum);
    }

    @CheckReturnValue
    public AspectContainer subtract(AspectContainer otherContainer) {
        return merge(otherContainer, false, (i1, i2) -> i1 - i2);
    }

    @CheckReturnValue
    private AspectContainer merge(AspectContainer otherContainer, boolean isReloading, BiFunction<Integer, Integer, Integer> mergeFunction) {
        Map<Aspect, Integer> mutableAspects = getMutableAspects();
        otherContainer.aspects.forEach((otherAspect, otherValue) -> mutableAspects.merge(otherAspect, otherValue, mergeFunction));

        val newParents = new ObjectArraySet<AspectContainerId>();
        if (isReloading) {
            val parents = getMutableParents();
            val otherParents = otherContainer.getMutableParents();
            if (parents != null) newParents.addAll(parents);
            if (otherParents != null) newParents.addAll(otherParents);
        }

        return new AspectContainer(mutableAspects, newParents);
    }

    public Map<Aspect, Integer> getMutableAspects() {
        return new Object2ObjectOpenHashMap<>(this.aspects);
    }

    @Nullable
    public Set<AspectContainerId> getMutableParents() {
        return parents != null ? new ObjectArraySet<>(parents) : null;
    }

    @CheckReturnValue
    public AspectContainer map(Function<Integer, Integer> mapper) {
        Map<Aspect, Integer> mutableAspects = getMutableAspects();
        mutableAspects.replaceAll((ignored, value) -> mapper.apply(value));
        return new AspectContainer(mutableAspects, true, getMutableParents());
    }

    public static Map<Aspect, Integer> clearZeros(Map<Aspect, Integer> map) {
        return map.entrySet().stream().filter(entry -> entry.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static JsonDeserializer<AspectContainer> deserializer() throws JsonParseException, IllegalArgumentException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<Aspect, Integer> aspects = new Object2ObjectOpenHashMap<>();
            Set<AspectContainerId> parents = new ObjectArraySet<>();
            root.asMap().forEach((s, jsonElement) -> {
                if (Objects.equals(s, "parent")) {
                    // parent: [ value ]
                    if (jsonElement.isJsonArray()) {
                        jsonElement.getAsJsonArray().asList().stream()
                                .map(JsonElement::getAsString)
                                .map(AspectContainerId::of)
                                .forEach(parents::add);
                        return;
                    }

                    // parent: value
                    parents.add(AspectContainerId.of(jsonElement.getAsString()));
                    return;
                }

                Aspect aspect = Aspect.valueOf(s.toUpperCase());
                int value = jsonElement.getAsInt();
                if (value < 0) throw new IllegalArgumentException("The value of the aspect cannot be negative.");
                if (value != 0) aspects.put(aspect, value);
            });

            return new AspectContainer(aspects, parents);
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
        Map<Aspect, Integer> result = new Object2ObjectOpenHashMap<>();

        NbtCompound nbtContainer = nbt.getCompound("aspects");
        nbtContainer.getKeys().forEach(aspectName -> {
            Aspect aspect = Aspect.valueOf(aspectName);
            int value = nbtContainer.getInt(aspectName);
            result.put(aspect, value);
        });

        // this.parents should not be written to nbt
        return new AspectContainer(result, null);
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeMap(aspects, PacketByteBuf::writeEnumConstant, PacketByteBuf::writeInt);
    }

    public Optional<Integer> max() {
        return aspects.values().stream().max(Comparator.comparingInt(Integer::intValue));
    }

    public Optional<Integer> sum() {
        return aspects.values().stream().reduce(Integer::sum);
    }

    // TODO: 13.04.2024 test results if aspects have same values
    public List<Pair<Aspect, Integer>> sorted(boolean reverse, int limit) {
        val sortedAspects = aspects.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ObjectArrayList::new));

        if (reverse) Collections.reverse(sortedAspects);
        if (limit < 0 || sortedAspects.size() <= limit) return sortedAspects;

        return sortedAspects.subList(0, limit);
    }

    public static AspectContainer readBuf(PacketByteBuf buf) {
        Map<Aspect, Integer> aspectMap = buf.readMap(buff -> buff.readEnumConstant(Aspect.class), PacketByteBuf::readInt);
        // this.parents should not be written to buf
        return new AspectContainer(aspectMap, null);
    }
}
