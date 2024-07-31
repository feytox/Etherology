package ru.feytox.etherology.gui.teldecore.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@RequiredArgsConstructor @Getter
public class TeldecoreComponent implements ComponentV3, CopyableComponent<TeldecoreComponent>, AutoSyncedComponent {

    private final PlayerEntity player;
    private Identifier selected = TeldecoreScreen.CHAPTER_MENU;
    private int page = 0;

    public void turnPage(boolean isLeft) {
        this.page = Math.max(0, this.page + (isLeft ? -1 : 1));
        sync();
    }

    public void setPage(int page) {
        this.page = page;
        sync();
    }

    public void setSelected(Identifier selected) {
        this.page = 0;
        this.selected = selected;
        sync();
    }

    @Override
    public void copyFrom(TeldecoreComponent other, RegistryWrapper.WrapperLookup registryLookup) {
        selected = other.selected;
        page = other.page;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        selected = Identifier.of(tag.getString("selected"));
        page = tag.getInt("page");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("selected", selected.toString());
        tag.putInt("page", page);
    }

    public void sync() {
        EtherologyComponents.TELDECORE.sync(player);
    }
}
