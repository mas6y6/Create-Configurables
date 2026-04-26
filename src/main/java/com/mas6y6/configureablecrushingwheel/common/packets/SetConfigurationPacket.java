package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SetConfigurationPacket(CrushingWheelsConfig config) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetConfigurationPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Configureablecrushingwheel.MODID,
                            "set_configuration"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, SetConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CrushingWheelsConfig.STREAM_CODEC, SetConfigurationPacket::config,
                    SetConfigurationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}