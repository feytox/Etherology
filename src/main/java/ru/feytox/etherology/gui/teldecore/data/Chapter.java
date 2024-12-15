package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.content.ImageContent;
import ru.feytox.etherology.gui.teldecore.content.RecipeContent;
import ru.feytox.etherology.gui.teldecore.content.TextContent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Chapter(ChapterType type, Identifier icon, String titleKey, String descKey, List<Identifier> requirements,
                      List<AbstractContent> contents, Optional<Quest> quest, Optional<Identifier> subTab) {

    private static final Map<String, MapCodec<? extends AbstractContent>> CONTENT_TYPES;
    public static final Codec<AbstractContent> CONTENT_CODEC;
    public static final Codec<Chapter> CODEC;

    public void tryCompleteQuest(ServerPlayerEntity player, Identifier chapterId) {
        quest.ifPresentOrElse(quest -> {
            if (quest.tryComplete(player, chapterId))
                return;
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
