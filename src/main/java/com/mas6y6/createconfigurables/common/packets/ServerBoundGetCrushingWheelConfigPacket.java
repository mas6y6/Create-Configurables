package com.mas6y6.createconfigurables.common.packets;

import com.mas6y6.createconfigurables.CreateConfigurables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerBoundGetCrushingWheelConfigPacket(UUID uuid) implements CustomPacketPayload {
    public static final Type<ServerBoundGetCrushingWheelConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateConfigurables.MODID, "get_crushing_wheel_config"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundGetCrushingWheelConfigPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet ) -> {
                buf.writeUUID(packet.uuid);
            },
            (buf) -> {
                return new ServerBoundGetCrushingWheelConfigPacket(buf.readUUID());
            }
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
