package ru.feytox.etherology.gui.teldecore.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.gui.teldecore.page.TaskType;

public abstract class AbstractTask {

    public abstract boolean isCompleted(PlayerEntity player);
    public abstract String getType();
    public abstract boolean shouldConsume();
    public abstract boolean consume(PlayerEntity player);
    public abstract TaskType getTaskType();

    @Environment(EnvType.CLIENT)
    public abstract FeySlot toSlot(float x, float y, float width, float height);

    public void onLoad() {}

    @Environment(EnvType.CLIENT)
    public boolean isClientCompleted() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && isCompleted(player);
    }
}
