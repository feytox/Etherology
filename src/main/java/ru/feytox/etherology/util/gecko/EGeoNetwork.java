package ru.feytox.etherology.util.gecko;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.blocks.armillar.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.blocks.ringMatrix.RingMatrixBlockEntity;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EGeoNetwork {
    public static final EIdentifier STOP_ANIM_PACKET_ID = new EIdentifier("stop_anim_packet");
    public static final EIdentifier START_ANIM_PACKET_ID = new EIdentifier("start_anim_packet");
    public static final EIdentifier START_MATRIX_PACKET_ID = new EIdentifier("start_matrix_packet");

    @Environment(EnvType.CLIENT)
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(STOP_ANIM_PACKET_ID, ((client, handler, buf, responseSender) -> {
            String animName = buf.readString();
            BlockPos blockPos = buf.readBlockPos();

            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeoBlockEntity geoBlock) {
                geoBlock.stopAnim(animName);
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(START_MATRIX_PACKET_ID, (((client, handler, buf, responseSender) -> {
            BlockPos blockPos = buf.readBlockPos();

            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof ArmillaryMatrixBlockEntity armBlock) {
                RingMatrixBlockEntity ringMatrix = armBlock.getRingMatrix(client.world);

                ringMatrix.stopAnim("inactively");
                ringMatrix.triggerAnim("start");
            }
        })));

        ClientPlayNetworking.registerGlobalReceiver(START_ANIM_PACKET_ID, (((client, handler, buf, responseSender) -> {
            String animName = buf.readString();
            BlockPos blockPos = buf.readBlockPos();

            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeoBlockEntity eGeoBlock) {
                eGeoBlock.triggerAnim(animName);
            }
        })));
    }

    public static void sendStartAnim(ServerWorld world, BlockPos blockPos, String animName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(animName);
        buf.writeBlockPos(blockPos);

        for (ServerPlayerEntity player : PlayerLookup.around(world, blockPos, 50)) {
            ServerPlayNetworking.send(player, START_ANIM_PACKET_ID, buf);
        }
    }

    public static void sendStopAnim(ServerWorld world, BlockPos blockPos, String animName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(animName);
        buf.writeBlockPos(blockPos);

        for (ServerPlayerEntity player : PlayerLookup.around(world, blockPos, 50)) {
            ServerPlayNetworking.send(player, STOP_ANIM_PACKET_ID, buf);
        }
    }

    public static void sendStartMatrix(ServerWorld world, ArmillaryMatrixBlockEntity armBlock) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(armBlock.getPos());

        for (ServerPlayerEntity player : PlayerLookup.around(world, armBlock.getPos(), 50)) {
            ServerPlayNetworking.send(player, START_MATRIX_PACKET_ID, buf);
        }
    }
}
