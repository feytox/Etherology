package ru.feytox.etherology.deprecated.armillar;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import ru.feytox.etherology.enums.ArmillarStateType;

import static ru.feytox.etherology.enums.ArmillarStateType.*;
import static ru.feytox.etherology.registry.util.EtherSounds.MATRIX_WORK;

public class OldMatrixWorkSoundInstance extends MovingSoundInstance {
    private final OldArmillaryMatrixBlockEntity armBlock;
    private final ClientPlayerEntity player;
    private double fading = 0.0f;

    protected OldMatrixWorkSoundInstance(OldArmillaryMatrixBlockEntity armBlock, ClientPlayerEntity player) {
        super(MATRIX_WORK, SoundCategory.BLOCKS, SoundInstance.createRandom());
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
