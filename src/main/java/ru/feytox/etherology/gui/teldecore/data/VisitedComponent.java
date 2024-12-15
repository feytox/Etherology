package ru.feytox.etherology.gui.teldecore.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VisitedComponent implements ComponentV3, CopyableComponent<VisitedComponent>, ServerTickingComponent, AutoSyncedComponent {

    private static final Map<Identifier, BiPredicate<PlayerEntity, World>> ALL_PLACES = new Object2ObjectOpenHashMap<>();
    private static final int REFRESH_RATE = 10;

    private final PlayerEntity player;
    private ObjectOpenHashSet<Identifier> visitedIds = new ObjectOpenHashSet<>();

    public static void addBiomeListener(Identifier biomeId) {
        ALL_PLACES.put(biomeId, (player, world) -> world.getBiome(player.getBlockPos()).matchesId(biomeId));
    }

    public static boolean isVisited(PlayerEntity player, Identifier id) {
        return EtherologyComponents.VISITED.maybeGet(player)
                .map(data -> data.visitedIds.contains(id)).orElse(false);
    }

    @Override
    public void serverTick() {
        World world = player.getWorld();
        if (world == null || world.getTime() % REFRESH_RATE != 0) return;

        ALL_PLACES.forEach((id, predicate) -> {
            if (visitedIds.contains(id)) return;
            if (!predicate.test(player, world)) return;
            visitedIds.add(id);
            EtherologyComponents.VISITED.sync(player);
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtIds = new NbtList();
        visitedIds.stream().map(Identifier::toString).map(NbtString::of).forEach(nbtIds::add);
        tag.put("visited", nbtIds);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        visitedIds = tag.getList("visited", NbtList.STRING_TYPE).stream()
                .map(NbtElement::asString).map(Identifier::of).collect(Collectors.toCollection(ObjectOpenHashSet::new));

    }

    @Override
    public void copyFrom(VisitedComponent other, RegistryWrapper.WrapperLookup registryLookup) {
        visitedIds = other.visitedIds.clone();
    }

    public void resetAll() {
        visitedIds.clear();
        EtherologyComponents.VISITED.sync(player);
    }
}
