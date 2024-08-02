package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChapterGrid {

    private static final Codec<ChapterInfo> INFO_CODEC;
    public static final Codec<ChapterGrid> CODEC;
    private final List<ChapterInfo> info;
    private final Map<Identifier, ChapterInfo> infoMap;

    private ChapterGrid(List<ChapterInfo> info) {
        this.info = info;
        this.infoMap = info.stream().collect(Collectors.toMap(ChapterInfo::id, Function.identity()));
    }

    public List<ChapterButton> toButtons(TeldecoreScreen parent, Function<Identifier, ItemStack> idToIcon, float rootX, float rootY, float scale) {
        return info.stream().map(info -> info.toButton(parent, idToIcon, rootX, rootY, scale)).toList();
    }

    public List<Pair<Vec2f, Vec2f>> toLines(float scale) {
        return info.stream().flatMap(info -> info.toLines(infoMap, scale))
                .filter(line -> !line.first().equals(line.second())).toList();
    }

    private record ChapterInfo(Identifier id, List<Identifier> after, float x, float y) {

        ChapterButton toButton(TeldecoreScreen parent, Function<Identifier, ItemStack> idToIcon, float rootX, float rootY, float scale) {
            return new ChapterButton(parent, id, idToIcon.apply(id), rootX, rootY, x*scale, y*scale);
        }

        Stream<Pair<Vec2f, Vec2f>> toLines(Map<Identifier, ChapterInfo> infoMap, float scale) {
            return after.stream().map(infoMap::get).flatMap(src -> Stream.of(
                    // vertical line
                    Pair.of(new Vec2f(src.x*scale, src.y*scale), new Vec2f(x*scale, src.y*scale)),
                    // horizontal line
                    Pair.of(new Vec2f(x*scale, src.y*scale), new Vec2f(x*scale, y*scale)
            )));
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
