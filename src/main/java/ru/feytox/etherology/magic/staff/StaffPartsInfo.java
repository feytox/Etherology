package ru.feytox.etherology.magic.staff;

import io.wispforest.owo.nbt.NbtKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StaffPartsInfo {

    private static final NbtKey.Type<StaffPartsInfo> NBT_TYPE;
    public static final NbtKey<StaffPartsInfo> NBT_KEY;
    public static final NbtKey.ListKey<StaffPartsInfo> LIST_KEY;

    @NonNull
    @Getter
    private final StaffParts part;

    @NonNull
    @Getter
    private final StaffPattern firstPattern;

    @NonNull
    @Getter
    private final StaffPattern secondPattern;

    public static List<StaffPartsInfo> generateAll() {
        return Arrays.stream(StaffParts.values())
                .flatMap(part -> part.getFirstPatterns().get().stream()
                        .map(firstPattern -> part.getSecondPatterns().get().stream()
                                .map(secondPattern -> new StaffPartsInfo(part, firstPattern, secondPattern))))
                .flatMap(Function.identity())
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    public ModelIdentifier toModelId() {
        String suffix = part.getName();
        if (!firstPattern.isEmpty()) suffix += "_" + firstPattern.getName();
        if (!secondPattern.isEmpty()) suffix += "_" + secondPattern.getName();
        return EtherologyModels.createItemModelId("staff_" + suffix);
    }

    public Identifier toTextureId() {
        String suffix = part.getTextureSuffix();
        if (!firstPattern.isEmpty()) suffix += "_" + firstPattern.getName();
        if (!secondPattern.isEmpty()) suffix += "_" + secondPattern.getName();
        return new EIdentifier("item/staff_" + suffix);
    }

    static {
        NBT_TYPE = NbtKey.Type.of(NbtElement.COMPOUND_TYPE, (nbt, s) -> {
            NbtCompound root = nbt.getCompound(s);
            StaffParts part = StaffParts.valueOf(root.getString("part"));
            StaffPattern firstPattern = StaffPatterns.get(root.getString("first_pattern"));
            StaffPattern secondPattern = StaffPatterns.get(root.getString("second_pattern"));
            return new StaffPartsInfo(part, firstPattern, secondPattern);
        }, ((nbtCompound, s, partsInfo) -> {
            NbtCompound root = new NbtCompound();
            root.putString("part", partsInfo.getPart().getName());
            root.putString("first_pattern", partsInfo.getFirstPattern().getName());
            root.putString("second_pattern", partsInfo.getSecondPattern().getName());
            nbtCompound.put(s, root);
        }));
        LIST_KEY = new NbtKey.ListKey<>("parts", NBT_TYPE);
        NBT_KEY = new NbtKey<>("part_info", NBT_TYPE);
    }
}
