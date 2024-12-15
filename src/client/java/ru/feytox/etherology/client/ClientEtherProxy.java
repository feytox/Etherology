package ru.feytox.etherology.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.client.block.ClientBlockTickers;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.item.OculusItemClient;
import ru.feytox.etherology.client.item.StaffItemClient;
import ru.feytox.etherology.client.item.revelationView.RevelationViewItemClient;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.util.misc.EtherProxy;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

// TODO: 08.12.2024 use something better
public class ClientEtherProxy extends EtherProxy {

    @Override
    public void tickStaff(ItemStack stack, PlayerEntity player) {
        StaffItemClient.tickStaff(stack, player);
    }

    @Override
    public void tickRevelationView(World world, PlayerEntity player){
        RevelationViewItemClient.tickRevelationView(world, player);
    }

    @Override
    public void tickOculus(World world, boolean selected) {
        OculusItemClient.tickOculus(world, selected);
    }

    @Override
    public <T extends TickableBlockEntity> boolean tryTickBlockEntity(T blockEntity, World world, BlockPos blockPos, BlockState state) {
        return ClientBlockTickers.tryTick(blockEntity, (ClientWorld) world, blockPos, state);
    }

    @Override
    public void playSound(Vec3d pos, SoundEvent soundEvent, SoundCategory category, float volume, float pitch, boolean useDistance) {
        var player = MinecraftClient.getInstance().player;
        if (player == null || player.getWorld() == null)
            return;

        player.getWorld().playSound(pos.x, pos.y, pos.z, soundEvent, category, volume, pitch, useDistance);
    }

    @Override
    public void sendToServer(AbstractC2SPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void openTeldecoreScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new TeldecoreScreen(client.currentScreen));
    }
}
