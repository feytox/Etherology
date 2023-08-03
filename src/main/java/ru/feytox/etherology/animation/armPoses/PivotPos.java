package ru.feytox.etherology.animation.armPoses;

import org.joml.Vector3f;

public class PivotPos extends Vector3f {

    public PivotPos() {
        super();
    }

    public void setX(float value) {
        this.set(value, this.y, this.z);
    }

    public void setY(float value) {
        this.set(this.x, value, this.z);
    }

    public void setZ(float value) {
        this.set(this.x, this.y, value);
    }
}
