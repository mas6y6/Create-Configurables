package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CheckCrushingWheelsLinkPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CheckCrushingWheelsLinkPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "check_link_crushing_wheels"));

    public static final StreamCodec<FriendlyByteBuf, CheckCrushingWheelsLinkPacket> STREAM_CODEC = StreamCodec.of(
            CheckCrushingWheelsLinkPacket::write,
            CheckCrushingWheelsLinkPacket::read
    );

    public static void write(FriendlyByteBuf buf, CheckCrushingWheelsLinkPacket packet) {
        buf.writeBlockPos(packet.pos);
    }

    public static CheckCrushingWheelsLinkPacket read(FriendlyByteBuf buf) {
        return new CheckCrushingWheelsLinkPacket(buf.readBlockPos());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
