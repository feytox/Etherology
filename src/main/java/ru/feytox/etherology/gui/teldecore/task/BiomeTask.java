package ru.feytox.etherology.gui.teldecore.task;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.data.VisitedComponent;

@RequiredArgsConstructor
@Getter
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

    static {
        CODEC = Identifier.CODEC.xmap(BiomeTask::new, t -> t.biomeId).fieldOf("id");
    }
}
