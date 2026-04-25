package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GetConflictingRecipesResponsePacket(RecipeConflicts recipeConflicts) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GetConflictingRecipesResponsePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "get_conflicting_recipes_response"));

    public static final StreamCodec<FriendlyByteBuf, GetConflictingRecipesResponsePacket> STREAM_CODEC = StreamCodec.composite(
        RecipeConflicts.STREAM_CODEC, GetConflictingRecipesResponsePacket::recipeConflicts,
            GetConflictingRecipesResponsePacket::new
    );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
