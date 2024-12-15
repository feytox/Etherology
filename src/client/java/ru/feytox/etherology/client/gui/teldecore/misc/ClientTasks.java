package ru.feytox.etherology.client.gui.teldecore.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.feytox.etherology.gui.teldecore.task.AbstractTask;
import ru.feytox.etherology.gui.teldecore.task.BiomeTask;
import ru.feytox.etherology.gui.teldecore.task.ItemTask;

public class ClientTasks {

    public static FeySlot toSlot(AbstractTask abstractTask, float x, float y, float width, float height) {
        return switch (abstractTask) {
            case BiomeTask task -> ofBiomeTask(task, x, y, width, height);
            case ItemTask task -> new FeySlot.ItemTask(new ItemStack(task.getItem(), task.getCount()), isClientCompleted(task), x, y, width, height);
            default -> throw new IllegalStateException("Unexpected value: " + abstractTask);
        };
    }

    private static FeySlot ofBiomeTask(BiomeTask task, float x, float y, float width, float height) {
        var biomeText = Text.translatable(task.getBiomeId().toTranslationKey("biome"));
        return new FeySlot.SearchTask(biomeText, isClientCompleted(task), x, y, width, height);
    }

    private static boolean isClientCompleted(AbstractTask task) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && task.isCompleted(player);
    }
}
