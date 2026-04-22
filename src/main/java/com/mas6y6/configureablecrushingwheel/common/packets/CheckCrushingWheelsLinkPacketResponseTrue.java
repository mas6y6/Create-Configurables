package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CheckCrushingWheelsLinkPacketResponseTrue(BlockPos a, BlockPos b) implements CustomPacketPayload {
    public static final Type<CheckCrushingWheelsLinkPacketResponseTrue> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "check_link_crushing_wheels_response_true"));

    public static final StreamCodec<FriendlyByteBuf, CheckCrushingWheelsLinkPacketResponseTrue> STREAM_CODEC = StreamCodec.of(
            CheckCrushingWheelsLinkPacketResponseTrue::write,
            CheckCrushingWheelsLinkPacketResponseTrue::read
    );

    public static void write(FriendlyByteBuf buf, CheckCrushingWheelsLinkPacketResponseTrue packet) {
        buf.writeBlockPos(packet.a);
        buf.writeBlockPos(packet.b);
    }

    public static CheckCrushingWheelsLinkPacketResponseTrue read(FriendlyByteBuf buf) {
        return new CheckCrushingWheelsLinkPacketResponseTrue(buf.readBlockPos(),buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
