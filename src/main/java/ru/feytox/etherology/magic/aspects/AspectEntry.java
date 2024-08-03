package ru.feytox.etherology.magic.aspects;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record AspectEntry(@NotNull AspectContainer aspects, @NotNull List<AspectContainerId> parents, int priority) {

    public static final Codec<AspectEntry> CODEC;

    public AspectContainer toContainer(AspectRegistryPart.Lookup lookup) {
        if (parents.isEmpty()) return aspects;

        return parents.stream().map(lookup::get).reduce(AspectContainer::add).get().add(aspects);
    }

    static {
        CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<AspectEntry, T>> decode(DynamicOps<T> ops, T input) {
                return ops.getMap(input).setLifecycle(Lifecycle.stable()).map(mapLike -> {
                    AtomicInteger priority = new AtomicInteger();
                    List<AspectContainerId> parents = new ObjectArrayList<>();

                    Stream<Pair<T, T>> aspectsStream = mapLike.entries().filter(pair -> switch (ops.getStringValue(pair.getFirst()).getOrThrow()) {
                        case "priority" -> {
                            priority.set(Codec.INT.parse(ops, pair.getSecond()).getOrThrow());
                            yield false;
                        }
                        case "parents" -> {
                            parents.addAll(AspectContainerId.CODEC.listOf().parse(ops, pair.getSecond()).getOrThrow());
                            yield false;
                        }
                        case "parent" -> {
                            parents.add(AspectContainerId.CODEC.parse(ops, pair.getSecond()).getOrThrow());
                            yield false;
                        }
                        default -> true;
                    });
                    AspectContainer aspects = AspectContainer.parse(ops, aspectsStream);
                    return new AspectEntry(aspects, parents, priority.get());
                }).map(entry -> Pair.of(entry, input));
            }

            @Override
            public <T> DataResult<T> encode(AspectEntry input, DynamicOps<T> ops, T prefix) {
                RecordBuilder<T> builder = ops.mapBuilder();
                if (input.priority != 0) builder.add("priority", Codec.INT.encodeStart(ops, input.priority));
                if (input.parents().size() == 1) builder.add("parent", AspectContainerId.CODEC.encodeStart(ops, input.parents.getFirst()));
                else if (input.parents().size() > 1) builder.add("parents", AspectContainerId.CODEC.listOf().encodeStart(ops, input.parents));
                AspectContainer.encodeStart(builder, ops, input.aspects);
                return builder.build(prefix);
            }
        };
    }
}

