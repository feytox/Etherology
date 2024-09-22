package ru.feytox.etherology.gui.teldecore.task;

import com.mojang.serialization.MapCodec;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.gui.teldecore.misc.VisitedComponent;
import ru.feytox.etherology.gui.teldecore.page.TaskType;

@RequiredArgsConstructor
public class BiomeTask extends AbstractTask {

    public static final MapCodec<BiomeTask> CODEC;

    private final Identifier biomeId;

    @Override
    public void onLoad() {
        VisitedComponent.addBiomeListener(biomeId);
    }

    @Override
    public boolean isCompleted(PlayerEntity player) {
        return VisitedComponent.isVisited(player, biomeId);
    }

    @Override
    public String getType() {
        return "biome";
    }

    @Override
    public boolean shouldConsume() {
        return false;
    }

    @Override
    public boolean consume(PlayerEntity player) {
        return false;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SEARCH;
    }

    @Override
    public FeySlot toSlot(float x, float y, float width, float height) {
        Text biomeText = Text.translatable(biomeId.toTranslationKey("biome"));
        return new FeySlot.SearchTask(biomeText, isClientCompleted(), x, y, width, height);
    }

    static {
        CODEC = Identifier.CODEC.xmap(BiomeTask::new, t -> t.biomeId).fieldOf("id");
    }
}
