package com.mas6y6.configureablecrushingwheel.common.packets;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenCrushingWheelRecipeGuiPacket(String uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenCrushingWheelRecipeGuiPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "open_gui"));

    public static final StreamCodec<FriendlyByteBuf, OpenCrushingWheelRecipeGuiPacket> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.STRING_UTF8, OpenCrushingWheelRecipeGuiPacket::uuid,
                    OpenCrushingWheelRecipeGuiPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
