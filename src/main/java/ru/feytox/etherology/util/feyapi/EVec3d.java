package ru.feytox.etherology.util.feyapi;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EVec3d {
    public static Vec3d of(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<Vec3d> listOf(Vec3d startPos, Vec3d endPos, double step) {
        List<Vec3d> result = new ArrayList<>();
        for (double x = Math.min(startPos.x, endPos.x); x <= Math.max(startPos.x, endPos.x); x += step) {
            for (double y = Math.min(startPos.y, endPos.y); y <= Math.max(startPos.y, endPos.y); y += step) {
                for (double z = Math.min(startPos.z, endPos.z); z <= Math.max(startPos.z, endPos.z); z += step) {
                    result.add(new Vec3d(x, y, z));
                }
            }
        }
        return result;
    }

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

    public static List<Vec3d> boxOf(Vec3d startPos, Vec3d endPos, double step, Direction except) {
        double x0 = Math.min(startPos.x, endPos.x);
        double y0 = Math.min(startPos.y, endPos.y);
        double z0 = Math.min(startPos.z, endPos.z);
        double x1 = Math.max(startPos.x, endPos.x);
        double y1 = Math.max(startPos.y, endPos.y);
        double z1 = Math.max(startPos.z, endPos.z);

        List<Vec3d> result = new ArrayList<>();
        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        if (except != null) directions.remove(except);

        for (Direction direction: directions) {
            // down direction
            Vec3d start = new Vec3d(x0, y0, z0);
            Vec3d end = new Vec3d(x1, y0, z1);
            switch (direction) {
                case UP -> {
                    start = new Vec3d(x0, y1, z0);
                    end = new Vec3d(x1, y1, z1);
                }
                case NORTH -> {
                    start = new Vec3d(x0, y0, z0);
                    end = new Vec3d(x1, y1, z0);
                }
                case SOUTH -> {
                    start = new Vec3d(x0, y0, z1);
                    end = new Vec3d(x1, y1, z1);
                }
                case WEST -> {
                    start = new Vec3d(x0, y0, z0);
                    end = new Vec3d(x0, y1, z1);
                }
                case EAST -> {
                    start = new Vec3d(x1, y0, z0);
                    end = new Vec3d(x1, y1, z1);
                }
            }
            result.addAll(listOf(start, end, step));
        }

        return result;
    }
}
