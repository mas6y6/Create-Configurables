package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ClientBoundGetMillstoneWheelConfigResponsePacket(UUID uuid, MillstoneConfig config) implements CustomPacketPayload {
    public static final Type<ClientBoundGetMillstoneWheelConfigResponsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_millstone_config_response"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundGetMillstoneWheelConfigResponsePacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet ) -> {
                buf.writeUUID(packet.uuid);
                MillstoneConfig.STREAM_CODEC.encode(buf, packet.config);
            },
            (buf) -> {
                return new ClientBoundGetMillstoneWheelConfigResponsePacket(buf.readUUID(),MillstoneConfig.STREAM_CODEC.decode(buf));
            }
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
