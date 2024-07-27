package ru.feytox.etherology.block.matrix;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

import static ru.feytox.etherology.registry.misc.EtherSounds.MATRIX_WORK;

public class MatrixSoundInstance extends MovingSoundInstance {
    private final MatrixBlockEntity armillary;
    private final ClientPlayerEntity player;
    private float fading = 0.0f;

    protected MatrixSoundInstance(MatrixBlockEntity armillary, ClientPlayerEntity player) {
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
