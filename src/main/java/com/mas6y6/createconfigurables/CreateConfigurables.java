package com.mas6y6.createconfigurables;

import com.mas6y6.createconfigurables.client.CreateConfigurablesClient;
import com.mas6y6.createconfigurables.common.PacketHandler;
import com.mas6y6.createconfigurables.common.packets.*;
import com.mas6y6.createconfigurables.server.CreateConfigurablesServer;
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

@Mod(CreateConfigurables.MODID)
public class CreateConfigurables {
    public static final String MODID = "createconfigurables";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public CreateConfigurables(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new CreateConfigurablesClient(modEventBus);
        }

        new CreateConfigurablesServer(modEventBus);
    }

    @SubscribeEvent
    public void registerNetworkPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar("1");

        payloadRegistrar.playToClient(ClientBoundOpenCrushingWheelRecipeGuiPacket.TYPE, ClientBoundOpenCrushingWheelRecipeGuiPacket.STREAM_CODEC, PacketHandler::OpenRecipeGuiPacket);
        payloadRegistrar.playToClient(ClientBoundOpenMillstoneRecipeGuiPacket.TYPE, ClientBoundOpenMillstoneRecipeGuiPacket.STREAM_CODEC, PacketHandler::OpenMillstoneRecipeGuiPacket);

        payloadRegistrar.playToServer(ServerBoundGetCrushingConflictingRecipesPacket.TYPE, ServerBoundGetCrushingConflictingRecipesPacket.STREAM_CODEC, PacketHandler::GetConflictingRecipesPacket);
        payloadRegistrar.playToServer(ServerBoundGetMillstoneConflictingRecipesPacket.TYPE, ServerBoundGetMillstoneConflictingRecipesPacket.STREAM_CODEC, PacketHandler::GetMillingConflictingRecipesPacket);

        payloadRegistrar.playToClient(ClientBoundGetConflictingRecipesResponsePacket.TYPE, ClientBoundGetConflictingRecipesResponsePacket.STREAM_CODEC, PacketHandler::GetConflictingRecipesResponsePacket);

        payloadRegistrar.playToServer(ServerBoundGetCrushingWheelConfigPacket.TYPE, ServerBoundGetCrushingWheelConfigPacket.STREAM_CODEC, PacketHandler::GetCrushingWheelConfigPacket);
        payloadRegistrar.playToClient(ClientBoundGetCrushingWheelConfigResponsePacket.TYPE, ClientBoundGetCrushingWheelConfigResponsePacket.STREAM_CODEC, PacketHandler::GetCrushingWheelConfigResponsePacket);

        payloadRegistrar.playToServer(ServerBoundGetMillstoneConfigPacket.TYPE, ServerBoundGetMillstoneConfigPacket.STREAM_CODEC, PacketHandler::GetMillingWheelConfigPacket);
        payloadRegistrar.playToClient(ClientBoundGetMillstoneWheelConfigResponsePacket.TYPE, ClientBoundGetMillstoneWheelConfigResponsePacket.STREAM_CODEC, PacketHandler::GetMillingWheelConfigResponsePacket);

        payloadRegistrar.playToServer(ServerBoundSetConfigurationPacket.TYPE, ServerBoundSetConfigurationPacket.STREAM_CODEC, PacketHandler::SetConfigurationPacket);
        payloadRegistrar.playToServer(ServerBoundSetMillstoneConfigurationPacket.TYPE, ServerBoundSetMillstoneConfigurationPacket.STREAM_CODEC, PacketHandler::SetMillstoneConfigurationPacket);

        payloadRegistrar.playToClient(ClientBoundOpenItemDrainRecipeGuiPacket.TYPE, ClientBoundOpenItemDrainRecipeGuiPacket.STREAM_CODEC, PacketHandler::OpenItemDrainRecipeGuiPacket);
        payloadRegistrar.playToServer(ServerBoundGetItemDrainConflictingRecipesPacket.TYPE, ServerBoundGetItemDrainConflictingRecipesPacket.STREAM_CODEC, PacketHandler::GetItemDrainConflictingRecipesPacket);
        payloadRegistrar.playToServer(ServerBoundGetItemDrainConfigPacket.TYPE, ServerBoundGetItemDrainConfigPacket.STREAM_CODEC, PacketHandler::GetItemDrainConfigPacket);
        payloadRegistrar.playToClient(ClientBoundGetItemDrainConfigResponsePacket.TYPE, ClientBoundGetItemDrainConfigResponsePacket.STREAM_CODEC, PacketHandler::GetItemDrainConfigResponsePacket);
        payloadRegistrar.playToServer(ServerBoundSetItemDrainConfigurationPacket.TYPE, ServerBoundSetItemDrainConfigurationPacket.STREAM_CODEC, PacketHandler::SetItemDrainConfigurationPacket);
    }
}
