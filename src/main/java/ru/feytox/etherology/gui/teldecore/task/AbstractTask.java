package ru.feytox.etherology.gui.teldecore.task;

import net.minecraft.entity.player.PlayerEntity;

public abstract class AbstractTask {

    public abstract boolean isCompleted(PlayerEntity player);
    public abstract String getType();
    public abstract boolean shouldConsume();
    public abstract boolean consume(PlayerEntity player);
    public abstract TaskType getTaskType();

    public void onLoad() {}
}
