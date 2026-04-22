package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CheckCrushingWheelsLinkPacketResponseFalse(BlockPos pos) implements CustomPacketPayload {
    public static final Type<CheckCrushingWheelsLinkPacketResponseFalse> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "check_link_crushing_wheels_response_false"));

    public static final StreamCodec<FriendlyByteBuf, CheckCrushingWheelsLinkPacketResponseFalse> STREAM_CODEC = StreamCodec.of(
            CheckCrushingWheelsLinkPacketResponseFalse::write,
            CheckCrushingWheelsLinkPacketResponseFalse::read
    );

    public static void write(FriendlyByteBuf buf, CheckCrushingWheelsLinkPacketResponseFalse packet) {
        buf.writeBlockPos(packet.pos);
    }

    public static CheckCrushingWheelsLinkPacketResponseFalse read(FriendlyByteBuf buf) {
        return new CheckCrushingWheelsLinkPacketResponseFalse(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
