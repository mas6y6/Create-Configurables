package com.mas6y6.configureablecrushingwheel.client.gui;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.client.gui.Components.SimpleScrollList;
import com.mas6y6.configureablecrushingwheel.client.gui.Components.TextureButton;
import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConfigureCrushingWheelScreenRecipe extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "textures/gui/configure_crushing_wheels_recipe.png");
    private static final Component TITLE = Component.translatable("gui.configureablecrushingwheel.title").withColor(0x3c3b47);
    public SimpleScrollList scrollList;
    public UUID controller_uuid;
    private int leftPos, topPos;
    public boolean noConflict = true;
    private int imageWidth, imageHeight;
    private TextureButton closeButton;
    private TextureButton backbutton;
    private ItemStack itemStack;
    public RecipeConflicts recipeConflicts;

    public ConfigureCrushingWheelScreenRecipe(String controller_uuid, ItemStack itemStack, RecipeConflicts recipeConflicts) {
        super(TITLE);
        this.controller_uuid = UUID.fromString(controller_uuid);
        this.imageWidth = 199;
        this.imageHeight = 175;
        this.itemStack = itemStack;
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

        this.scrollList = new SimpleScrollList(this.leftPos + 7, this.topPos + 50, 178, 88);
        scrollList.setListBorder(0xFF585858);
        scrollList.setListBackground(0xFF3E3E3E);

        this.closeButton = new TextureButton(this.leftPos + 166, this.topPos + 151, 18, 18, ResourceLocation.parse("configureablecrushingwheel:textures/gui/buttons.png"), (button) -> {
            Minecraft.getInstance().setScreen(null);
        });
        this.closeButton.setUV(0,0);
        this.closeButton.setUVHover(18,0);
        this.closeButton.setPressed(36,0);

        this.backbutton = new TextureButton(this.leftPos + 8, this.topPos + 151, 18, 18, ResourceLocation.parse("configureablecrushingwheel:textures/gui/buttons.png"), (button) -> {
            Minecraft.getInstance().setScreen(new ConfigureCrushingWheelScreenMain(controller_uuid.toString()));
        });
        this.backbutton.setUV(0,18);
        this.backbutton.setUVHover(18,18);
        this.backbutton.setPressed(36,18);

        this.recipeConflicts.recipes.get(itemStack.getItem()).forEach(recipe -> {
           this.scrollList.entry(recipe)
                   .text(Component.literal(recipe.toString()))
                   .add();
        });

        this.addRenderableWidget(scrollList);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(backbutton);
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
                Component.translatable("gui.configureablecrushingwheel.select_recipe").withColor(0x9e9e9e),
                this.leftPos + 10,
                this.topPos + 40,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.configureablecrushingwheel.configure")
                        .append(" ")
                        .append(itemStack.getItem().getDescription()),
                this.leftPos + 10,
                this.topPos + 23,
                0xFFFFFF,
                false
        );


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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollList != null && scrollList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}