package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.content.ImageContent;
import ru.feytox.etherology.gui.teldecore.content.RecipeContent;
import ru.feytox.etherology.gui.teldecore.content.TextContent;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.gui.teldecore.page.EmptyPage;
import ru.feytox.etherology.gui.teldecore.page.QuestPage;
import ru.feytox.etherology.gui.teldecore.page.TitlePage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@RequiredArgsConstructor
public class Chapter {

    private static final Map<String, MapCodec<? extends AbstractContent>> CONTENT_TYPES;
    public static final Codec<AbstractContent> CONTENT_CODEC;
    public static final Codec<Chapter> CODEC;

    @Getter
    private final ChapterType type;
    @Getter
    private final Identifier icon;
    @Getter
    private final String titleKey;
    @Getter
    private final String descKey;
    private final List<Identifier> requirements;
    private final List<AbstractContent> contents;
    @Getter
    private final Optional<Quest> quest;
    @Getter
    private final Optional<Identifier> subTab;

    @Environment(EnvType.CLIENT)
    public List<AbstractPage> toPages(TeldecoreScreen screen, TeldecoreComponent data, Identifier chapterId) {
        List<AbstractPage> pages = new ObjectArrayList<>();
        Text title = Text.translatable(titleKey);

        quest.ifPresent(quest -> {
            if (data.isCompleted(chapterId)) return;
            QuestPage page = new QuestPage(screen, quest, chapterId, true);
            for (AbstractContent content : quest.contents()) {
                if (!page.addContent(content, 10)) Etherology.ELOGGER.error("Found a content in the chapter \"{}\", that doesn't fit in quest info.", title);
            }
            pages.add(page);
        });

        pages.add(new TitlePage(screen, title, pages.isEmpty(), true));

        for (AbstractContent content : contents) {
            AbstractPage lastPage = pages.getLast();
            if (lastPage.addContent(content, 10)) continue;
            EmptyPage page = new EmptyPage(screen, !lastPage.isLeft());
            if (!page.addContent(content, 10)) {
                Etherology.ELOGGER.error("Found a content in the chapter \"{}\", that doesn't fit anywhere.", title);
                return pages;
            }
            pages.add(page);
        }

        return pages;
    }

    public void tryCompleteQuest(ServerPlayerEntity player, Identifier chapterId) {
        quest.ifPresentOrElse(quest -> {
            if (quest.tryComplete(player, chapterId)) return;
            Etherology.ELOGGER.error("Failed to complete a quest from the chapter {}.", chapterId.toString());
        }, () -> Etherology.ELOGGER.error("Unexpected attempt to complete a quest from the chapter {} without a quest.", chapterId.toString()));
    }

    public boolean isAvailable(TeldecoreComponent data) {
        if (requirements.isEmpty()) return true;
        return requirements.stream().noneMatch(id -> !data.isCompleted(id));
    }

    static {
        CONTENT_TYPES = Map.of(
                "text", TextContent.CODEC, "image", ImageContent.CODEC, "recipe", RecipeContent.CODEC
        );
        CONTENT_CODEC = Codec.STRING.dispatch(AbstractContent::getType, CONTENT_TYPES::get);

        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ChapterType.CODEC.fieldOf("type").forGetter(c -> c.type),
                Identifier.CODEC.fieldOf("icon").forGetter(c -> c.icon),
                Codec.STRING.fieldOf("title").forGetter(c -> c.titleKey),
                Codec.STRING.fieldOf("desc").forGetter(c -> c.descKey),
                Identifier.CODEC.listOf().optionalFieldOf("require", List.of()).forGetter(c -> c.requirements),
                CONTENT_CODEC.listOf().optionalFieldOf("content", List.of()).forGetter(c -> c.contents),
                Quest.CODEC.optionalFieldOf("quest").forGetter(c -> c.quest),
                Identifier.CODEC.optionalFieldOf("sub_tab").forGetter(c -> c.subTab)
        ).apply(instance, Chapter::new));
    }
}
