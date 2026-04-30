package com.mas6y6.configureablecrushingwheel;

import com.mas6y6.configureablecrushingwheel.client.ConfigureablecrushingwheelClient;
import com.mas6y6.configureablecrushingwheel.common.PacketHandler;
import com.mas6y6.configureablecrushingwheel.common.packets.*;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Configureablecrushingwheel.MODID)
public class Configureablecrushingwheel {
    public static final String MODID = "configureablecrushingwheel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public Configureablecrushingwheel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ConfigureablecrushingwheelClient(modEventBus);
        }

        new ConfigureablecrushingwheelServer(modEventBus);
    }

    @SubscribeEvent
    public void registerNetworkPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar("1");

        payloadRegistrar.playToClient(OpenCrushingWheelRecipeGuiPacket.TYPE, OpenCrushingWheelRecipeGuiPacket.STREAM_CODEC, PacketHandler::OpenRecipeGuiPacket);
        payloadRegistrar.playToClient(OpenMillstoneRecipeGuiPacket.TYPE, OpenMillstoneRecipeGuiPacket.STREAM_CODEC, PacketHandler::OpenMillstoneRecipeGuiPacket);

        payloadRegistrar.playToServer(GetCrushingConflictingRecipesPacket.TYPE, GetCrushingConflictingRecipesPacket.STREAM_CODEC, PacketHandler::GetConflictingRecipesPacket);
        payloadRegistrar.playToServer(GetMillstoneConflictingRecipesPacket.TYPE, GetMillstoneConflictingRecipesPacket.STREAM_CODEC, PacketHandler::GetMillingConflictingRecipesPacket);

        payloadRegistrar.playToClient(GetConflictingRecipesResponsePacket.TYPE,GetConflictingRecipesResponsePacket.STREAM_CODEC, PacketHandler::GetConflictingRecipesResponsePacket);

        payloadRegistrar.playToServer(GetCrushingWheelConfigPacket.TYPE,GetCrushingWheelConfigPacket.STREAM_CODEC, PacketHandler::GetCrushingWheelConfigPacket);
        payloadRegistrar.playToClient(GetCrushingWheelConfigResponsePacket.TYPE,GetCrushingWheelConfigResponsePacket.STREAM_CODEC, PacketHandler::GetCrushingWheelConfigResponsePacket);

        payloadRegistrar.playToServer(GetMillstoneConfigPacket.TYPE, GetMillstoneConfigPacket.STREAM_CODEC, PacketHandler::GetMillingWheelConfigPacket);
        payloadRegistrar.playToClient(GetMillstoneWheelConfigResponsePacket.TYPE, GetMillstoneWheelConfigResponsePacket.STREAM_CODEC, PacketHandler::GetMillingWheelConfigResponsePacket);

        payloadRegistrar.playToServer(SetConfigurationPacket.TYPE,SetConfigurationPacket.STREAM_CODEC, PacketHandler::SetConfigurationPacket);
        payloadRegistrar.playToServer(SetMillstoneConfigurationPacket.TYPE,SetMillstoneConfigurationPacket.STREAM_CODEC, PacketHandler::SetMillstoneConfigurationPacket);
    }
}
