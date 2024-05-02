package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EtherSounds {
    public static final SoundEvent ELECTRICITY = register("electricity_sound");
    public static final SoundEvent MATRIX_WORK = register("matrix_idle_sound");
    public static final SoundEvent DEFLECT = register("deflect");
    public static final SoundEvent BUBBLES = register("bubbles");
    public static final SoundEvent POUF = register("pouf");
    public static final SoundEvent RATCHET = register("ratchet");
    public static final SoundEvent BREWING_DISSOLUTION = register("brewing_dissolution");
    public static final SoundEvent THUNDER_ZAP = register("thunder_zap");
    public static final SoundEvent TUNING_MACE = register("tuning_mace");

    public static void registerAll() {}

    private static SoundEvent register(String id) {
        Identifier identifier = new EIdentifier(id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
