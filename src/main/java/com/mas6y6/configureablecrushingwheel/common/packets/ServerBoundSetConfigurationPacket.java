package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerBoundSetConfigurationPacket(CrushingWheelsConfig config) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerBoundSetConfigurationPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Configureablecrushingwheel.MODID,
                            "set_configuration"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, ServerBoundSetConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CrushingWheelsConfig.STREAM_CODEC, ServerBoundSetConfigurationPacket::config,
                    ServerBoundSetConfigurationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}