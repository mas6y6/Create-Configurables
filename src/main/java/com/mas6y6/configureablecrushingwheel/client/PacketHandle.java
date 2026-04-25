package com.mas6y6.configureablecrushingwheel.client;

import com.mas6y6.configureablecrushingwheel.client.gui.ConfigureCrushingWheelScreen;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesResponsePacket;
import com.mas6y6.configureablecrushingwheel.common.packets.OpenRecipeGuiPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class PacketHandle {
    public static void OpenRecipeGui(OpenRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        Minecraft.getInstance().setScreen(new ConfigureCrushingWheelScreen(packet.uuid()));
    }

    public static void getConflictingRecipesResponsePacket(GetConflictingRecipesResponsePacket packet, IPayloadContext iPayloadContext) {
        ConfigureCrushingWheelScreen screen = (ConfigureCrushingWheelScreen) Minecraft.getInstance().screen;
        if (screen != null) {
            screen.noConflict = false;
            screen.scrollList.clear();
            packet.recipeConflicts().recipes.forEach((item, conflicts) -> {
                net.minecraft.network.chat.MutableComponent text = item.getDescription().copy();
                text.append(" ("+conflicts.size()+" recipes)");
                screen.scrollList.entry(item.getDefaultInstance().getItem())
                        .items(List.of(item.getDefaultInstance().getItem().getDefaultInstance()))
                        .text(text)
                        .add();
            });
        }
    }
}
