package com.mas6y6.configureablecrushingwheel.client.gui;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.client.gui.Components.SimpleScrollList;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConfigureCrushingWheelScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "textures/gui/configure_crushing_wheels.png");
    private static final Component TITLE = Component.translatable("gui.configureablecrushingwheel.title").withColor(0x3c3b47);
    public SimpleScrollList scrollList;
    public UUID controller_uuid;
    private int leftPos, topPos;
    public boolean noConflict = true;
    private int imageWidth, imageHeight;

    public ConfigureCrushingWheelScreen(String controller_uuid) {
        super(TITLE);
        this.controller_uuid = UUID.fromString(controller_uuid);
        this.imageWidth = 199;
        this.imageHeight = 175;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        super.init();

        this.scrollList = new SimpleScrollList(this.leftPos + 7, this.topPos + 50, 178, 73);
        scrollList.setListBorder(0xFF585858);
        scrollList.setListBackground(0xFF3E3E3E);

        PacketDistributor.sendToServer(new GetConflictingRecipesPacket());

        this.addRenderableWidget(scrollList);
    }


    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawString(
                font,
                TITLE,
                this.leftPos + this.imageWidth / 2 - font.width(TITLE) / 2,
                this.topPos + 4,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.configureablecrushingwheel.conflicting_text").withColor(0x9e9e9e),
                this.leftPos + 10,
                this.topPos + 40,
                0xFFFFFF,
                false
        );

        if (noConflict) {
            graphics.drawString(
                    font,
                    Component.translatable("gui.configureablecrushingwheel.no_conflict"),
                    this.leftPos + 9,
                    this.topPos + 53,
                    0xFFFFFF,
                    false
            );
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(this.leftPos + 210, this.topPos + 140, 0);
        poseStack.scale(2.5f, 2.5f, 1f);
        graphics.renderItem(AllBlocks.CRUSHING_WHEEL.asStack(), 0, 0);
        poseStack.popPose();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}