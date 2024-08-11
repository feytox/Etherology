package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResearchTree {

    private static final Codec<ChapterInfo> INFO_CODEC;
    public static final Codec<ResearchTree> CODEC;
    private final List<ChapterInfo> chapterInfos;
    private final Map<Identifier, ChapterInfo> infoMap;

    private ResearchTree(List<ChapterInfo> chapterInfos) {
        this.chapterInfos = chapterInfos;
        this.infoMap = chapterInfos.stream().collect(Collectors.toMap(ChapterInfo::id, Function.identity()));
    }

    public List<ChapterButton> toButtons(TeldecoreScreen parent, Function<Identifier, Chapter> idToChapter, Predicate<Identifier> chapterCheck, float rootX, float rootY, float scale) {
        return chapterInfos.stream().filter(info -> chapterCheck.test(info.id))
                .map(info -> info.toButton(parent, idToChapter, rootX, rootY, scale)).toList();
    }

    public List<Pair<Vec2f, Vec2f>> toLines(Predicate<Identifier> chapterCheck, float scale) {
        return chapterInfos.stream().filter(info -> chapterCheck.test(info.id)).flatMap(info -> info.toLines(infoMap, scale))
                .filter(line -> !line.first().equals(line.second())).toList();
    }

    private record ChapterInfo(Identifier id, List<Identifier> after, float x, float y) {

        ChapterButton toButton(TeldecoreScreen parent, Function<Identifier, Chapter> idToChapter, float rootX, float rootY, float scale) {
            Chapter chapter = idToChapter.apply(id);
            Identifier texture = chapter.getType().getTexture();
            ItemStack icon = Registries.ITEM.get(chapter.getIcon()).getDefaultStack();
            Text title = Text.translatable(chapter.getTitleKey()).formatted(Formatting.WHITE);
            Text desc = Text.translatable(chapter.getDescKey()).formatted(Formatting.GRAY);
            return new ChapterButton(parent, texture, id, icon, List.of(title, desc), rootX, rootY, x*scale, y*scale);
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
                Identifier.CODEC.listOf().optionalFieldOf("after", List.of()).forGetter(ChapterInfo::after),
                Codec.FLOAT.fieldOf("x").forGetter(ChapterInfo::x),
                Codec.FLOAT.fieldOf("y").forGetter(ChapterInfo::y)
        ).apply(instance, ChapterInfo::new));
        CODEC = INFO_CODEC.listOf().xmap(ResearchTree::new, grid -> grid.chapterInfos);
    }
}
