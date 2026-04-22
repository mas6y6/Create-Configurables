package com.mas6y6.configureablecrushingwheel.client;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.common.packets.LinkCrushingWheelsPacket;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@Mod(value = Configureablecrushingwheel.MODID, dist = Dist.CLIENT)
public class ConfigureablecrushingwheelClient {

    private final LinkedHashSet<BlockPos> selected = new LinkedHashSet<>();
    private final List<SuccessHighlight> success = new ArrayList<>();
    private final List<SuccessHighlight> seeLink = new ArrayList<>();
    public static ConfigureablecrushingwheelClient instance;

    public ConfigureablecrushingwheelClient() {
        NeoForge.EVENT_BUS.register(this);
        instance = this;
    }

    public static ConfigureablecrushingwheelClient getInstance() {
        return instance;
    }

    private static class SuccessHighlight {
        BlockPos a;
        BlockPos b;
        long start;

        SuccessHighlight(BlockPos a, BlockPos b) {
            this.a = a;
            this.b = b;
            this.start = System.currentTimeMillis();
        }
    }

    public void addSuccess(BlockPos a, BlockPos b) {
        success.add(new SuccessHighlight(a, b));
    }

    public void addSeeLink(BlockPos a, BlockPos b) {
        seeLink.add(new SuccessHighlight(a, b));
    }

    @SubscribeEvent
    public void wrenchRightClick(PlayerInteractEvent.RightClickBlock event) {

        if (!event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!event.getEntity().isShiftKeyDown()) return;

        BlockPos pos = event.getPos();

        if (event.getLevel().getBlockState(pos).getBlock() != AllBlocks.CRUSHING_WHEEL.get()) {
            return;
        }

        if (selected.contains(pos)) {
            selected.remove(pos);
        } else {
            if (selected.size() >= 2) selected.clear();
            selected.add(pos);
        }

        if (selected.size() == 2) {

            Iterator<BlockPos> it = selected.iterator();
            BlockPos a = it.next();
            BlockPos b = it.next();

            Level level = event.getLevel();

            BlockEntity beA = level.getBlockEntity(a);
            BlockEntity beB = level.getBlockEntity(b);

            if (beA != null && beB != null) {
                PacketDistributor.sendToServer(new LinkCrushingWheelsPacket(a, b));
            }
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();

        float time = (System.currentTimeMillis() % 1000L) / 1000.0f;
        float pulse = (float) (0.6f + 0.4f * Math.sin(time * Math.PI * 2));

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        VertexConsumer buffer = mc.renderBuffers()
                .bufferSource()
                .getBuffer(RenderType.lines());

        renderSelection(poseStack, buffer, cam, pulse);
        renderSuccess(poseStack, buffer, cam);
        renderSeeLink(poseStack, buffer, cam);

        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
    }

    private void renderSelection(PoseStack poseStack, VertexConsumer buffer, Vec3 cam, float pulse) {

        if (selected.isEmpty()) return;

        if (selected.size() == 1) {

            BlockPos pos = selected.iterator().next();
            renderBox(poseStack, buffer, cam, pos, 1f, 1f, 1f, pulse);

        } else {

            Iterator<BlockPos> it = selected.iterator();
            BlockPos a = it.next();
            BlockPos b = it.next();

            AABB box = new AABB(a).minmax(new AABB(b));

            LevelRenderer.renderLineBox(
                    poseStack,
                    buffer,
                    box,
                    1f, 1f, 1f,
                    pulse
            );
        }
    }

    private void renderSuccess(PoseStack poseStack, VertexConsumer buffer, Vec3 cam) {

        long now = System.currentTimeMillis();

        Iterator<SuccessHighlight> it = success.iterator();

        while (it.hasNext()) {

            SuccessHighlight s = it.next();

            long age = now - s.start;

            if (age > 3000) {
                it.remove();
                continue;
            }

            float alpha = 1f - (age / 3000f);

            double minX = Math.min(s.a.getX(), s.b.getX());
            double minY = Math.min(s.a.getY(), s.b.getY());
            double minZ = Math.min(s.a.getZ(), s.b.getZ());

            double maxX = Math.max(s.a.getX(), s.b.getX()) + 1;
            double maxY = Math.max(s.a.getY(), s.b.getY()) + 1;
            double maxZ = Math.max(s.a.getZ(), s.b.getZ()) + 1;

            double x1 = minX - cam.x;
            double y1 = minY - cam.y;
            double z1 = minZ - cam.z;

            double x2 = maxX - cam.x;
            double y2 = maxY - cam.y;
            double z2 = maxZ - cam.z;

            for (int i = 0; i < 2; i++) {

                double expand = i * 0.02;

                AABB box = new AABB(
                        x1 - expand, y1 - expand, z1 - expand,
                        x2 + expand, y2 + expand, z2 + expand
                );

                LevelRenderer.renderLineBox(
                        poseStack,
                        buffer,
                        box,
                        0f, 1f, 0.2f,
                        alpha
                );
            }
        }
    }

    private void renderSeeLink(PoseStack poseStack, VertexConsumer buffer, Vec3 cam) {

        long now = System.currentTimeMillis();

        Iterator<SuccessHighlight> it = success.iterator();

        while (it.hasNext()) {

            SuccessHighlight s = it.next();

            long age = now - s.start;

            if (age > 3000) {
                it.remove();
                continue;
            }

            float alpha = 1f - (age / 3000f);

            double minX = Math.min(s.a.getX(), s.b.getX());
            double minY = Math.min(s.a.getY(), s.b.getY());
            double minZ = Math.min(s.a.getZ(), s.b.getZ());

            double maxX = Math.max(s.a.getX(), s.b.getX()) + 1;
            double maxY = Math.max(s.a.getY(), s.b.getY()) + 1;
            double maxZ = Math.max(s.a.getZ(), s.b.getZ()) + 1;

            double x1 = minX - cam.x;
            double y1 = minY - cam.y;
            double z1 = minZ - cam.z;

            double x2 = maxX - cam.x;
            double y2 = maxY - cam.y;
            double z2 = maxZ - cam.z;

            for (int i = 0; i < 2; i++) {

                double expand = i * 0.02;

                AABB box = new AABB(
                        x1 - expand, y1 - expand, z1 - expand,
                        x2 + expand, y2 + expand, z2 + expand
                );

                LevelRenderer.renderLineBox(
                        poseStack,
                        buffer,
                        box,
                        1f, 1f, 0f,
                        alpha
                );
            }
        }
    }

    private void renderBox(PoseStack poseStack, VertexConsumer buffer, Vec3 cam,
                           BlockPos pos, float r, float g, float b, float a) {

        double x = pos.getX() - cam.x;
        double y = pos.getY() - cam.y;
        double z = pos.getZ() - cam.z;

        for (int i = 0; i < 2; i++) {

            double expand = i * 0.015;

            AABB box = new AABB(
                    x - expand, y - expand, z - expand,
                    x + 1 + expand, y + 1 + expand, z + 1 + expand
            );

            LevelRenderer.renderLineBox(poseStack, buffer, box, r, g, b, a);
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Vec3 eye = player.getEyePosition();
        double maxDistSq = 25;

        selected.removeIf(pos ->
                eye.distanceToSqr(Vec3.atCenterOf(pos)) > maxDistSq
        );
    }
}