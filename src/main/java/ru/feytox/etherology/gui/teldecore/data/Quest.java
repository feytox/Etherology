package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.task.AbstractTask;
import ru.feytox.etherology.gui.teldecore.task.BiomeTask;
import ru.feytox.etherology.gui.teldecore.task.ItemTask;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.List;
import java.util.Map;

public record Quest(String titleKey, List<AbstractTask> tasks, List<AbstractContent> contents) {

    private static final Map<String, MapCodec<? extends AbstractTask>> TASK_TYPES;
    private static final Codec<AbstractTask> TASK_CODEC;
    public static final Codec<Quest> CODEC;

    public static Quest createAndReload(String titleKey, List<AbstractTask> tasks, List<AbstractContent> contents) {
        Quest quest = new Quest(titleKey, tasks, contents);
        quest.tasks.forEach(AbstractTask::onLoad);
        return quest;
    }

    public boolean isCompleted(@Nullable PlayerEntity player) {
        if (player == null) return false;
        return tasks.stream().noneMatch(task -> !task.isCompleted(player));
    }

    public boolean tryComplete(ServerPlayerEntity player, Identifier chapterId) {
        if (!isCompleted(player)) return false;
        for (AbstractTask task : tasks) {
            if (task.shouldConsume() && !task.consume(player)) return false;
        }
        EtherologyComponents.TELDECORE.maybeGet(player).ifPresent(data -> data.addCompletedQuest(chapterId));
        return true;
    }

    static {
        TASK_TYPES = Map.of("item", ItemTask.CODEC, "biome", BiomeTask.CODEC);
        TASK_CODEC = Codec.STRING.dispatch(AbstractTask::getType, TASK_TYPES::get);

        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("title").forGetter(q -> q.titleKey),
                TASK_CODEC.listOf().fieldOf("tasks").forGetter(q -> q.tasks),
                Chapter.CONTENT_CODEC.listOf().fieldOf("content").forGetter(q -> q.contents)
        ).apply(instance, Quest::createAndReload));
    }

}
