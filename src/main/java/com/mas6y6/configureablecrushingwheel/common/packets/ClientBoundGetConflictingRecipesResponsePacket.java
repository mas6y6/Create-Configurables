package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientBoundGetConflictingRecipesResponsePacket(RecipeConflicts recipeConflicts) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientBoundGetConflictingRecipesResponsePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_conflicting_recipes_response"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundGetConflictingRecipesResponsePacket> STREAM_CODEC = StreamCodec.composite(
        RecipeConflicts.STREAM_CODEC, ClientBoundGetConflictingRecipesResponsePacket::recipeConflicts,
            ClientBoundGetConflictingRecipesResponsePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
