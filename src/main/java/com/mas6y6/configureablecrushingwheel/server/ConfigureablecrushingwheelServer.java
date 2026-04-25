package com.mas6y6.configureablecrushingwheel.server;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredCrushingWheelUUID;
import com.mas6y6.configureablecrushingwheel.common.packets.OpenRecipeGuiPacket;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.createmod.catnip.levelWrappers.RayTraceLevel;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
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

        BlockEntity blockentity = event.getLevel().getBlockEntity(event.getPos());

        if (blockentity instanceof IConfiguredCrushingWheelUUID configuredCrushingWheel) {
            UUID uuid = configuredCrushingWheel.getUUID();
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(),new OpenRecipeGuiPacket(uuid.toString()));
        }
    }
}
