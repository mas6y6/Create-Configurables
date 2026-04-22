package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LinkCrushingWheelsPacket(BlockPos a, BlockPos b) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LinkCrushingWheelsPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "link_crushing_wheels"));

    public static final StreamCodec<FriendlyByteBuf, LinkCrushingWheelsPacket> STREAM_CODEC = StreamCodec.of(
            LinkCrushingWheelsPacket::write,
            LinkCrushingWheelsPacket::read
    );

    public static void write(FriendlyByteBuf buf, LinkCrushingWheelsPacket packet) {
        buf.writeBlockPos(packet.a);
        buf.writeBlockPos(packet.b);
    }

    public static LinkCrushingWheelsPacket read(FriendlyByteBuf buf) {
        return new LinkCrushingWheelsPacket(buf.readBlockPos(), buf.readBlockPos());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}