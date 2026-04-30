package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record GetMillstoneWheelConfigPacket(UUID uuid) implements CustomPacketPayload {
    public static final Type<GetMillstoneWheelConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_milling_wheel_config"));

    public static final StreamCodec<FriendlyByteBuf, GetMillstoneWheelConfigPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet ) -> {
                buf.writeUUID(packet.uuid);
            },
            (buf) -> {
                return new GetMillstoneWheelConfigPacket(buf.readUUID());
            }
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
