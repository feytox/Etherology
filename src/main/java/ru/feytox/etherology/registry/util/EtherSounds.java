package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class EtherSounds {
    public static final SoundEvent ELECTRICITY = register("electricity_sound");
    public static final SoundEvent MATRIX_WORK = register("matrix_work_sound");
    public static final SoundEvent HAMMER_SWING = register("hammer_swing_sound");
    public static final SoundEvent HAMMER_DAMAGE = register("hammer_damage_sound");
    public static final SoundEvent THUNDER_ZAP = register("thunder_zap_sound");
    public static final SoundEvent DEFLECT = register("deflect");

    public static void registerAll() {}

    private static SoundEvent register(String id) {
        Identifier identifier = new EIdentifier(id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
