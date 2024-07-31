package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class TurnPageButton extends AbstractButton {

    private static final Identifier LEFT = EIdentifier.of("textures/gui/teldecore/icon/left_corner.png");
    private static final Identifier RIGHT = EIdentifier.of("textures/gui/teldecore/icon/right_corner.png");
    private static final Identifier LEFT_BIG = EIdentifier.of("textures/gui/teldecore/icon/left_big_corner.png");
    private static final Identifier RIGHT_BIG = EIdentifier.of("textures/gui/teldecore/icon/right_big_corner.png");

    protected final boolean isLeft;

    private TurnPageButton(TeldecoreScreen parent, boolean isLeft, Identifier texture, @Nullable Identifier hoveredTexture, float dx, float dy, int width, int height) {
        super(parent, texture, hoveredTexture, dx, dy, width, height);
        this.isLeft = isLeft;
    }

    public static TurnPageButton of(TeldecoreScreen parent, boolean isLeft) {
        Identifier texture = isLeft ? LEFT : RIGHT;
        Identifier hoveredTexture = isLeft ? LEFT_BIG : RIGHT_BIG;
        return new TurnPageButton(parent, isLeft, texture, hoveredTexture, isLeft ? 10 : 276, 182, 24, 20);
    }

    @Override
    public boolean onClick(int button) {
        parent.getData().ifPresentOrElse(data -> {
            data.turnPage(isLeft);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        }, () -> Etherology.ELOGGER.error("Failed to turn the page."));

        return true;
    }
}
