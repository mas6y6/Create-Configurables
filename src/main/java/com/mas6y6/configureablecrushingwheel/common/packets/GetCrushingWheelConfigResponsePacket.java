package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record GetCrushingWheelConfigResponsePacket(UUID uuid, CrushingWheelsConfig config) implements CustomPacketPayload {
    public static final Type<GetCrushingWheelConfigResponsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_crushing_wheel_config_response"));

    public static final StreamCodec<FriendlyByteBuf, GetCrushingWheelConfigResponsePacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet ) -> {
                buf.writeUUID(packet.uuid);
                CrushingWheelsConfig.STREAM_CODEC.encode(buf, packet.config);
            },
            (buf) -> {
                return new GetCrushingWheelConfigResponsePacket(buf.readUUID(),CrushingWheelsConfig.STREAM_CODEC.decode(buf));
            }
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
