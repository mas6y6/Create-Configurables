package com.mas6y6.configureablecrushingwheel.server;

import com.mas6y6.configureablecrushingwheel.common.packets.CheckCrushingWheelsLinkPacket;
import com.mas6y6.configureablecrushingwheel.common.packets.LinkCrushingWheelsPacket;
import com.mas6y6.configureablecrushingwheel.common.packets.LinkCrushingWheelsResponsePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandle {
    public static void LinkCrushingWheels(LinkCrushingWheelsPacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();

        PacketDistributor.sendToPlayer(player, new LinkCrushingWheelsResponsePacket(packet.a(), packet.b(), true));
    }

    public static void CheckCrushingWheelsLink(CheckCrushingWheelsLinkPacket checkCrushingWheelsLinkPacket, IPayloadContext iPayloadContext) {

    }
}
