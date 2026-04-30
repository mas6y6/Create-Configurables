package com.mas6y6.configureablecrushingwheel.client;

import com.mas6y6.configureablecrushingwheel.client.gui.ConfigureCrushingWheelScreenMain;
import com.mas6y6.configureablecrushingwheel.client.gui.ConfigureCrushingWheelScreenRecipe;
import com.mas6y6.configureablecrushingwheel.client.gui.ConfigureMillstoneScreenMain;
import com.mas6y6.configureablecrushingwheel.common.packets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class PacketHandle {
    public static void OpenRecipeGui(OpenCrushingWheelRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        Minecraft.getInstance().setScreen(new ConfigureCrushingWheelScreenMain(packet.uuid()));
    }

    public static void getConflictingRecipesResponsePacket(GetConflictingRecipesResponsePacket packet, IPayloadContext iPayloadContext) {
        if (Minecraft.getInstance().screen instanceof ConfigureCrushingWheelScreenMain screen) {
            screen.noConflict = packet.recipeConflicts().recipes.isEmpty();
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

            screen.applyResolvedConflictHighlights();
        }
    }

    public static void GetCrushingWheelConfigResponsePacket(GetCrushingWheelConfigResponsePacket packet, IPayloadContext iPayloadContext) {
        if (Minecraft.getInstance().screen instanceof ConfigureCrushingWheelScreenMain screen) {
            screen.config = packet.config();
            screen.applyResolvedConflictHighlights();
        } else if (Minecraft.getInstance().screen instanceof ConfigureCrushingWheelScreenRecipe screen) {
            screen.config = packet.config();
            screen.applyConfiguredSelection();
        }
    }

    public static void OpenMillstoneRecipeGuiPacket(OpenMillstoneRecipeGuiPacket packet, IPayloadContext iPayloadContext) {
        Minecraft.getInstance().setScreen(new ConfigureMillstoneScreenMain(packet.uuid()));
    }

    public static void GetMillingWheelConfigResponsePacket(GetMillstoneWheelConfigResponsePacket getMillingWheelConfigResponsePacket, IPayloadContext iPayloadContext) {

    }
}
