package ru.feytox.etherology.gui.teldecore.task;

import net.minecraft.entity.player.PlayerEntity;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;

public abstract class AbstractTask {

    public abstract boolean isCompleted(PlayerEntity player);
    public abstract String getType();
    public abstract boolean shouldConsume();
    public abstract boolean consume(PlayerEntity player);
    public abstract FeySlot toSlot(float x, float y, float width, float height);
}
