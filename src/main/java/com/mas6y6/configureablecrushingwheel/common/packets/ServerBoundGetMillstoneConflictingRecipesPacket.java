package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ServerBoundGetMillstoneConflictingRecipesPacket() implements CustomPacketPayload {
    public static final Type<ServerBoundGetMillstoneConflictingRecipesPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_milling_conflicting_recipes"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundGetMillstoneConflictingRecipesPacket> STREAM_CODEC = StreamCodec.unit(new ServerBoundGetMillstoneConflictingRecipesPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
