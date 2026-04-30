package com.mas6y6.configureablecrushingwheel.mixin;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredMillstoneUUID;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import com.mas6y6.configureablecrushingwheel.server.world.WorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin implements IConfiguredMillstoneUUID {

    @Unique
    private static final String CONFIGUREABLE_MILLSTONE_WHEEL_ID =
            "configureable_millstone_wheel_id";

    @Unique
    private UUID customUUID;

    @Shadow
    private MillingRecipe lastRecipe;


    @Inject(method = "write", at = @At("TAIL"))
    private void writeUUID(CompoundTag compound,
                           HolderLookup.Provider registries,
                           boolean clientPacket,
                           CallbackInfo ci) {
        if (customUUID != null) {
            compound.putUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID, customUUID);
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readUUID(CompoundTag compound,
                          HolderLookup.Provider registries,
                          boolean clientPacket,
                          CallbackInfo ci) {

        if (compound.hasUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID)) {
            customUUID = compound.getUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID);
        } else {
            customUUID = UUID.randomUUID();
        }
    }

    @Override
    public UUID getUUID() {
        if (customUUID == null) {
            customUUID = UUID.randomUUID();
        }
        return customUUID;
    }


    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private Optional<RecipeHolder<MillingRecipe>> redirectTickRecipeLookup(AllRecipeTypes recipeType,
                                                                           RecipeInput inventoryIn,
                                                                           Level level) {
        return getConfiguredRecipe(recipeType, inventoryIn, level);
    }

    @Redirect(
            method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private Optional<RecipeHolder<MillingRecipe>> redirectProcessRecipeLookup(AllRecipeTypes recipeType,
                                                                              RecipeInput inventoryIn,
                                                                              Level level) {
        return getConfiguredRecipe(recipeType, inventoryIn, level);
    }

    @Unique
    private Optional<RecipeHolder<MillingRecipe>> getConfiguredRecipe(AllRecipeTypes recipeType,
                                                                      RecipeInput inventoryIn,
                                                                      Level level) {
        ConfigureablecrushingwheelServer.LOGGER.debug("Looking for configured millstone recipe for item {}", inventoryIn.getItem(0).getItem());
        Optional<RecipeHolder<MillingRecipe>> fallback = recipeType.find(inventoryIn, level);

        if (level == null || level.isClientSide()) {
            ConfigureablecrushingwheelServer.LOGGER.debug("Using default UUID for configured millstone recipe for item {}", inventoryIn.getItem(0).getItem());
            return fallback;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(inventoryIn.getItem(0).getItem());
        MillstoneConfig config = WorldData.get((ServerLevel) level).getMillstone(getUUID());
        ResourceLocation requestedRecipeId = config.config.get(itemId);

        if (requestedRecipeId == null) {
            ConfigureablecrushingwheelServer.LOGGER.debug("No configured millstone recipe found for item {}", itemId);
            return fallback;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(AllRecipeTypes.MILLING.getType())) {
            if (!(holder.value() instanceof MillingRecipe millingRecipe)) {
                continue;
            }

            if (holder.id().equals(requestedRecipeId) && millingRecipe.matches(inventoryIn, level)) {
                ConfigureablecrushingwheelServer.LOGGER.debug("Using preferred millstone recipe {}", requestedRecipeId);
                return Optional.of((RecipeHolder<MillingRecipe>) holder);
            }
        }

        ConfigureablecrushingwheelServer.LOGGER.debug("Preferred millstone recipe {} was not found for item {}", requestedRecipeId, itemId);
        return fallback;
    }
}
