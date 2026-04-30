package com.mas6y6.configureablecrushingwheel.common;

import com.mas6y6.configureablecrushingwheel.common.packets.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandler {
    public static void OpenRecipeGuiPacket(OpenCrushingWheelRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.OpenRecipeGui(packet, iPayloadContext);
        });
    }

    public static void GetConflictingRecipesPacket(GetCrushingConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
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

    public static void SetMillstoneConfigurationPacket(SetMillstoneConfigurationPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.SetMillstoneConfigurationPacket(packet, iPayloadContext);
        });
    }

    public static void OpenMillstoneRecipeGuiPacket(OpenMillstoneRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.OpenMillstoneRecipeGuiPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingConflictingRecipesPacket(GetMillstoneConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.GetMillingConflictingRecipesPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingWheelConfigPacket(GetMillstoneConfigPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.GetMillingWheelConfigPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingWheelConfigResponsePacket(GetMillstoneWheelConfigResponsePacket getMillingWheelConfigResponsePacket, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.GetMillingWheelConfigResponsePacket(getMillingWheelConfigResponsePacket, iPayloadContext);
        });
    }
}
