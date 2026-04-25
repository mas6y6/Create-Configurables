package com.mas6y6.configureablecrushingwheel.client;

import com.mas6y6.configureablecrushingwheel.client.gui.ConfigureCrushingWheelScreenMain;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesResponsePacket;
import com.mas6y6.configureablecrushingwheel.common.packets.GetCrushingWheelConfigResponsePacket;
import com.mas6y6.configureablecrushingwheel.common.packets.OpenRecipeGuiPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class PacketHandle {
    public static void OpenRecipeGui(OpenRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        Minecraft.getInstance().setScreen(new ConfigureCrushingWheelScreenMain(packet.uuid()));
    }

    public static void getConflictingRecipesResponsePacket(GetConflictingRecipesResponsePacket packet, IPayloadContext iPayloadContext) {
        if (Minecraft.getInstance().screen instanceof ConfigureCrushingWheelScreenMain screen) {
            screen.noConflict = false;
            screen.scrollList.clear();
            screen.recipeConflicts = packet.recipeConflicts();

            packet.recipeConflicts().recipes.forEach((item, conflicts) -> {
                MutableComponent text = item.getDescription().copy();
                text.append(" (" + conflicts.size() + " recipes)");

                screen.scrollList.entry(item.getDefaultInstance().getItem())
                        .items(List.of(item.getDefaultInstance().getItem().getDefaultInstance()))
                        .text(text)
                        .add();
            });
        }
    }

    public static void GetCrushingWheelConfigResponsePacket(GetCrushingWheelConfigResponsePacket packet, IPayloadContext iPayloadContext) {

    }
}
