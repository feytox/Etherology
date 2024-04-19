package ru.feytox.etherology.block.armillary;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

import static ru.feytox.etherology.registry.util.EtherSounds.MATRIX_WORK;

public class ArmillaryMatrixSoundInstance extends MovingSoundInstance {
    private final ArmillaryMatrixBlockEntity armillary;
    private final ClientPlayerEntity player;
    private float fading = 0.0f;

    protected ArmillaryMatrixSoundInstance(ArmillaryMatrixBlockEntity armillary, ClientPlayerEntity player) {
        super(MATRIX_WORK, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.armillary = armillary;
        this.player = player;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() {
        if (armillary.isRemoved()) {
            fading = Math.max(0, this.fading - 0.05f);
            if (fading == 0) {
                setDone();
                return;
            }
        } else if (fading < 1) {
            fading = Math.min(1, this.fading + 0.05f);
        }

        double distance = player.squaredDistanceTo(armillary.getCenterPos());
        volume = (float) (fading * 4 / distance);
    }
}
