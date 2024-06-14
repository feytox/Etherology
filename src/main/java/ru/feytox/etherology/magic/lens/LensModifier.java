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
    public static final LensModifier AREA = register("area");
    public static final LensModifier SAVING = register("saving");

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