package com.mas6y6.createconfigurables.client.gui;

import com.mas6y6.createconfigurables.CreateConfigurables;
import com.mas6y6.createconfigurables.client.gui.Components.SimpleScrollList;
import com.mas6y6.createconfigurables.client.gui.Components.TextureButton;
import com.mas6y6.createconfigurables.common.ItemDrainConfig;
import com.mas6y6.createconfigurables.common.RecipeConflicts;
import com.mas6y6.createconfigurables.common.packets.ServerBoundSetItemDrainConfigurationPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ConfigureItemDrainScreenRecipe extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(CreateConfigurables.MODID, "textures/gui/configure_crushing_wheels_recipe.png");
    private static final Component TITLE = Component.translatable("gui.configureableitemdrain.title").withColor(0x3c3b47);
    public SimpleScrollList scrollList;
    public UUID controller_uuid;
    private int leftPos, topPos;
    public boolean noConflict = true;
    private int imageWidth, imageHeight;
    private TextureButton closeButton;
    private TextureButton backbutton;
    private TextureButton resetButton;
    private ItemStack itemStack;
    public RecipeConflicts recipeConflicts;
    public ItemDrainConfig config;

    public ConfigureItemDrainScreenRecipe(String controller_uuid, ItemStack itemStack, RecipeConflicts recipeConflicts, ItemDrainConfig config) {
        super(TITLE);
        this.controller_uuid = UUID.fromString(controller_uuid);
        this.imageWidth = 199;
        this.imageHeight = 175;
        this.itemStack = itemStack;
        this.recipeConflicts = recipeConflicts;
        this.config = config;
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

        this.scrollList = new SimpleScrollList(
                this.leftPos + 7,
                this.topPos + 50,
                178,
                88
        );

        scrollList.setListBorder(0xFF585858);
        scrollList.setListBackground(0xFF3E3E3E);

        this.closeButton = new TextureButton(this.leftPos + 166, this.topPos + 151, 18, 18, ResourceLocation.parse("createconfigurables:textures/gui/buttons.png"), (button) -> {
            Minecraft.getInstance().setScreen(null);
        }).setUV(0,0).setUVHover(18,0).setPressed(36,0);

        this.backbutton = new TextureButton(this.leftPos + 8, this.topPos + 151, 18, 18, ResourceLocation.parse("createconfigurables:textures/gui/buttons.png"), (button) -> {
            Minecraft.getInstance().setScreen(new ConfigureItemDrainScreenMain(controller_uuid.toString()));
        }).setUV(0,18).setUVHover(18,18).setPressed(36,18).setTooltip(Component.translatable("gui.configureablecrushingwheel.back"));

        this.resetButton = new TextureButton(this.leftPos + 136, this.topPos + 151, 18, 18, ResourceLocation.parse("createconfigurables:textures/gui/buttons.png"), (button) -> {
            clearRecipeSelections();

            if (config != null) {
                config.removeConfig(BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()));
                PacketDistributor.sendToServer(new ServerBoundSetItemDrainConfigurationPacket(config));
            }
        }).setUV(0,36).setUVHover(18,36).setPressed(36,36).setTooltip(Component.translatable("gui.configureablecrushingwheel.reset_recipe"));

        this.recipeConflicts.recipes.get(itemStack.getItem()).forEach(recipeId -> {
            this.scrollList.entry(recipeId)
                    .items(this.recipeConflicts.outputs.get(recipeId))
                    .fluids(this.recipeConflicts.fluidOutputs.getOrDefault(recipeId, List.of()))
                    .text(Component.literal(recipeId.toString()))
                    .add();
        });
        applyConfiguredSelection();

        scrollList.setOnSelectEntry(ctx -> {
            clearRecipeSelections();
            this.scrollList.setSelectedId(ctx.entry().id());
            this.scrollList.updateEntry(ctx.entry().id(), ctx.entry().withBackgroundColor(0xFF748c5D));

            if (config != null) {
                config.setConfig(BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()), (ResourceLocation) ctx.entry().id());
                PacketDistributor.sendToServer(new ServerBoundSetItemDrainConfigurationPacket(config));
            }
        });

        this.addRenderableWidget(scrollList);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(backbutton);
        this.addRenderableWidget(resetButton);
    }

    public void applyConfiguredSelection() {
        if (this.scrollList == null || this.config == null) {
            return;
        }

        ResourceLocation configuredRecipe = this.config.config.get(BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()));
        clearRecipeSelections();

        if (configuredRecipe != null) {
            this.scrollList.getEntry(configuredRecipe, entry -> {
                this.scrollList.setSelectedId(configuredRecipe);
                this.scrollList.updateEntry(configuredRecipe, entry.withBackgroundColor(0xFF748c5D));
            });
        }
    }

    private void clearRecipeSelections() {
        this.scrollList.setSelectedId(null);
        this.scrollList.getAllEntries((id, entry) -> this.scrollList.updateEntry(id, entry.withBackgroundColor(0)));
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
        poseStack.scale(2.5f, 2.5f, 2.5f);
        graphics.renderItem(com.simibubi.create.AllBlocks.ITEM_DRAIN.asStack(), 0, 0);
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
