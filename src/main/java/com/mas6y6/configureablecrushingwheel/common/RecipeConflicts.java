package com.mas6y6.configureablecrushingwheel.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeConflicts {

    public Map<Item, List<ResourceLocation>> recipes = new HashMap<>();

    public void addConflict(Item item, List<ResourceLocation> recipes) {
        this.recipes.put(item, recipes);
    }

    public static final StreamCodec<FriendlyByteBuf, RecipeConflicts> STREAM_CODEC =
            StreamCodec.of(
                    (buf, conflict) -> {
                        buf.writeInt(conflict.recipes.size());

                        conflict.recipes.forEach((item, recipeList) -> {
                            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));

                            buf.writeInt(recipeList.size());
                            for (ResourceLocation id : recipeList) {
                                buf.writeResourceLocation(id);
                            }
                        });
                    },
                    buf -> {
                        RecipeConflicts conflicts = new RecipeConflicts();

                        int mapSize = buf.readInt();

                        for (int i = 0; i < mapSize; i++) {
                            Item item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());

                            int recipeCount = buf.readInt();
                            List<ResourceLocation> recipeList = new ArrayList<>();

                            for (int j = 0; j < recipeCount; j++) {
                                recipeList.add(buf.readResourceLocation());
                            }

                            conflicts.addConflict(item, recipeList);
                        }

                        return conflicts;
                    }
            );
}