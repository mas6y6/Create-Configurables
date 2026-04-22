package com.mas6y6.configureablecrushingwheel;

import com.mas6y6.configureablecrushingwheel.common.PacketHandler;
import com.mas6y6.configureablecrushingwheel.common.packets.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(Configureablecrushingwheel.MODID)
public class Configureablecrushingwheel {
    public static final String MODID = "configureablecrushingwheel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Configureablecrushingwheel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
    }

    @SubscribeEvent
    public void registerNetworkPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar("1");
        payloadRegistrar.playToServer(
                LinkCrushingWheelsPacket.TYPE,
                LinkCrushingWheelsPacket.STREAM_CODEC,
                PacketHandler::linkCrushingWheels
        );
        payloadRegistrar.playToClient(
                LinkCrushingWheelsResponsePacket.TYPE,
                LinkCrushingWheelsResponsePacket.STREAM_CODEC,
                PacketHandler::linkCrushingWheelsResponse
        );

        payloadRegistrar.playToServer(
                CheckCrushingWheelsLinkPacket.TYPE,
                CheckCrushingWheelsLinkPacket.STREAM_CODEC,
                PacketHandler::checkCrushingWheelsLink
        );

        payloadRegistrar.playToClient(
                CheckCrushingWheelsLinkPacketResponseTrue.TYPE,
                CheckCrushingWheelsLinkPacketResponseTrue.STREAM_CODEC,
                PacketHandler::checkCrushingWheelsLinkResponseTrue
        );

        payloadRegistrar.playToClient(
                CheckCrushingWheelsLinkPacketResponseFalse.TYPE,
                CheckCrushingWheelsLinkPacketResponseFalse.STREAM_CODEC,
                PacketHandler::checkCrushingWheelsLinkResponseFalse
        );
    }
}
