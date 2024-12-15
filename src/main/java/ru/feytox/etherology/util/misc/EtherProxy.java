package ru.feytox.etherology.util.misc;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.network.util.AbstractC2SPacket;

// TODO: 08.12.2024 use something better
public abstract class EtherProxy {

    @Nullable
    private static EtherProxy instance;

    public static void setProxy(EtherProxy proxy) {
        if (instance != null)
            throw new NullPointerException("Proxy has already been initialized");
        instance = proxy;
    }

    public static EtherProxy getInstance() {
        if (instance == null)
            throw new NullPointerException("Proxy was not initialized, or you tried to run client code on the server-side.");
        return instance;
    }

    public abstract void tickStaff(ItemStack stack, PlayerEntity player);

    public abstract void tickRevelationView(World world, PlayerEntity player);

    public abstract void tickOculus(World world, boolean selected);

    public abstract <T extends TickableBlockEntity> boolean tryTickBlockEntity(T blockEntity, World world, BlockPos blockPos, BlockState state);

    public abstract void playSound(Vec3d pos, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, boolean useDistance);

    public abstract void sendToServer(AbstractC2SPacket packet);

    public abstract void openTeldecoreScreen();
}
