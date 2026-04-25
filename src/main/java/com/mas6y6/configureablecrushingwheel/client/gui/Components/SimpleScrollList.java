package com.mas6y6.configureablecrushingwheel.client.gui.Components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.util.Mth.clamp;

public class SimpleScrollList extends AbstractWidget {

    public record Entry(
            Object id,
            Component text,
            List<ItemStack> items,
            List<ResourceLocation> textures,
            float itemScale,
            boolean showText,
            int height,
            ResourceLocation background,
            int backgroundColor,
            List<Component> tooltip
    ) {}

    private final List<Entry> items = new ArrayList<>();

    private float scrollAmount;
    private int selectedIndex = -1;
    private boolean isDragging;

    private Consumer<Integer> onSelect;
    private Consumer<Entry> onSelectEntry;

    private ResourceLocation listBackground;
    private int listBackgroundColor = 0xFF000000;
    private ResourceLocation listBorder;
    private int listBorderColor = 0xFFFFFFFF;

    public SimpleScrollList(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    // =========================
    // FLUENT API ENTRY POINT
    // =========================

    public EntryBuilder entry(Object id) {
        return new EntryBuilder(this, id);
    }

    void addEntry(Entry entry) {
        items.add(entry);
    }

    public void clear() {
        items.clear();
        selectedIndex = -1;
        scrollAmount = 0;
    }

    // =========================
    // SELECTION
    // =========================

    public void setOnSelect(Consumer<Integer> onSelect) {
        this.onSelect = onSelect;
    }

    public void setOnSelectEntry(Consumer<Entry> onSelectEntry) {
        this.onSelectEntry = onSelectEntry;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Entry getSelectedItem() {
        return (selectedIndex >= 0 && selectedIndex < items.size())
                ? items.get(selectedIndex)
                : null;
    }

    // =========================
    // STYLE
    // =========================

    public void setListBackground(ResourceLocation texture, int color) {
        this.listBackground = texture;
        this.listBackgroundColor = color;
    }

    public void setListBackground(int color) {
        this.listBackgroundColor = color;
    }

    public void setListBorder(ResourceLocation texture, int color) {
        this.listBorder = texture;
        this.listBorderColor = color;
    }

    public void setListBorder(int color) {
        this.listBorderColor = color;
    }

    // =========================
    // RENDERING
    // =========================

    @Override
    public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {

        // background
        if (listBackground != null) {
            gg.blit(listBackground, getX(), getY(), 0, 0, width, height, width, height);
        } else {
            gg.fill(getX(), getY(), getX() + width, getY() + height, listBackgroundColor);
        }

        // border
        if (listBorder != null) {
            gg.blit(listBorder, getX(), getY(), 0, 0, width, height, width, height);
        } else {
            gg.renderOutline(getX(), getY(), width, height, listBorderColor);
        }

        int totalHeight = 0;
        for (Entry e : items) totalHeight += e.height();

        gg.enableScissor(getX(), getY(), getX() + width, getY() + height);

        int currentY = getY() - (int) scrollAmount;

        for (int i = 0; i < items.size(); i++) {
            Entry entry = items.get(i);
            int h = entry.height();

            if (currentY + h < getY()) {
                currentY += h;
                continue;
            }
            if (currentY > getY() + height) {
                currentY += h;
                continue;
            }

            boolean hovered =
                    mouseX >= getX() && mouseX < getX() + width - 6 &&
                            mouseY >= currentY && mouseY < currentY + h;

            // entry background
            if (entry.background() != null) {
                gg.blit(entry.background(), getX() + 1, currentY, 0, 0, width - 7, h, width - 7, h);
            } else if (entry.backgroundColor() != 0) {
                gg.fill(getX() + 1, currentY, getX() + width - 7, currentY + h, entry.backgroundColor());
            }

            // selection / hover
            if (i == selectedIndex) {
                gg.fill(getX() + 1, currentY, getX() + width - 7, currentY + h, 0x88FFFFFF);
            } else if (hovered) {
                gg.fill(getX() + 1, currentY, getX() + width - 7, currentY + h, 0x44FFFFFF);
            }

            // items
            if (entry.items() != null && !entry.items().isEmpty()) {
                float scale = entry.itemScale();
                int size = (int) (16 * scale);

                for (int j = 0; j < entry.items().size(); j++) {
                    ItemStack stack = entry.items().get(j);

                    gg.pose().pushPose();
                    gg.pose().translate(getX() + 4 + j * (size + 2), currentY + 2, 0);
                    gg.pose().scale(scale, scale, 1);
                    gg.renderFakeItem(stack, 0, 0);
                    gg.pose().popPose();
                }

            } else if (entry.textures() != null && !entry.textures().isEmpty()) {
                float scale = entry.itemScale();
                int size = (int) (16 * scale);

                for (int j = 0; j < entry.textures().size(); j++) {
                    ResourceLocation tex = entry.textures().get(j);
                    gg.blit(tex, getX() + 4 + j * (size + 2), currentY + 2, 0, 0, size, size, size, size);
                }
            }

            // text
            if (entry.showText()) {
                int maxWidth = width - 12;
                List<FormattedCharSequence> lines =
                        Minecraft.getInstance().font.split(entry.text(), maxWidth);

                int itemSize = (int) (16 * entry.itemScale());
                int textY = currentY + itemSize + 4;

                for (int j = 0; j < lines.size(); j++) {
                    gg.drawString(Minecraft.getInstance().font,
                            lines.get(j),
                            getX() + 6,
                            textY + j * 10,
                            0xFFFFFF,
                            false);
                }
            }

            currentY += h;
        }

        gg.disableScissor();

        // tooltips
        currentY = getY() - (int) scrollAmount;

        for (int i = 0; i < items.size(); i++) {
            Entry entry = items.get(i);
            int h = entry.height();

            boolean hovered =
                    mouseX >= getX() && mouseX < getX() + width - 6 &&
                            mouseY >= currentY && mouseY < currentY + h;

            if (hovered && entry.tooltip() != null && !entry.tooltip().isEmpty()) {
                gg.renderComponentTooltip(Minecraft.getInstance().font, entry.tooltip(), mouseX, mouseY);
            }

            currentY += h;
        }

        // scrollbar
        if (totalHeight > height) {
            int barX = getX() + width - 6;
            int barW = 4;

            int barH = Math.max(10, (int) ((float) height * height / totalHeight));
            int barY = getY() + (int) ((height - barH) * (scrollAmount / (totalHeight - height)));

            gg.fill(barX, getY(), barX + barW, getY() + height, 0x44FFFFFF);
            gg.fill(barX, barY, barX + barW, barY + barH, 0xFFFFFFFF);
        }
    }

    // =========================
    // INPUT
    // =========================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible) return false;

        int scrollbarX = getX() + width - 6;

        if (mouseX >= scrollbarX && mouseX < getX() + width
                && mouseY >= getY() && mouseY < getY() + height) {

            int total = 0;
            for (Entry e : items) total += e.height();

            if (total > height) {
                isDragging = true;
                updateScrolling(mouseY);
                return true;
            }
        }

        if (mouseX < getX() || mouseX >= scrollbarX
                || mouseY < getY() || mouseY >= getY() + height) {
            return false;
        }
        int currentY = getY() - (int) scrollAmount;

        for (int i = 0; i < items.size(); i++) {
            Entry entry = items.get(i);

            if (mouseY >= currentY && mouseY < currentY + entry.height()) {

                boolean changed = (selectedIndex != i);
                selectedIndex = i;

                if (onSelect != null) onSelect.accept(i);
                if (onSelectEntry != null) onSelectEntry.accept(entry);

                if (changed) {
                    playDownSound(Minecraft.getInstance().getSoundManager());
                }

                return true;
            }

            currentY += entry.height();
        }

        return false;
    }

    private void updateScrolling(double mouseY) {
        int totalContentHeight = 0;
        for (Entry entry : items) {
            totalContentHeight += entry.height();
        }

        if (totalContentHeight <= height) return;

        float scrollPercent = (float) (mouseY - getY()) / (float) height;

        scrollAmount = clamp(
                scrollPercent * totalContentHeight - (height * 0.5f),
                0,
                Math.max(0, totalContentHeight - height)
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int total = 0;
        for (Entry e : items) total += e.height();

        if (total <= height) return false;

        scrollAmount = clamp(
                scrollAmount - (float) scrollY * 10,
                0,
                total - height
        );

        return true;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    // =========================
    // BUILDER
    // =========================

    public static class EntryBuilder {

        private final SimpleScrollList parent;
        private final Object id;

        private Component text = Component.empty();
        private List<ItemStack> items;
        private List<ResourceLocation> textures;

        private float scale = 1.0f;
        private boolean showText = true;

        private ResourceLocation background;
        private int backgroundColor = 0;

        private List<Component> tooltip;

        public EntryBuilder(SimpleScrollList parent, Object id) {
            this.parent = parent;
            this.id = id;
        }

        public EntryBuilder text(Component text) {
            this.text = text;
            return this;
        }

        public EntryBuilder items(List<ItemStack> items) {
            this.items = items;
            return this;
        }

        public EntryBuilder item(ItemStack stack) {
            this.items = List.of(stack);
            return this;
        }

        public EntryBuilder textures(List<ResourceLocation> textures) {
            this.textures = textures;
            return this;
        }

        public EntryBuilder texture(ResourceLocation texture) {
            this.textures = List.of(texture);
            return this;
        }

        public EntryBuilder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public EntryBuilder showText(boolean showText) {
            this.showText = showText;
            return this;
        }

        public EntryBuilder background(ResourceLocation bg) {
            this.background = bg;
            return this;
        }

        public EntryBuilder backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public EntryBuilder tooltip(List<Component> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public void add() {
            int itemSize = (int) (16 * scale);
            int textWidth = parent.width - 12;

            List<FormattedCharSequence> split =
                    Minecraft.getInstance().font.split(text, textWidth);

            int textHeight = showText ? split.size() * 10 : 0;
            int rowHeight = itemSize + textHeight + 6;

            parent.addEntry(new Entry(
                    id,
                    text,
                    items,
                    textures,
                    scale,
                    showText,
                    rowHeight,
                    background,
                    backgroundColor,
                    tooltip
            ));
        }
    }
}