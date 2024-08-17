package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.gui.teldecore.misc.TreeLine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

    public List<ChapterButton> toButtons(TeldecoreScreen parent, TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float rootX, float rootY, float scale) {
        return chapterInfos.stream().map(info -> info.toButton(parent, data, idToChapter, rootX, rootY, scale))
                .filter(Optional::isPresent).map(Optional::get).toList();
    }

    public List<TreeLine> toLines(TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float scale) {
        return chapterInfos.stream().map(info -> info.toLines(data, idToChapter, infoMap, scale))
                .filter(Optional::isPresent).flatMap(Optional::get)
                .filter(line -> !line.start().equals(line.end())).toList();
    }

    private record ChapterInfo(Identifier id, List<Identifier> after, float x, float y) {

        Optional<ChapterButton> toButton(TeldecoreScreen parent, TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float rootX, float rootY, float scale) {
            Chapter chapter = idToChapter.apply(id);
            if (chapter == null || !chapter.isAvailable(data)) return Optional.empty();

            Identifier texture = chapter.getType().getTexture();
            ItemStack icon = Registries.ITEM.get(chapter.getIcon()).getDefaultStack();
            Text title = Text.translatable(chapter.getTitleKey()).formatted(Formatting.WHITE);
            Text desc = Text.translatable(chapter.getDescKey()).formatted(Formatting.GRAY);
            boolean wasOpened = data.wasOpened(id);
            boolean isSubTab = chapter.getSubTab().isPresent();
            Identifier target = chapter.getSubTab().orElse(id);
            return Optional.of(new ChapterButton(parent, texture, target, icon, List.of(title, desc), wasOpened, isSubTab, rootX, rootY, x*scale, y*scale));
        }

        Optional<Stream<TreeLine>> toLines(TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, Map<Identifier, ChapterInfo> infoMap, float scale) {
            Chapter chapter = idToChapter.apply(id);
            if (!chapter.isAvailable(data)) return Optional.empty();

            boolean glowing = chapter.getQuest().isPresent() && !data.isCompleted(id);
            return Optional.of(after.stream().map(infoMap::get).flatMap(src -> Stream.of(
                    // vertical line
                    new TreeLine(new Vec2f(src.x*scale, src.y*scale), new Vec2f(x*scale, src.y*scale), glowing),
                    // horizontal line
                    new TreeLine(new Vec2f(x*scale, src.y*scale), new Vec2f(x*scale, y*scale), glowing))));
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
