package com.mas6y6.configureablecrushingwheel.client;

import com.mas6y6.configureablecrushingwheel.common.packets.CheckCrushingWheelsLinkPacketResponseFalse;
import com.mas6y6.configureablecrushingwheel.common.packets.CheckCrushingWheelsLinkPacketResponseTrue;
import com.mas6y6.configureablecrushingwheel.common.packets.LinkCrushingWheelsResponsePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandle {
    public static void LinkCrushingWheelsResponse(LinkCrushingWheelsResponsePacket linkCrushingWheelsResponsePacket, IPayloadContext iPayloadContext) {
        if (linkCrushingWheelsResponsePacket.success()) {
            ConfigureablecrushingwheelClient.getInstance().addSuccess(linkCrushingWheelsResponsePacket.a(), linkCrushingWheelsResponsePacket.b());
        } else {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.configureablecrushingwheel.link_failed"), true);
        }
    }

    public static void CheckCrushingWheelsLinkResponseTrue(CheckCrushingWheelsLinkPacketResponseTrue checkCrushingWheelsLinkPacketResponse, IPayloadContext iPayloadContext) {

    }

    public static void CheckCrushingWheelsLinkResponseFalse(CheckCrushingWheelsLinkPacketResponseFalse checkCrushingWheelsLinkPacketResponseFalse, IPayloadContext iPayloadContext) {
    }
}
