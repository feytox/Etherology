package name.uwu.feytox.etherology.magic.ether;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface EtherFork extends EtherPipe {
    List<Direction> getOutputSides();

    @Override
    default boolean canInputFrom(EtherStorage supplier) {
        return !(supplier instanceof EtherPipe);
    }

    @Override
    default boolean isInputSide(Direction side) {
        List<Direction> outputSides = getOutputSides();
        return !outputSides.contains(side);
    }

    @Override
    default void transfer(ServerWorld world) {
        float storedEther = getStoredEther();
        if (storedEther == 0 || !isActive()) return;

        List<Direction> outputSides = getOutputSides();
        outputSides.forEach(direction -> transferTo(world, direction));
    }
}
