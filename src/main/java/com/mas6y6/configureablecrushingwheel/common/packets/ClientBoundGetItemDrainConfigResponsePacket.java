package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.ItemDrainConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ClientBoundGetItemDrainConfigResponsePacket(UUID uuid, ItemDrainConfig config) implements CustomPacketPayload {
    public static final Type<ClientBoundGetItemDrainConfigResponsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_item_drain_config_response"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundGetItemDrainConfigResponsePacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet ) -> {
                buf.writeUUID(packet.uuid);
                ItemDrainConfig.STREAM_CODEC.encode(buf, packet.config);
            },
            (buf) -> {
                return new ClientBoundGetItemDrainConfigResponsePacket(buf.readUUID(), ItemDrainConfig.STREAM_CODEC.decode(buf));
            }
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
