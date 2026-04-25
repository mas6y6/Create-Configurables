package com.mas6y6.configureablecrushingwheel.client.gui.Components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureButton extends AbstractWidget {
    private final ResourceLocation texture;

    private int u;
    private int v;

    private int hoverU;
    private int hoverV;

    private int disabledU;
    private int disabledV;

    private int pressedU;
    private int pressedV;

    private boolean pressed = false;

    private final OnPress onPress;

    public interface OnPress {
        void onPress(TextureButton button);
    }

    public TextureButton(int x, int y, int w, int h,
                         ResourceLocation texture,
                         int u, int v,
                         int hoverU, int hoverV,
                         int disabledU, int disabledV,
                         int pressedU, int pressedV,
                         OnPress onPress) {
        super(x, y, w, h, Component.empty());
        this.texture = texture;

        this.u = u;
        this.v = v;

        this.hoverU = hoverU;
        this.hoverV = hoverV;

        this.disabledU = disabledU;
        this.disabledV = disabledV;

        this.pressedU = pressedU;
        this.pressedV = pressedV;

        this.onPress = onPress;
    }

    public TextureButton(int x, int y, int w, int h,
                         ResourceLocation texture,
                         int u, int v,
                         int hoverU, int hoverV,
                         OnPress onPress) {
        this(x, y, w, h, texture,
                u, v,
                hoverU, hoverV,
                u, v,
                u, v,
                onPress);
    }

    public TextureButton(int x, int y, int w, int h,
                         ResourceLocation texture,
                         OnPress onPress) {
        this(x, y, w, h, texture,
                0, 0,
                0, 0,
                onPress);
    }

    public void setUV(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public void setUVHover(int u, int v) {
        this.hoverU = u;
        this.hoverV = v;
    }

    public void setUVDisabled(int u, int v) {
        this.disabledU = u;
        this.disabledV = v;
    }

    public void setPressed(int u, int v) {
        this.pressedU = u;
        this.pressedV = v;
    }

    public void enable() {
        this.active = true;
    }

    public void disable() {
        this.active = false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY) && button == 0) {
            this.pressed = true;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.pressed) {
            this.pressed = false;

            if (this.isMouseOver(mouseX, mouseY)) {
                this.onPress.onPress(this);
            }
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        int currentU = this.u;
        int currentV = this.v;

        if (!this.active) {
            currentU = this.disabledU;
            currentV = this.disabledV;

        } else if (this.pressed) {
            currentU = this.pressedU;
            currentV = this.pressedV;

        } else if (this.isHovered()) {
            currentU = this.hoverU;
            currentV = this.hoverV;
        }

        gg.blit(texture,
                this.getX(), this.getY(),
                currentU, currentV,
                this.width, this.height,
                256, 256);
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
}