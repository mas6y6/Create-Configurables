package com.mas6y6.configureablecrushingwheel.client.gui;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.client.gui.Components.SimpleScrollList;
import com.mas6y6.configureablecrushingwheel.client.gui.Components.TextureButton;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import com.mas6y6.configureablecrushingwheel.common.packets.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigureMillstoneScreenMain extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Configureablecrushingwheel.MODID, "textures/gui/configure_crushing_wheels.png");
    private static final Component TITLE = Component.translatable("gui.configureablemillstone.title").withColor(0x3c3b47);
    public SimpleScrollList scrollList;
    public UUID controller_uuid;
    private int leftPos, topPos;
    public boolean noConflict = true;
    private int imageWidth, imageHeight;
    private TextureButton closeButton;
    private TextureButton resetButton;
    public RecipeConflicts recipeConflicts;
    public MillstoneConfig config;
    private EditBox searchBox;

    public ConfigureMillstoneScreenMain(String controller_uuid) {
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

        this.scrollList = new SimpleScrollList(this.leftPos + 7, this.topPos + 60, 178, 73);
        scrollList.setListBorder(0xFF585858);
        scrollList.setListBackground(0xFF3E3E3E);
        scrollList.setSearchQuerySupplier(() -> searchBox != null ? searchBox.getValue() : "");
        scrollList.setOnSelectEntry((ctx) -> {
            // Minecraft.getInstance().setScreen(new ConfigureCrushingWheelScreenRecipe(controller_uuid.toString(),ctx.entry().items().getFirst(), recipeConflicts, config));
        });

        this.closeButton = new TextureButton(this.leftPos + 166, this.topPos + 151, 18, 18, ResourceLocation.parse("configureablecrushingwheel:textures/gui/buttons.png"), (button) -> {
            Minecraft.getInstance().setScreen(null);
        }).setUV(0,0).setUVHover(18,0).setPressed(36,0);

        this.resetButton = new TextureButton(this.leftPos + 136, this.topPos + 151, 18, 18, ResourceLocation.parse("configureablecrushingwheel:textures/gui/buttons.png"), (button) -> {
            PacketDistributor.sendToServer(new SetMillstoneConfigurationPacket(new MillstoneConfig(Map.of(), controller_uuid)));
            Minecraft.getInstance().setScreen(new ConfigureMillstoneScreenMain(controller_uuid.toString()));
        }).setUV(0,36).setUVHover(18,36).setPressed(36,36).setTooltip(Component.translatable("gui.configureablecrushingwheel.reset_config"));

        PacketDistributor.sendToServer(new GetMillstoneConflictingRecipesPacket());
        PacketDistributor.sendToServer(new GetMillstoneWheelConfigPacket(controller_uuid));


        searchBox = new EditBox(this.font,this.leftPos + 7,this.topPos + 20, 178, 16, Component.translatable("gui.configureablecrushingwheel.search_box"));
        searchBox.setMaxLength(50);
        searchBox.setResponder(s -> {
            if (this.scrollList != null) {
                this.scrollList.setSearchQuerySupplier(this.searchBox::getValue);
            }
        });

        this.addRenderableWidget(searchBox);
        this.addRenderableWidget(scrollList);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(resetButton);
    }

    public void applyResolvedConflictHighlights() {
        if (this.scrollList == null) {
            return;
        }

        this.scrollList.getAllEntries((id, entry) -> this.scrollList.updateEntry(id, entry.withBackgroundColor(0)));

        if (this.config == null) {
            return;
        }

        this.config.config.forEach((itemId, recipeId) -> {
            Object item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId).asItem();
            this.scrollList.getEntry(item, entry -> this.scrollList.updateEntry(item, entry.withBackgroundColor(0xFF748c5D)));
        });
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
                this.topPos + 50,
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

        graphics.renderComponentTooltip(font, List.of(), mouseX, mouseY);
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
