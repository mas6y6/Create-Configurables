package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerBoundSetMillstoneConfigurationPacket(MillstoneConfig config) implements CustomPacketPayload {

    public static final Type<ServerBoundSetMillstoneConfigurationPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Configureablecrushingwheel.MODID,
                            "set_millstone_configuration"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, ServerBoundSetMillstoneConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    MillstoneConfig.STREAM_CODEC, ServerBoundSetMillstoneConfigurationPacket::config,
                    ServerBoundSetMillstoneConfigurationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}