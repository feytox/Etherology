package ru.feytox.etherology.animation.playerAnimation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain = true)
@Setter
@Getter
public class PartsInfo {
    private boolean head;
    private boolean body;
    private boolean rightArm;
    private boolean leftArm;
    private boolean rightLeg;
    private boolean leftLeg;
}
