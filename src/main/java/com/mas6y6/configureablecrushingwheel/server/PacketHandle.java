package com.mas6y6.configureablecrushingwheel.server;


import com.mas6y6.configureablecrushingwheel.common.RecipeConflicts;
import com.mas6y6.configureablecrushingwheel.common.packets.*;
import com.mas6y6.configureablecrushingwheel.server.world.WorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
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
    public static void GetConflictingRecipesPacket(GetCrushingConflictingRecipesPacket packet, IPayloadContext ctx) {
        if (ctx.player().level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) ctx.player();
        ServerLevel level = player.serverLevel();
        RecipeManager manager = level.getRecipeManager();

        RecipeConflicts conflicts = new RecipeConflicts();

        Map<Item, Set<ResourceLocation>> itemToRecipes = new HashMap<>();

        for (var holder : manager.getAllRecipesFor(AllRecipeTypes.CRUSHING.getType())) {
            if (!(holder.value() instanceof CrushingRecipe crushing)) continue;

            if (crushing.getIngredients().isEmpty()) continue;
            Ingredient ingredient = crushing.getIngredients().getFirst();

            for (ItemStack stack : ingredient.getItems()) {
                itemToRecipes
                        .computeIfAbsent(stack.getItem(), i -> new HashSet<>())
                        .add(holder.id());
            }

            conflicts.inputs.put(holder.id(), crushing.getIngredients().getFirst().getItems()[0]);

            List<ItemStack> outputs = new ArrayList<>();

            for (ProcessingOutput output : crushing.getRollableResults()) {
                outputs.add(output.getStack());
            }

            conflicts.outputs.put(holder.id(), outputs);
        }

        itemToRecipes.forEach((item, recipes) -> {
            if (recipes.size() > 1) {
                conflicts.addConflict(item, new ArrayList<>(recipes));
            }
        });

        PacketDistributor.sendToPlayer(player,
                new GetConflictingRecipesResponsePacket(conflicts));
    }

    public static void GetCrushingWheelConfigPacket(GetCrushingWheelConfigPacket packet, IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().isClientSide()) return;

        ServerLevel level = (ServerLevel) iPayloadContext.player().level();
        WorldData data = WorldData.get(level);

        PacketDistributor.sendToPlayer(
                (ServerPlayer) iPayloadContext.player(),
                new GetCrushingWheelConfigResponsePacket(packet.uuid(),data.getCrushingWheel(packet.uuid()))
        );
    }

    public static void SetConfigurationPacket(SetConfigurationPacket packet, IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().isClientSide()) return;

        ServerLevel level = (ServerLevel) iPayloadContext.player().level();
        WorldData data = WorldData.get(level);

        if (packet.config().config.isEmpty()) {
            data.removeCrushingWheel(packet.config().uuid);
            return;
        }

        data.putCrushingWheel(packet.config().uuid, packet.config());
    }

    public static void SetMillstoneConfigurationPacket(SetMillstoneConfigurationPacket packet, IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().isClientSide()) return;

        ServerLevel level = (ServerLevel) iPayloadContext.player().level();
        WorldData data = WorldData.get(level);

        if (packet.config().config.isEmpty()) {
            data.removeMillstone(packet.config().uuid);
            return;
        }

        data.putMillstone(packet.config().uuid, packet.config());
    }

    public static void GetMillingConflictingRecipesPacket(GetMillstoneConflictingRecipesPacket packet, IPayloadContext ctx) {
        if (ctx.player().level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) ctx.player();
        ServerLevel level = player.serverLevel();
        RecipeManager manager = level.getRecipeManager();

        RecipeConflicts conflicts = new RecipeConflicts();

        Map<Item, Set<ResourceLocation>> itemToRecipes = new HashMap<>();

        for (var holder : manager.getAllRecipesFor(AllRecipeTypes.MILLING.getType())) {
            if (!(holder.value() instanceof MillingRecipe milling)) continue;

            if (milling.getIngredients().isEmpty()) continue;
            Ingredient ingredient = milling.getIngredients().getFirst();

            for (ItemStack stack : ingredient.getItems()) {
                itemToRecipes
                        .computeIfAbsent(stack.getItem(), i -> new HashSet<>())
                        .add(holder.id());
            }

            conflicts.inputs.put(holder.id(), milling.getIngredients().getFirst().getItems()[0]);

            List<ItemStack> outputs = new ArrayList<>();

            for (ProcessingOutput output : milling.getRollableResults()) {
                outputs.add(output.getStack());
            }

            conflicts.outputs.put(holder.id(), outputs);
        }

        itemToRecipes.forEach((item, recipes) -> {
            if (recipes.size() > 1) {
                conflicts.addConflict(item, new ArrayList<>(recipes));
            }
        });

        PacketDistributor.sendToPlayer(player,
                new GetConflictingRecipesResponsePacket(conflicts));
    }

    public static void GetMillingWheelConfigPacket(GetMillstoneWheelConfigPacket packet, IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().isClientSide()) return;

        ServerLevel level = (ServerLevel) iPayloadContext.player().level();
        WorldData data = WorldData.get(level);

        PacketDistributor.sendToPlayer(
                (ServerPlayer) iPayloadContext.player(),
                new GetMillstoneWheelConfigResponsePacket(packet.uuid(),data.getMillstone(packet.uuid()))
        );
    }
}
