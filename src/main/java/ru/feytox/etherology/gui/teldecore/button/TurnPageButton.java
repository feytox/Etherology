package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class TurnPageButton extends AbstractButton {

    private static final Identifier LEFT = EIdentifier.of("textures/gui/teldecore/icon/left_corner.png");
    private static final Identifier RIGHT = EIdentifier.of("textures/gui/teldecore/icon/right_corner.png");
    private static final Identifier LEFT_BIG = EIdentifier.of("textures/gui/teldecore/icon/left_big_corner.png");
    private static final Identifier RIGHT_BIG = EIdentifier.of("textures/gui/teldecore/icon/right_big_corner.png");

    private final boolean isLeft;
    private final KeyBinding turnKey;

    private TurnPageButton(TeldecoreScreen parent, boolean isLeft, Identifier texture, @Nullable Identifier hoveredTexture, float dx, float dy, int width, int height) {
        super(parent, texture, hoveredTexture, dx, dy, width, height);
        this.isLeft = isLeft;
        GameOptions options = MinecraftClient.getInstance().options;
        this.turnKey = isLeft ? options.leftKey : options.rightKey;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!active) return false;
        if (mouseX < baseX - (isLeft ? 0 : 24) || mouseY < baseY - 24) return false;
        return mouseX <= baseX + width + (isLeft ? 24 : 0) && mouseY <= baseY + height;
    }

    public static TurnPageButton of(TeldecoreScreen parent, boolean isLeft) {
        Identifier texture = isLeft ? LEFT : RIGHT;
        Identifier hoveredTexture = isLeft ? LEFT_BIG : RIGHT_BIG;
        return new TurnPageButton(parent, isLeft, texture, hoveredTexture, isLeft ? 10 : 276, 182, 24, 20);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (turnKey.matchesKey(keyCode, scanCode)) return onClick(0);
        return false;
    }

    @Override
    public boolean onClick(int button) {
        return dataAction("Failed to turn the page.", data -> {
            data.turnPage(isLeft);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}
