package ru.feytox.etherology.block.armillar;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import ru.feytox.etherology.enums.ArmillarStateType;

import static ru.feytox.etherology.enums.ArmillarStateType.*;
import static ru.feytox.etherology.registry.util.EtherSounds.MATRIX_WORK_SOUND_EVENT;

public class MatrixWorkSoundInstance extends MovingSoundInstance {
    private final ArmillaryMatrixBlockEntity armBlock;
    private final ClientPlayerEntity player;
    private double fading = 0.0f;

    protected MatrixWorkSoundInstance(ArmillaryMatrixBlockEntity armBlock, ClientPlayerEntity player) {
        super(MATRIX_WORK_SOUND_EVENT, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.armBlock = armBlock;
        this.player = player;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() {
        ArmillarStateType stateType = armBlock.getArmillarStateType();
        if (stateType.equalsAny(OFF, RAISING, LOWERING) || armBlock.isRemoved()) {
            fading = Math.max(0, this.fading - 1.0d/20d);
            if (fading == 0) {
                setDone();
                return;
            }
        } else if (fading < 1) {
            fading = Math.min(1, this.fading + 1.0d/20d);
        }

        double distance = player.squaredDistanceTo(armBlock.getCenterPos());
        volume = (float) ((4 / distance) * fading);
    }
}
