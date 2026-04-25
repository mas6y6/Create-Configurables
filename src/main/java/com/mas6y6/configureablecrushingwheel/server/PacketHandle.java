package com.mas6y6.configureablecrushingwheel.server;


import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesPacket;
import com.mas6y6.configureablecrushingwheel.common.packets.GetConflictingRecipesResponsePacket;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;

public class PacketHandle {
    public static void GetConflictingRecipesPacket(GetConflictingRecipesPacket packet, IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().isClientSide()) return;

        ServerLevel level = (ServerLevel) iPayloadContext.player().level();
        RecipeManager manager = level.getRecipeManager();
        RecipeConflicts conflicts = new RecipeConflicts();

        Map<Item, List<ResourceLocation>> itemToRecipes = new HashMap<>();

        manager.getAllRecipesFor(AllRecipeTypes.CRUSHING.getType()).forEach(holder -> {
            var recipe = holder.value();

            if (!(recipe instanceof CrushingRecipe crushing)) return;

            for (Ingredient ingredient : crushing.getIngredients()) {
                for (ItemStack stack : ingredient.getItems()) {
                    itemToRecipes
                            .computeIfAbsent(stack.getItem(), i -> new ArrayList<>())
                            .add(holder.id());
                }
            }
        });

        itemToRecipes.forEach((item, recipes) -> {
            if (recipes.size() > 1) {
                conflicts.addConflict(item, recipes);
            }
        });

        PacketDistributor.sendToPlayer((ServerPlayer) iPayloadContext.player(),new GetConflictingRecipesResponsePacket(conflicts));
    }
}
