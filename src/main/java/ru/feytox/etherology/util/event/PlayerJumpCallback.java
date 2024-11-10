package ru.feytox.etherology.util.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerJumpCallback {

    Event<PlayerJumpCallback> BEFORE_JUMP = EventFactory.createArrayBacked(PlayerJumpCallback.class,
            (listeners) -> (player) -> {
                for (var listener : listeners) {
                    var result = listener.beforeJump(player);
                    if (result != ActionResult.PASS)
                        return result;
                }

                return ActionResult.PASS;
            });

    ActionResult beforeJump(PlayerEntity player);
}
