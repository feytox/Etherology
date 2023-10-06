package ru.feytox.etherology.magic.staff;

import io.wispforest.owo.nbt.NbtKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.EnumUtils;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StaffPartInfo {

    public static final NbtKey.Type<StaffPartInfo> NBT_TYPE;
    public static final NbtKey<StaffPartInfo> NBT_KEY;
    public static final NbtKey.ListKey<StaffPartInfo> LIST_KEY;

    @NonNull
    @Getter
    private final StaffPart part;

    @NonNull
    @Getter
    private final StaffPattern firstPattern;

    @NonNull
    @Getter
    private final StaffPattern secondPattern;

    public static List<StaffPartInfo> generateAll() {
        return Arrays.stream(StaffPart.values())
                .flatMap(part -> part.getFirstPatterns().get().stream()
                        .map(firstPattern -> part.getSecondPatterns().get().stream()
                                .map(secondPattern -> new StaffPartInfo(part, firstPattern, secondPattern))))
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
        String prefix = part.isStyled() ? "trims/textures/" : "item/";
        String suffix = part.isStyled() ? "style" : part.getName();
        if (!firstPattern.isEmpty()) suffix += "_" + firstPattern.getName();
        if (!secondPattern.isEmpty()) suffix += "_" + secondPattern.getName();
        return new EIdentifier(prefix + "staff_" + suffix);
    }

    static {
        NBT_TYPE = NbtKey.Type.of(NbtElement.COMPOUND_TYPE, (nbt, s) -> {
            StaffPart part = EnumUtils.getEnumIgnoreCase(StaffPart.class, nbt.getString("part"));
            StaffPattern firstPattern = StaffPatterns.get(nbt.getString("first_pattern"));
            StaffPattern secondPattern = StaffPatterns.get(nbt.getString("second_pattern"));
            return new StaffPartInfo(part, firstPattern, secondPattern);
        }, ((root, s, partsInfo) -> {
            root.putString("part", partsInfo.getPart().getName());
            root.putString("first_pattern", partsInfo.getFirstPattern().getName());
            root.putString("second_pattern", partsInfo.getSecondPattern().getName());
        }));
        LIST_KEY = new NbtKey.ListKey<>("parts", NBT_TYPE);
        NBT_KEY = new NbtKey<>("", NBT_TYPE);
    }
}
