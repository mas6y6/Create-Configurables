package com.mas6y6.createconfigurables.client.gui.Components;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LimitedSlot extends Slot {
    private final Item matchItem;

    public LimitedSlot(Container container, int index, int x, int y, Item matchItem) {
        super(container, index, x, y);
        this.matchItem = matchItem;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(matchItem);
    }
}
