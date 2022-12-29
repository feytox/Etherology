package name.uwu.feytox.etherology.enums;

import name.uwu.feytox.etherology.Etherology;
import name.uwu.feytox.etherology.util.EIdentifier;
import name.uwu.feytox.etherology.util.FakeItem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public enum MixTypes {
    CLEAR(null),
    RED(new EIdentifier("textures/gui/teldecore/mix_red.png")),
    YELLOW(new EIdentifier("textures/gui/teldecore/mix_yellow.png")),
    MAGENTA(new EIdentifier("textures/gui/teldecore/mix_magenta.png")),
    BLUE(new EIdentifier("textures/gui/teldecore/mix_blue.png")),
    CYAN(new EIdentifier("textures/gui/teldecore/mix_cyan.png")),
    GREEN(new EIdentifier("textures/gui/teldecore/mix_green.png"));

    private final Identifier mixCraftTexture;

    MixTypes(Identifier mixCraftTexture) {
        this.mixCraftTexture = mixCraftTexture;
    }

    public Identifier getCraftTexture() {
        return this.mixCraftTexture;
    }

    public Text getTooltip() {
        return Text.translatable(Etherology.MOD_ID + "." + this.getLangKey());
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
