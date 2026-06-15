package com.mas6y6.createconfigurables.mixin;

import com.mas6y6.createconfigurables.CreateConfigurables;
import com.mas6y6.createconfigurables.common.IConfiguredItemDrainUUID;
import com.mas6y6.createconfigurables.common.ItemDrainConfig;
import com.mas6y6.createconfigurables.server.CreateConfigurablesServer;
import com.mas6y6.createconfigurables.server.world.WorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(value = ItemDrainBlockEntity.class)
public class ItemDrainBlockEntityMixin implements IConfiguredItemDrainUUID {
    @Unique
    private static final String CONFIGUREABLE_ID =
            "configureable_item_drain_id";

    @Unique
    private UUID customUUID;

    @Shadow
    TransportedItemStack heldItem;

    @Inject(method = "write", at = @At("TAIL"))
    private void writeUUID(CompoundTag compound,
                           HolderLookup.Provider registries,
                           boolean clientPacket,
                           CallbackInfo ci) {
        if (customUUID != null) {
            compound.putUUID(CONFIGUREABLE_ID, customUUID);
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readUUID(CompoundTag compound,
                          HolderLookup.Provider registries,
                          boolean clientPacket,
                          CallbackInfo ci) {

        if (compound.hasUUID(CONFIGUREABLE_ID)) {
            customUUID = compound.getUUID(CONFIGUREABLE_ID);
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
            method = "continueProcessing",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/fluids/transfer/GenericItemEmptying;emptyItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Z)Lnet/createmod/catnip/data/Pair;"
            )
    )
    private Pair<FluidStack, ItemStack> wrapEmptyItem(
            Level level,
            ItemStack stack,
            boolean simulate
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return GenericItemEmptying.emptyItem(level, stack, simulate);
        }

        IConfiguredItemDrainUUID thing = (IConfiguredItemDrainUUID) (Object) this;
        CreateConfigurables.LOGGER.debug("Intercepted level={} stack={} simulate={} UUID={}", level, stack, simulate, thing.getUUID());

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(heldItem.stack.getItem());
        ItemDrainConfig config = WorldData.get(serverLevel).getItemDrain(getUUID());
        ResourceLocation requestedRecipeId = config.config.get(itemId);

        if (requestedRecipeId == null) {
            CreateConfigurablesServer.LOGGER.debug("No configured item drain recipe found for item {}", itemId);
            return GenericItemEmptying.emptyItem(level, stack, simulate);
        }

        RecipeHolder<Recipe<SingleRecipeInput>> recipe = null;

        for (RecipeHolder<?> holder : level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.EMPTYING.getType())) {
            if (!(holder.value() instanceof EmptyingRecipe emptyingRecipe)) {
                continue;
            }

            if (holder.id().equals(requestedRecipeId) && emptyingRecipe.matches(new SingleRecipeInput(stack), level)) {
                CreateConfigurablesServer.LOGGER.debug("Using preferred emptying recipe {}", requestedRecipeId);
                recipe = (RecipeHolder<Recipe<SingleRecipeInput>>) holder;
                break;
            }
        }
        // Just stealing the original function because I don't want to rewrite it and deal with more complexity

        FluidStack resultingFluid = FluidStack.EMPTY;
        ItemStack resultingItem = ItemStack.EMPTY;

        if (PotionFluidHandler.isPotionItem(stack))
            return PotionFluidHandler.emptyPotion(stack, simulate);

        /*

        I am gonna use the code inside the
        ```
        Optional<RecipeHolder<Recipe<SingleRecipeInput>>> recipe = AllRecipeTypes.EMPTYING.find(new SingleRecipeInput(stack), level);
		if (recipe.isPresent()) {*}
		```

		because the recipe is always gonna be present since the recipe id.

        */

        EmptyingRecipe emptyingRecipe = (EmptyingRecipe) recipe.value();
        List<ItemStack> results = emptyingRecipe.rollResults(level.random);
        if (!simulate)
            stack.shrink(1);
        resultingItem = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        resultingFluid = emptyingRecipe.getResultingFluid();
        return Pair.of(resultingFluid, resultingItem);
    }
}
