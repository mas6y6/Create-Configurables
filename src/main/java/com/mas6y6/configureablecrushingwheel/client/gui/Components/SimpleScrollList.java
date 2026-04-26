package com.mas6y6.configureablecrushingwheel.client.gui.Components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.util.Mth.clamp;

/*

BEFORE YOU GET MAD AT ME!!!
yes this entire thing is vibe coded. I hate making UI elements from scratch, so I kinda had to vibe code this entire thing.

I am sorry for any developers that need to make their own UI elements.

*/

public class SimpleScrollList extends AbstractWidget {

    // =========================
    // ENTRY
    // =========================

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
            List<Component> tooltip,
            Consumer<EntryContext> onClick
    ) {
        public Entry withBackgroundColor(int color) {
            return new Entry(id, text, items, textures, itemScale, showText,
                    height, background, color, tooltip, onClick);
        }

        public Entry withHeight(int newHeight) {
            return new Entry(id, text, items, textures, itemScale, showText,
                    newHeight, background, backgroundColor, tooltip, onClick);
        }
    }

    public record EntryContext(SimpleScrollList list, Object id, Entry entry) {
        public void update(Entry newEntry) {
            list.updateEntry(id, newEntry);
        }
    }

    // =========================
    // STORAGE
    // =========================

    private final LinkedHashMap<Object, Entry> items = new LinkedHashMap<>();

    private float scrollAmount;

    private float scrollVelocity = 0;
    private boolean draggingScrollbar = false;

    private double lastMouseY = 0;

    private Object selectedId = null;

    private boolean isDragging;

    private Consumer<Object> onSelect;
    private Consumer<EntryContext> onSelectEntry;
    private Supplier<String> searchQuerySupplier;

    // =========================
    // STYLE
    // =========================

    private ResourceLocation listBackground;
    private int listBackgroundColor = 0xFF000000;

    private ResourceLocation listBorder;
    private int listBorderColor = 0xFFFFFFFF;

    // =========================
    // CONSTRUCTOR
    // =========================

    public SimpleScrollList(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    // =========================
    // ENTRY MANAGEMENT
    // =========================

    private List<Entry> orderedEntries() {
        String query = getSearchQuery();
        if (query.isEmpty()) {
            return new ArrayList<>(items.values());
        }

        List<Entry> filteredEntries = new ArrayList<>();
        for (Entry entry : items.values()) {
            if (entry.text().getString().toLowerCase(Locale.ROOT).contains(query)) {
                filteredEntries.add(entry);
            }
        }

        return filteredEntries;
    }

    public EntryBuilder entry(Object id) {
        return new EntryBuilder(this, id);
    }

    void addEntry(Entry entry) {
        items.put(entry.id(), entry);
    }

    public void updateEntry(Object id, Entry newEntry) {
        if (items.containsKey(id)) {
            items.put(id, newEntry.withHeight(calculateEntryHeight(newEntry)));
        }
    }

    public void removeEntry(Object id) {
        items.remove(id);
        if (Objects.equals(selectedId, id)) {
            selectedId = null;
        }
    }

    public void clear() {
        items.clear();
        selectedId = null;
        scrollAmount = 0;
    }

    public void getEntry(Object id, Consumer<Entry> consumer) {
        Entry e = items.get(id);
        if (e != null) consumer.accept(e);
    }

    public void getAllEntries(BiConsumer<Object, Entry> consumer) {
        items.forEach(consumer);
    }

    // =========================
    // SELECTION
    // =========================

    public void setOnSelect(Consumer<Object> onSelect) {
        this.onSelect = onSelect;
    }

    public void setOnSelectEntry(Consumer<EntryContext> onSelectEntry) {
        this.onSelectEntry = onSelectEntry;
    }

    public void setSearchQuerySupplier(@Nullable Supplier<String> searchQuerySupplier) {
        this.searchQuerySupplier = searchQuerySupplier;
        scrollAmount = 0;
    }

    public Object getSelectedId() {
        return selectedId;
    }

    public Entry getSelectedItem() {
        return items.get(selectedId);
    }

    public void setSelectedId(@Nullable Object selectedId) {
        this.selectedId = selectedId;
    }

    // =========================
    // STYLE API (FIXED)
    // =========================

    public void setListBackground(ResourceLocation texture, int color) {
        this.listBackground = texture;
        this.listBackgroundColor = color;
    }

    public void setListBackground(int color) {
        this.listBackgroundColor = color;
        this.listBackground = null;
    }

    public void setListBorder(ResourceLocation texture, int color) {
        this.listBorder = texture;
        this.listBorderColor = color;
    }

    public void setListBorder(int color) {
        this.listBorderColor = color;
        this.listBorder = null;
    }

    // =========================
    // RENDER
    // =========================

    @Override
    public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        updateMomentum();

        List<Entry> list = orderedEntries();

        boolean scrollbar = needsScrollbar();

        int contentRight = getX() + width - (scrollbar ? 7 : 0);
        int contentW = contentRight - getX();

        int contentBottom = getY() + height;

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
        for (Entry e : list) totalHeight += e.height();

        gg.enableScissor(getX(), getY(), contentRight, getY() + height);

        int currentY = getY() - (int) scrollAmount;

        for (Entry entry : list) {
            int h = entry.height();

            if (currentY + h < getY() || currentY > getY() + height) {
                currentY += h;
                continue;
            }

            boolean hovered =
                    mouseX >= getX() + 1 && mouseX < contentRight - 1 &&
                            mouseY >= currentY && mouseY < currentY + h;

            boolean selected = Objects.equals(selectedId, entry.id());

            int baseColor = selected ? 0xFF748c5D : entry.backgroundColor();
            int bgColor = hovered
                    ? brightenColor(baseColor != 0 ? baseColor : 0xFF444444, baseColor != 0 ? 0.18f : 0.08f)
                    : baseColor;

            int rowW = contentW - 2;

            if (entry.background() != null) {
                gg.blit(entry.background(),
                        getX() + 1,
                        currentY,
                        0, 0,
                        rowW,
                        h,
                        rowW,
                        h);
            } else {
                gg.fill(getX() + 1, currentY,
                        getX() + 1 + rowW,
                        currentY + h,
                        bgColor);
            }

            // items
            if (entry.items() != null) {
                float scale = entry.itemScale();
                int size = (int) (16 * scale);

                Minecraft mc = Minecraft.getInstance();

                for (int i = 0; i < entry.items().size(); i++) {
                    ItemStack stack = entry.items().get(i);

                    gg.pose().pushPose();
                    gg.pose().translate(getX() + 4 + i * (size + 2), currentY + 2, 0);
                    gg.pose().scale(scale, scale, 1);

                    gg.renderItem(stack, 0, 0);
                    gg.renderItemDecorations(mc.font, stack, 0, 0);

                    gg.pose().popPose();
                }
            }

            // text
            if (entry.showText()) {
                Font font = Minecraft.getInstance().font;

                int maxWidth = calculateTextWidth(entry);

                List<FormattedCharSequence> lines =
                        font.split(entry.text(), maxWidth);

                int textX = getX() + 6;
                if (entry.items() != null && !entry.items().isEmpty()) {
                    int itemSize = (int) (16 * entry.itemScale());
                    textX += entry.items().size() * (itemSize + 2) + 2;
                }

                int textHeight = lines.size() * 10;
                int textY = currentY + Math.max(2, (h - textHeight) / 2);

                for (int i = 0; i < lines.size(); i++) {
                    gg.drawString(font,
                            lines.get(i),
                            textX,
                            textY + i * 10,
                            0xFFFFFF,
                            false);
                }
            }

            currentY += h;
        }

        gg.disableScissor();

        // scrollbar
        if (scrollbar && totalHeight > height) {

            int barX = getX() + width - 6;
            int barW = 4;

            int barH = Math.max(10,
                    (int) ((float) height * height / totalHeight));

            int barY = getY() + (int)
                    ((height - barH) * (scrollAmount / (float) (totalHeight - height)));

            gg.fill(barX, getY(), barX + barW, getY() + height, 0x44FFFFFF);
            gg.fill(barX, barY, barX + barW, barY + barH, 0xFFFFFFFF);
        }
    }

    // =========================
    // INPUT
    // =========================

    private int getTextOffset(Entry entry, int mouseX, boolean hovered, int x, int y, int width) {
        Font font = Minecraft.getInstance().font;

        int textWidth = font.width(entry.text());

        if (textWidth <= width - 10) {
            return 0;
        }

        // only animate when hovered (like vanilla buttons)
        if (!hovered) {
            return 0;
        }

        long time = System.currentTimeMillis() / 10;

        int overflow = textWidth - (width - 10);

        return (int)(Math.sin(time * 0.05) * overflow * 0.5 + overflow * 0.5);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateMomentum() {
        List<Entry> list = orderedEntries();

        int totalHeight = 0;
        for (Entry e : list) totalHeight += e.height();

        float maxScroll = Math.max(0, totalHeight - height);

        // apply velocity
        scrollAmount += scrollVelocity;

        // friction (Create-style feel)
        scrollVelocity *= 0.85f;

        // stop tiny jitter
        if (Math.abs(scrollVelocity) < 0.01f) {
            scrollVelocity = 0;
        }

        // clamp
        scrollAmount = clamp(scrollAmount, 0, maxScroll);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar) {

            List<Entry> list = orderedEntries();

            int totalHeight = 0;
            for (Entry e : list) totalHeight += e.height();

            float maxScroll = Math.max(0, totalHeight - height);

            // convert mouse to scroll percent
            float percent = (float)(mouseY - getY()) / (float)height;

            scrollAmount = clamp(
                    percent * totalHeight - (height * 0.5f),
                    0,
                    maxScroll
            );

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void updateScrolling(double mouseY) {
        List<Entry> list = orderedEntries();

        int totalHeight = 0;
        for (Entry e : list) {
            totalHeight += e.height();
        }

        if (totalHeight <= height) {
            scrollAmount = 0;
            return;
        }

        float percent = (float) (mouseY - getY()) / (float) height;

        scrollAmount = clamp(
                percent * totalHeight - (height * 0.5f),
                0,
                Math.max(0, totalHeight - height)
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < getX() || mouseX >= getX() + width
                || mouseY < getY() || mouseY >= getY() + height) {
            return false;
        }

        int barX = getX() + width - 6;

        if (needsScrollbar()
                && mouseX >= barX
                && mouseX < getX() + width
                && mouseY >= getY()
                && mouseY < getY() + height) {

            draggingScrollbar = true;
            lastMouseY = mouseY;
            return true;
        }

        List<Entry> list = orderedEntries();

        int currentY = getY() - (int) scrollAmount;

        for (Entry entry : list) {

            int h = entry.height();

            if (currentY + h <= getY()) {
                currentY += h;
                continue;
            }

            if (currentY >= getY() + height) {
                break;
            }

            if (mouseX >= getX() + 1 && mouseX < getX() + width - 7 &&
                    mouseY >= currentY && mouseY < currentY + h) {

                selectedId = entry.id();
                playDownSound(Minecraft.getInstance().getSoundManager());

                EntryContext ctx = new EntryContext(this, entry.id(), entry);

                if (onSelect != null) onSelect.accept(entry.id());
                if (onSelectEntry != null) onSelectEntry.accept(ctx);

                if (entry.onClick() != null) {
                    entry.onClick().accept(ctx);
                }

                return true;
            }

            currentY += h;
        }

        return false;
    }

    // =========================
    // SCROLL
    // =========================

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        scrollVelocity += (float) -scrollY * 6f;
        return true;
    }

    // =========================
    // NARRATION (REQUIRED)
    // =========================

    private boolean needsScrollbar() {
        int total = 0;
        for (Entry e : orderedEntries()) total += e.height();
        return total > height;
    }

    private String getSearchQuery() {
        if (searchQuerySupplier == null) {
            return "";
        }

        String query = searchQuerySupplier.get();
        if (query == null) {
            return "";
        }

        return query.trim().toLowerCase(Locale.ROOT);
    }

    private int contentWidth() {
        return needsScrollbar() ? width - 7 : width;
    }

    private int calculateTextWidth(Entry entry) {
        int rowWidth = contentWidth() - 2;
        int itemWidth = 0;

        if (entry.items() != null && !entry.items().isEmpty()) {
            int itemSize = (int) (16 * entry.itemScale());
            itemWidth = entry.items().size() * (itemSize + 2) + 2;
        }

        return Math.max(20, rowWidth - 10 - itemWidth);
    }

    private int calculateEntryHeight(Entry entry) {
        int itemHeight = entry.items() != null && !entry.items().isEmpty()
                ? (int) (16 * entry.itemScale()) + 4
                : 4;

        if (!entry.showText()) {
            return itemHeight;
        }

        Font font = Minecraft.getInstance().font;
        int textHeight = Math.max(10, font.split(entry.text(), calculateTextWidth(entry)).size() * 10);

        return Math.max(itemHeight, textHeight + 4);
    }

    private int brightenColor(int color, float amount) {
        int alpha = (color >>> 24) & 0xFF;
        int red = (color >>> 16) & 0xFF;
        int green = (color >>> 8) & 0xFF;
        int blue = color & 0xFF;

        red = clamp((int) (red + (255 - red) * amount), 0, 255);
        green = clamp((int) (green + (255 - green) * amount), 0, 255);
        blue = clamp((int) (blue + (255 - blue) * amount), 0, 255);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    // =========================
    // BUILDER
    // =========================

    public static class EntryBuilder {

        private final SimpleScrollList parent;
        private final Object id;

        private Component text = Component.empty();
        private List<ItemStack> items = List.of();
        private float scale = 1f;
        private boolean showText = true;
        private int bgColor = 0;
        private Consumer<EntryContext> onClick;

        public EntryBuilder(SimpleScrollList parent, Object id) {
            this.parent = parent;
            this.id = id;
        }

        public EntryBuilder text(Component t) { this.text = t; return this; }
        public EntryBuilder items(List<ItemStack> i) { this.items = i; return this; }
        public EntryBuilder itemScale(float s) { this.scale = s; return this; }
        public EntryBuilder backgroundColor(int c) { this.bgColor = c; return this; }

        public EntryBuilder onClick(Consumer<EntryContext> c) {
            this.onClick = c;
            return this;
        }

        public void add() {
            Entry entry = new Entry(
                    id, text, items, List.of(),
                    scale, showText, 0,
                    null, bgColor, List.of(),
                    onClick
            );

            parent.addEntry(entry.withHeight(parent.calculateEntryHeight(entry)));
        }
    }
}
