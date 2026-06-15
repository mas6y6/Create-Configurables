package com.mas6y6.createconfigurables.common;

import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
    public static ResourceLocation buildSignatureId(CrushingRecipe r) {
        StringBuilder sb = new StringBuilder();

        for (Ingredient ing : r.getIngredients()) {
            for (ItemStack stack : ing.getItems()) {
                sb.append(stack.getItem()).append("x").append(stack.getCount()).append(";");
            }
            sb.append("|");
        }

        sb.append("->");

        for (ProcessingOutput out : r.getRollableResults()) {
            ItemStack stack = out.getStack();
            sb.append(stack.getItem()).append("x").append(stack.getCount()).append(";");
        }

        sb.append("|");

        sb.append(r.getType().toString());

        String hash = Integer.toHexString(sb.toString().hashCode());

        return ResourceLocation.fromNamespaceAndPath("conflicts", hash);
    }

    public static Set<Item> expandIngredient(Ingredient ingredient) {
        Set<Item> items = new HashSet<>();
        for (ItemStack stack : ingredient.getItems()) {
            items.add(stack.getItem());
        }
        return items;
    }

    public static ResourceLocation buildNormalizedSignature(CrushingRecipe recipe) {
        StringBuilder sb = new StringBuilder();

        List<Set<Item>> inputs = new ArrayList<>();

        for (Ingredient ing : recipe.getIngredients()) {
            inputs.add(expandIngredient(ing));
        }

        inputs.stream()
                .map(set -> {
                    return set.stream()
                            .map(item -> item.builtInRegistryHolder().key().location().toString())
                            .sorted()
                            .reduce("", (a, b) -> a + b + ",");
                })
                .sorted()
                .forEach(sb::append);

        sb.append("|");

        for (ProcessingOutput out : recipe.getRollableResults()) {
            ItemStack stack = out.getStack();
            if (!stack.isEmpty()) {
                sb.append(stack.getItem().builtInRegistryHolder().key().location());
                sb.append("x").append(stack.getCount()).append(";");
            }
        }

        String hash = Integer.toHexString(sb.toString().hashCode());

        return ResourceLocation.fromNamespaceAndPath("conflicts", hash);
    }
}
