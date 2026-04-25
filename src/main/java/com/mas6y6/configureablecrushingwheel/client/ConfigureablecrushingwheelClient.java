package com.mas6y6.configureablecrushingwheel.client;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureablecrushingwheelClient {
    public static final String MODID = "configureablecrushingwheel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static ConfigureablecrushingwheelClient instance;

    public ConfigureablecrushingwheelClient(IEventBus modEventBus) {
        instance = this;
        NeoForge.EVENT_BUS.register(this);
    }

    public static ConfigureablecrushingwheelClient getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

        Vec3 start = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(reach));

        BlockHitResult hit = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            if (level.getBlockEntity(pos) instanceof CrushingWheelControllerBlockEntity entity) {
                Outliner.getInstance().showAABB(entity, AABB.encapsulatingFullBlocks(pos,pos))
                        .disableLineNormals()
                        .colored(0x5999ff)
                        .lineWidth(1 / 55f);
            }
        }
    }
}