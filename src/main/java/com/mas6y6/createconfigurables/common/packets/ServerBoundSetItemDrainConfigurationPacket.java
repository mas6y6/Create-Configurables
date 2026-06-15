package com.mas6y6.createconfigurables.common.packets;

import com.mas6y6.createconfigurables.CreateConfigurables;
import com.mas6y6.createconfigurables.common.ItemDrainConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerBoundSetItemDrainConfigurationPacket(ItemDrainConfig config) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerBoundSetItemDrainConfigurationPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreateConfigurables.MODID,
                            "set_item_drain_configuration"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, ServerBoundSetItemDrainConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemDrainConfig.STREAM_CODEC, ServerBoundSetItemDrainConfigurationPacket::config,
                    ServerBoundSetItemDrainConfigurationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
