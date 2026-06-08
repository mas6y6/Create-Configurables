package com.mas6y6.configureablecrushingwheel.server;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredCrushingWheelUUID;
import com.mas6y6.configureablecrushingwheel.common.IConfiguredItemDrainUUID;
import com.mas6y6.configureablecrushingwheel.common.IConfiguredMillstoneUUID;
import com.mas6y6.configureablecrushingwheel.common.packets.ClientBoundOpenCrushingWheelRecipeGuiPacket;
import com.mas6y6.configureablecrushingwheel.common.packets.ClientBoundOpenItemDrainRecipeGuiPacket;
import com.mas6y6.configureablecrushingwheel.common.packets.ClientBoundOpenMillstoneRecipeGuiPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ConfigureablecrushingwheelServer {
    public static final String MODID = "configureablecrushingwheel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private static ConfigureablecrushingwheelServer instance;

    public static ConfigureablecrushingwheelServer getInstance() {
        return instance;
    }

    public ConfigureablecrushingwheelServer(IEventBus modEventBus) {
        instance = this;
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!event.getEntity().getMainHandItem().isEmpty()) return;

        BlockEntity blockentity = event.getLevel().getBlockEntity(event.getPos());

        if (blockentity instanceof IConfiguredCrushingWheelUUID configuredCrushingWheel) {
            UUID uuid = configuredCrushingWheel.getUUID();
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(),new ClientBoundOpenCrushingWheelRecipeGuiPacket(uuid.toString()));
        } else if (blockentity instanceof IConfiguredMillstoneUUID millstone) {
            UUID uuid = millstone.getUUID();
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(),new ClientBoundOpenMillstoneRecipeGuiPacket(uuid.toString()));
        }  else if (blockentity instanceof IConfiguredItemDrainUUID itemDrain) {
            UUID uuid = itemDrain.getUUID();
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(),new ClientBoundOpenItemDrainRecipeGuiPacket(uuid.toString()));
        }
    }
}
