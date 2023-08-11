package ru.feytox.etherology.util.deprecated;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class EVec3d {

    public static List<Vec3d> lineOf(Vec3d startPos, Vec3d endPos, double step) {
        Vec3d lineVec = endPos.subtract(startPos);
        double lineLen = lineVec.length();
        Vec3d stepVec = lineVec.multiply(step / lineLen);

        List<Vec3d> result = new ArrayList<>();
        for (int i = 1; i <= MathHelper.ceil(lineLen / step); i++) {
            result.add(startPos.add(stepVec.multiply(i)));
        }

        return result;
    }

    public static List<Vec3d> aroundSquareOf(Vec3d startPos, Vec3d endPos, double step) {
        double y = (startPos.y + endPos.y) / 2;
        Vec3d leftTop = new Vec3d(endPos.x, y, startPos.z);
        Vec3d rightBottom = new Vec3d(startPos.x, y, endPos.z);

        return aroundOf(startPos, leftTop, rightBottom, endPos, step);
    }

    public static List<Vec3d> aroundOf(Vec3d leftBottom, Vec3d leftTop, Vec3d rightBottom, Vec3d rightTop, double step) {
        List<Vec3d> result = new ArrayList<>();
        result.addAll(lineOf(leftBottom, leftTop, step));
        result.addAll(lineOf(rightBottom, rightTop, step));
        result.addAll(lineOf(leftBottom, rightBottom, step));
        result.addAll(lineOf(leftTop, rightTop, step));
        return result;
    }
}
