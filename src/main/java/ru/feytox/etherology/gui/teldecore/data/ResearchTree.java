package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.misc.TreeLine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ResearchTree {

    private static final Codec<ChapterInfo> INFO_CODEC;
    public static final Codec<ResearchTree> CODEC;
    private final List<ChapterInfo> chapterInfos;
    private final Map<Identifier, ChapterInfo> infoMap;

    private ResearchTree(List<ChapterInfo> chapterInfos) {
        this.chapterInfos = chapterInfos;
        this.infoMap = chapterInfos.stream().collect(Collectors.toMap(ChapterInfo::id, Function.identity()));
    }

    public record ChapterInfo(Identifier id, List<Identifier> after, float x, float y) {

        public Optional<Stream<TreeLine>> toLines(TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, Map<Identifier, ChapterInfo> infoMap, float scale) {
            Chapter chapter = idToChapter.apply(id);
            if (!chapter.isAvailable(data)) return Optional.empty();

            return Optional.of(after.stream().map(infoMap::get).flatMap(src -> Stream.of(
                    // vertical line
                    new TreeLine(new Vec2f(src.x*scale, src.y*scale), new Vec2f(x*scale, src.y*scale)),
                    // horizontal line
                    new TreeLine(new Vec2f(x*scale, src.y*scale), new Vec2f(x*scale, y*scale)))));
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
