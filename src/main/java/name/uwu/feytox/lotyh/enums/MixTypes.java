package name.uwu.feytox.lotyh.enums;

import name.uwu.feytox.lotyh.util.FakeItem;
import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.Lotyh;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public enum MixTypes {
    CLEAR(null),
    RED(new LIdentifier("textures/gui/teldecore/mix_red.png")),
    YELLOW(new LIdentifier("textures/gui/teldecore/mix_yellow.png")),
    MAGENTA(new LIdentifier("textures/gui/teldecore/mix_magenta.png")),
    BLUE(new LIdentifier("textures/gui/teldecore/mix_blue.png")),
    CYAN(new LIdentifier("textures/gui/teldecore/mix_cyan.png")),
    GREEN(new LIdentifier("textures/gui/teldecore/mix_green.png"));

    private final Identifier mixCraftTexture;

    MixTypes(Identifier mixCraftTexture) {
        this.mixCraftTexture = mixCraftTexture;
    }

    public Identifier getCraftTexture() {
        return this.mixCraftTexture;
    }

    public Text getTooltip() {
        return Text.translatable(Lotyh.MOD_ID + "." + this.getLangKey());
    }

    public static MixTypes getNextMix(int lastMix) {
        List<MixTypes> mixes = Arrays.stream(MixTypes.values()).toList();
        return mixes.get(getNextMixId(lastMix));
    }

    public String getLangKey() {
        return "mix_" + this.name().toLowerCase();
    }

    public static MixTypes getByNum(int mixNum) {
        List<MixTypes> mixes = Arrays.stream(MixTypes.values()).toList();
        return mixes.get(mixNum);
    }

    public static int getNextMixId(int lastMix) {
        List<MixTypes> mixes = Arrays.stream(MixTypes.values()).toList();
        lastMix += 1;
        if (lastMix >= mixes.size()) {
            lastMix = 0;
        }
        return lastMix;
    }

    public static void registerMixes() {
        List<MixTypes> mixes = Arrays.stream(MixTypes.values()).toList();
        mixes.forEach(mixType -> new FakeItem(mixType.getLangKey()).register());
    }
}
