package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetMillstoneConfigurationPacket(MillstoneConfig config) implements CustomPacketPayload {

    public static final Type<SetMillstoneConfigurationPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Configureablecrushingwheel.MODID,
                            "set_millstone_configuration"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, SetMillstoneConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    MillstoneConfig.STREAM_CODEC, SetMillstoneConfigurationPacket::config,
                    SetMillstoneConfigurationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}