package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LinkCrushingWheelsResponsePacket(BlockPos a, BlockPos b, Boolean success) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LinkCrushingWheelsResponsePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "link_crushing_wheels_response"));

    public static final StreamCodec<FriendlyByteBuf, LinkCrushingWheelsResponsePacket> STREAM_CODEC = StreamCodec.of(
            LinkCrushingWheelsResponsePacket::write,
            LinkCrushingWheelsResponsePacket::read
    );

    public static void write(FriendlyByteBuf buf, LinkCrushingWheelsResponsePacket packet) {
        buf.writeBlockPos(packet.a);
        buf.writeBlockPos(packet.b);
        buf.writeBoolean(packet.success);
    }

    public static LinkCrushingWheelsResponsePacket read(FriendlyByteBuf buf) {
        return new LinkCrushingWheelsResponsePacket(buf.readBlockPos(), buf.readBlockPos(), buf.readBoolean());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}