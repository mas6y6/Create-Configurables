package com.mas6y6.configureablecrushingwheel.common;

import com.mas6y6.configureablecrushingwheel.common.packets.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandler {
    public static void linkCrushingWheels(LinkCrushingWheelsPacket linkCrushingWheelsPacket, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.LinkCrushingWheels(linkCrushingWheelsPacket, iPayloadContext);
        });
    }

    public static void linkCrushingWheelsResponse(LinkCrushingWheelsResponsePacket linkCrushingWheelsResponsePacket, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.LinkCrushingWheelsResponse(linkCrushingWheelsResponsePacket, iPayloadContext);
        });
    }


    public static void checkCrushingWheelsLink(CheckCrushingWheelsLinkPacket checkCrushingWheelsLinkPacket, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.server.PacketHandle.CheckCrushingWheelsLink(checkCrushingWheelsLinkPacket, iPayloadContext);
        });
    }

    public static void checkCrushingWheelsLinkResponseTrue(CheckCrushingWheelsLinkPacketResponseTrue checkCrushingWheelsLinkPacketResponseTrue, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.CheckCrushingWheelsLinkResponseTrue(checkCrushingWheelsLinkPacketResponseTrue, iPayloadContext);
        });
    }

    public static void checkCrushingWheelsLinkResponseFalse(CheckCrushingWheelsLinkPacketResponseFalse checkCrushingWheelsLinkPacketResponseFalse, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            com.mas6y6.configureablecrushingwheel.client.PacketHandle.CheckCrushingWheelsLinkResponseFalse(checkCrushingWheelsLinkPacketResponseFalse, iPayloadContext);
        });
    }
}
