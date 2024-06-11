package ru.feytox.etherology.magic.lens;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Map;

public record LensModifier(Identifier modifierId) {

    // TODO: 04.06.2024 consider to use vanilla registry
    // registry
    private static final Map<Identifier, LensModifier> MODIFIERS = new Object2ObjectOpenHashMap<>();

    // modifiers
    public static final LensModifier STREAM = register("stream");
    public static final LensModifier CHARGE = register("charge");
    public static final LensModifier FILTERING = register("filtering");
    public static final LensModifier CONCENTRATION = register("concentration");
    public static final LensModifier REINFORCEMENT = register("reinforcement");

    // TODO: 11.06.2024 rename or move
    // modifiers constants
    public static final float STREAM_MODIFIER = 0.1f;
    public static final float CHARGE_COOLDOWN_MODIFIER = 0.15f;
    public static final float CHARGE_SPEED_MODIFIER = 0.1f;
    public static final float FILTERING_PER_LEVEL = 0.4f;
    public static final float REINFORCEMENT_MODIFIER = 0.65f;

    @Nullable
    public static LensModifier get(Identifier id) {
        return MODIFIERS.getOrDefault(id, null);
    }

    private static LensModifier register(String name) {
        val modifier = new LensModifier(new EIdentifier(name));
        MODIFIERS.put(modifier.modifierId, modifier);
        return modifier;
    }

    public static void registerAll() {}
}
