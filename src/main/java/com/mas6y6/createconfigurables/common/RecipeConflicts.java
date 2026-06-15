package com.mas6y6.createconfigurables.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeConflicts {

    public Map<Item, List<ResourceLocation>> recipes = new HashMap<>();
    public Map<ResourceLocation, ItemStack> inputs = new HashMap<>();
    public Map<ResourceLocation, List<ItemStack>> outputs = new HashMap<>();
    public Map<ResourceLocation, List<FluidStack>> fluidOutputs = new HashMap<>();

    public void addConflict(Item item, List<ResourceLocation> recipes) {
        this.recipes.put(item, recipes);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeConflicts> STREAM_CODEC =
            StreamCodec.of(
                    (buf, conflict) -> {

                        // ===== recipes =====
                        buf.writeInt(conflict.recipes.size());

                        conflict.recipes.forEach((item, recipeList) -> {
                            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));

                            buf.writeInt(recipeList.size());
                            for (ResourceLocation id : recipeList) {
                                buf.writeResourceLocation(id);
                            }
                        });

                        // ===== inputs =====
                        buf.writeInt(conflict.inputs.size());

                        conflict.inputs.forEach((id, stack) -> {
                            buf.writeResourceLocation(id);
                            ItemStack.STREAM_CODEC.encode(buf, stack);
                        });

                        // ===== outputs =====
                        buf.writeInt(conflict.outputs.size());

                        conflict.outputs.forEach((id, stacks) -> {
                            buf.writeResourceLocation(id);

                            buf.writeInt(stacks.size());
                            for (ItemStack stack : stacks) {
                                ItemStack.STREAM_CODEC.encode(buf, stack);
                            }
                        });

                        // ===== fluid outputs =====
                        buf.writeInt(conflict.fluidOutputs.size());

                        conflict.fluidOutputs.forEach((id, stacks) -> {
                            buf.writeResourceLocation(id);

                            buf.writeInt(stacks.size());
                            for (FluidStack stack : stacks) {
                                FluidStack.STREAM_CODEC.encode(buf, stack);
                            }
                        });
                    },

                    buf -> {
                        RecipeConflicts conflicts = new RecipeConflicts();

                        // ===== recipes =====
                        int recipeMapSize = buf.readInt();

                        for (int i = 0; i < recipeMapSize; i++) {
                            Item item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());

                            int recipeCount = buf.readInt();
                            List<ResourceLocation> recipeList = new ArrayList<>();

                            for (int j = 0; j < recipeCount; j++) {
                                recipeList.add(buf.readResourceLocation());
                            }

                            conflicts.recipes.put(item, recipeList);
                        }

                        // ===== inputs =====
                        int inputSize = buf.readInt();

                        for (int i = 0; i < inputSize; i++) {
                            ResourceLocation id = buf.readResourceLocation();
                            ItemStack stack = ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);

                            conflicts.inputs.put(id, stack);
                        }

                        // ===== outputs =====
                        int outputSize = buf.readInt();

                        for (int i = 0; i < outputSize; i++) {
                            ResourceLocation id = buf.readResourceLocation();

                            int stackCount = buf.readInt();
                            List<ItemStack> stacks = new ArrayList<>();

                            for (int j = 0; j < stackCount; j++) {
                                stacks.add(ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
                            }

                            conflicts.outputs.put(id, stacks);
                        }

                        // ===== fluid outputs =====
                        int fluidOutputSize = buf.readInt();

                        for (int i = 0; i < fluidOutputSize; i++) {
                            ResourceLocation id = buf.readResourceLocation();

                            int stackCount = buf.readInt();
                            List<FluidStack> stacks = new ArrayList<>();

                            for (int j = 0; j < stackCount; j++) {
                                stacks.add(FluidStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
                            }

                            conflicts.fluidOutputs.put(id, stacks);
                        }

                        return conflicts;
                    }
            );
}
