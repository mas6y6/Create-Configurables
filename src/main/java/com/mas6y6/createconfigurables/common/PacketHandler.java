package com.mas6y6.createconfigurables.common;

import com.mas6y6.createconfigurables.common.packets.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandler {
    public static void OpenRecipeGuiPacket(ClientBoundOpenCrushingWheelRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.OpenRecipeGui(packet, iPayloadContext);
        });
    }

    public static void GetConflictingRecipesPacket(ServerBoundGetCrushingConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetConflictingRecipesPacket(packet, iPayloadContext);
        });
    }

    public static void GetConflictingRecipesResponsePacket(ClientBoundGetConflictingRecipesResponsePacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.getConflictingRecipesResponsePacket(packet, iPayloadContext);
        });
    }

    public static void GetCrushingWheelConfigResponsePacket(ClientBoundGetCrushingWheelConfigResponsePacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.GetCrushingWheelConfigResponsePacket(packet, iPayloadContext);
        });
    }

    public static void GetCrushingWheelConfigPacket(ServerBoundGetCrushingWheelConfigPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetCrushingWheelConfigPacket(packet, iPayloadContext);
        });
    }

    public static void SetConfigurationPacket(ServerBoundSetConfigurationPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.SetConfigurationPacket(packet, iPayloadContext);
        });
    }

    public static void SetMillstoneConfigurationPacket(ServerBoundSetMillstoneConfigurationPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.SetMillstoneConfigurationPacket(packet, iPayloadContext);
        });
    }

    public static void OpenItemDrainRecipeGuiPacket(ClientBoundOpenItemDrainRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.OpenItemDrainRecipeGui(packet, iPayloadContext);
        });
    }

    public static void GetItemDrainConflictingRecipesPacket(ServerBoundGetItemDrainConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetItemDrainConflictingRecipesPacket(packet, iPayloadContext);
        });
    }

    public static void GetItemDrainConfigPacket(ServerBoundGetItemDrainConfigPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetItemDrainConfigPacket(packet, iPayloadContext);
        });
    }

    public static void GetItemDrainConfigResponsePacket(ClientBoundGetItemDrainConfigResponsePacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.GetItemDrainConfigResponsePacket(packet, iPayloadContext);
        });
    }

    public static void SetItemDrainConfigurationPacket(ServerBoundSetItemDrainConfigurationPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.SetItemDrainConfigurationPacket(packet, iPayloadContext);
        });
    }

    public static void OpenMillstoneRecipeGuiPacket(ClientBoundOpenMillstoneRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.OpenMillstoneRecipeGuiPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingConflictingRecipesPacket(ServerBoundGetMillstoneConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetMillingConflictingRecipesPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingWheelConfigPacket(ServerBoundGetMillstoneConfigPacket packet, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.server.PacketHandle.GetMillingWheelConfigPacket(packet, iPayloadContext);
        });
    }

    public static void GetMillingWheelConfigResponsePacket(ClientBoundGetMillstoneWheelConfigResponsePacket getMillingWheelConfigResponsePacket, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.createconfigurables.client.PacketHandle.GetMillingWheelConfigResponsePacket(getMillingWheelConfigResponsePacket, iPayloadContext);
        });
    }
}
