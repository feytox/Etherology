package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;

import java.util.List;
import java.util.function.Function;

public class ChapterGrid {

    private static final Codec<ChapterInfo> INFO_CODEC;
    public static final Codec<ChapterGrid> CODEC;
    private final List<ChapterInfo> info;

    private ChapterGrid(List<ChapterInfo> info) {
        this.info = info;
    }

    public List<ChapterButton> toButtons(TeldecoreScreen parent, Function<Identifier, ItemStack> idToIcon, float rootX, float rootY, float scale) {
        return info.stream().map(info -> info.toButton(parent, idToIcon, rootX, rootY, scale)).toList();
    }

    private record ChapterInfo(Identifier id, List<Identifier> after, float x, float y) {

        ChapterButton toButton(TeldecoreScreen parent, Function<Identifier, ItemStack> idToIcon, float rootX, float rootY, float scale) {
            return new ChapterButton(parent, id, idToIcon.apply(id), rootX, rootY, x*scale, y*scale);
        }
    }

    static {
        INFO_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(ChapterInfo::id),
                Identifier.CODEC.listOf().optionalFieldOf("after", new ObjectArrayList<>()).forGetter(ChapterInfo::after),
                Codec.FLOAT.fieldOf("x").forGetter(ChapterInfo::x),
                Codec.FLOAT.fieldOf("y").forGetter(ChapterInfo::y)
        ).apply(instance, ChapterInfo::new));
        CODEC = INFO_CODEC.listOf().xmap(ChapterGrid::new, grid -> grid.info);
    }
}
