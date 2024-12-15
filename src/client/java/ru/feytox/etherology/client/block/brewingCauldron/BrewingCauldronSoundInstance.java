package ru.feytox.etherology.client.block.brewingCauldron;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity;
import ru.feytox.etherology.registry.misc.EtherSounds;

public class BrewingCauldronSoundInstance extends MovingSoundInstance {

    private final BrewingCauldronBlockEntity cauldron;
    private final ClientPlayerEntity player;
    private float fading = 0.0f;

    protected BrewingCauldronSoundInstance(BrewingCauldronBlockEntity cauldron, ClientPlayerEntity player) {
        super(EtherSounds.BUBBLES, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.cauldron = cauldron;
        this.player = player;
        attenuationType = AttenuationType.NONE;
        repeat = false;
        repeatDelay = 0;

        var pos = cauldron.getPos();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void tick() {
        if (cauldron.isRemoved() || cauldron.getTemperature() < 100) {
            setDone();
            return;
        } else if (fading < 1) {
            fading = Math.min(1, this.fading + 0.05f);
        }

        double distance = player.squaredDistanceTo(cauldron.getPos().toCenterPos());
        volume = (float) (fading * 4 / distance);
    }
}
