package ru.feytox.etherology.magic.staff;

import io.wispforest.owo.nbt.NbtKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.util.feyapi.NbtReadable;

@AllArgsConstructor
public class StaffPartInfo implements NbtReadable<StaffPartInfo> {

    private static final NbtKey.Type<StaffPartInfo> NBT_TYPE;
    public static final NbtKey<StaffPartInfo> NBT_KEY;
    public static final NbtKey.ListKey<StaffPartInfo> LIST_KEY;

    @NonNull
    @Getter
    @Setter
    private StaffPart part;

    @NonNull
    @Getter
    @Setter
    private StaffStyle style;

    public static StaffPartInfo of(@Nullable StaffPart part, @Nullable StaffStyle style) {
        if (part == null) part = StaffPart.NULL;
        if (style == null) style = StaffStyle.NULL;
        return new StaffPartInfo(part, style);
    }

    public ModelIdentifier toModelId() {
        String postfix = part.getName();
        if (!style.equals(StaffStyle.NULL)) postfix = postfix + "_" + style.getName();
        return EtherologyModels.createItemModelId("staff_" + postfix);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        part.writeNbt(nbt);
        style.writeNbt(nbt);
    }

    @Override
    public StaffPartInfo readNbt(NbtCompound nbt) {
        setPart(part.readNbt(nbt));
        setStyle(style.readNbt(nbt));
        return this;
    }

    static {
        NBT_TYPE = NbtKey.Type.of(NbtElement.COMPOUND_TYPE, (nbt, s) -> {
            StaffPartInfo partInfo = StaffPartInfo.of(null, null);
            partInfo.readNbt(nbt.getCompound(s));
            return partInfo;
        }, (nbt, s, partInfo) -> {
            NbtCompound subNbt = new NbtCompound();
            partInfo.writeNbt(subNbt);
            nbt.put(s, subNbt);
        });
        LIST_KEY = new NbtKey.ListKey<>("parts", NBT_TYPE);
        NBT_KEY = new NbtKey<>("part_info", NBT_TYPE);
    }
}
