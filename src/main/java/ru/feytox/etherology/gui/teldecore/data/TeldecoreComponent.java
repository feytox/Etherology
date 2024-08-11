package ru.feytox.etherology.gui.teldecore.data;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.network.interaction.EntityComponentC2SType;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor @Getter
public class TeldecoreComponent implements ComponentV3, CopyableComponent<TeldecoreComponent>, AutoSyncedComponent {

    private final PlayerEntity player;
    @Setter
    private Identifier selected = TeldecoreScreen.CHAPTER_MENU;
    @Nullable @Setter
    private Identifier tab = null;
    @Setter
    private int page = 0;
    private Set<Identifier> completedQuests = new ObjectOpenHashSet<>();

    public void addCompletedQuest(Identifier chapterId) {
        completedQuests.add(chapterId);
        sync();
    }

    public boolean isCompleted(Identifier chapterId) {
        return completedQuests.contains(chapterId);
    }

    public void turnPage(boolean isLeft) {
        setPage(Math.max(0, page + (isLeft ? -1 : 1)));
    }

    public void switchTab(Identifier tab) {
        setChapterPage(0);
        setSelected(TeldecoreScreen.CHAPTER_MENU);
        this.tab = tab;
        sendToServer(EntityComponentC2SType.TELDECORE_TAB);
    }

    public void setChapterPage(int page) {
        this.page = page;
        sendToServer(EntityComponentC2SType.TELDECORE_PAGE);
    }

    public void setSelectedChapter(Identifier selected) {
        setChapterPage(0);
        this.selected = selected;
        sendToServer(EntityComponentC2SType.TELDECORE_SELECTED);
    }

    @Override
    public void copyFrom(TeldecoreComponent other, RegistryWrapper.WrapperLookup registryLookup) {
        selected = other.selected;
        page = other.page;
        tab = other.tab;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        selected = Identifier.of(tag.getString("selected"));
        page = tag.getInt("page");
        String tabStr = tag.getString("tab");
        tab = tabStr.isEmpty() ? null : Identifier.of(tabStr);

        completedQuests = tag.getList("completed", NbtList.STRING_TYPE).stream().map(NbtElement::asString)
                .map(Identifier::of).collect(Collectors.toCollection(ObjectOpenHashSet::new));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("selected", selected.toString());
        tag.putInt("page", page);
        if (tab != null) tag.putString("tab", tab.toString());

        if (completedQuests.isEmpty()) return;
        NbtList quests = new NbtList();
        completedQuests.stream().map(Identifier::toString).map(NbtString::of).forEach(quests::add);
        tag.put("completed", quests);
    }

    private void sync() {
        EtherologyComponents.TELDECORE.sync(player);
    }

    private void sendToServer(EntityComponentC2SType<TeldecoreComponent, ?> packetType) {
        packetType.sendToServer(this);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return this.player.equals(player);
    }

    public void clearQuests() {
        completedQuests.clear();
        sync();
    }

    @Environment(EnvType.CLIENT)
    public static Optional<TeldecoreComponent> maybeGetClient() {
        return Optional.ofNullable(MinecraftClient.getInstance().player).flatMap(EtherologyComponents.TELDECORE::maybeGet);
    }
}
