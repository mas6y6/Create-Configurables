package com.mas6y6.configureablecrushingwheel.common;

import com.mas6y6.configureablecrushingwheel.common.packets.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandler {
    public static void OpenRecipeGuiPacket(OpenRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.OpenRecipeGui(packet, iPayloadContext);
        });
    }

    public static void GetConflictingRecipesPacket(GetConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.GetConflictingRecipesPacket(packet, iPayloadContext);
        });
    }

    public static void GetConflictingRecipesResponsePacket(GetConflictingRecipesResponsePacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.getConflictingRecipesResponsePacket(packet, iPayloadContext);
        });
    }

    public static void GetCrushingWheelConfigResponsePacket(GetCrushingWheelConfigResponsePacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.GetCrushingWheelConfigResponsePacket(packet, iPayloadContext);
        });
    }

    public static void GetCrushingWheelConfigPacket(GetCrushingWheelConfigPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.GetCrushingWheelConfigPacket(packet, iPayloadContext);
        });
    }

    public static void SetConfigurationPacket(SetConfigurationPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.SetConfigurationPacket(packet, iPayloadContext);
        });
    }
}
