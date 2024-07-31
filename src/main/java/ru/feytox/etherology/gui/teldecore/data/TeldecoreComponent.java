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
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor @Getter
public class TeldecoreComponent implements ComponentV3, CopyableComponent<TeldecoreComponent>, AutoSyncedComponent {

    public static final Identifier MENU = EIdentifier.of("menu");

    private final PlayerEntity player;
    private Identifier selected = MENU;
    private int page = 0;

    public void turnPage(boolean isLeft) {
        this.page = Math.max(0, this.page + (isLeft ? -1 : 1));
        sync();
    }

    public void setPage(int page) {
        this.page = page;
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
