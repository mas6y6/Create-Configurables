package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ServerBoundGetCrushingConflictingRecipesPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerBoundGetCrushingConflictingRecipesPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_conflicting_recipes"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundGetCrushingConflictingRecipesPacket> STREAM_CODEC = StreamCodec.unit(new ServerBoundGetCrushingConflictingRecipesPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
