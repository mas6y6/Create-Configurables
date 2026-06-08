package com.mas6y6.configureablecrushingwheel.mixin;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredItemDrainUUID;
import com.mas6y6.configureablecrushingwheel.common.ItemDrainConfig;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import com.mas6y6.configureablecrushingwheel.server.world.WorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemDrainBlock.class)
public class ItemDrainBlockMixin {
    @Redirect(
            method = "tryExchange",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/fluid/FluidHelper;tryEmptyItemIntoBE(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;)Z"
            )
    )
    private boolean tryEmptyItemIntoBE(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem, SmartBlockEntity be) {
        if (!(be instanceof ItemDrainBlockEntity)) {
            return FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be);
        }

        if (!(be instanceof IConfiguredItemDrainUUID iConfiguredItemDrainUUID)) {
            return FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be);
        }

        // Copying code again from FluidHelper.tryEmptyItemIntoBE(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem, SmartBlockEntity be)

        if (!GenericItemEmptying.canItemBeEmptied(worldIn, heldItem))
            return false;

        Pair<FluidStack, ItemStack> emptyingResult = emptyItemWithConfiguration(worldIn, heldItem, true, iConfiguredItemDrainUUID);
        IFluidHandler capability = worldIn.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        FluidStack fluidStack = emptyingResult.getFirst();

        if (capability == null || fluidStack.getAmount() != capability.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE))
            return false;
        if (worldIn.isClientSide)
            return true;

        if (capability == null || fluidStack.getAmount() != capability.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE))
            return false;
        if (worldIn.isClientSide)
            return true;

        ItemStack copyOfHeld = heldItem.copy();
        emptyingResult = GenericItemEmptying.emptyItem(worldIn, copyOfHeld, false);
        capability.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

        if (!player.isCreative() && !(be instanceof CreativeFluidTankBlockEntity)) {
            if (copyOfHeld.isEmpty())
                player.setItemInHand(handIn, emptyingResult.getSecond());
            else {
                player.setItemInHand(handIn, copyOfHeld);
                player.getInventory()
                        .placeItemBackInInventory(emptyingResult.getSecond());
            }
        }
        return true;
    }

    // from ItemDrainBlockEntityMixin
    @Unique
    private Pair<FluidStack, ItemStack> emptyItemWithConfiguration(Level level, ItemStack stack, boolean simulate, IConfiguredItemDrainUUID iConfiguredItemDrainUUID) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return GenericItemEmptying.emptyItem(level, stack, simulate);
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ItemDrainConfig config = WorldData.get(serverLevel).getItemDrain(iConfiguredItemDrainUUID.getUUID());
        ResourceLocation requestedRecipeId = config.config.get(itemId);

        if (requestedRecipeId == null) {
            ConfigureablecrushingwheelServer.LOGGER.debug("No configured item drain recipe found for item {}", itemId);
            return GenericItemEmptying.emptyItem(level, stack, simulate);
        }

        RecipeHolder<Recipe<SingleRecipeInput>> recipe = null;

        for (RecipeHolder<?> holder : level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.EMPTYING.getType())) {
            if (!(holder.value() instanceof EmptyingRecipe emptyingRecipe)) {
                continue;
            }

            if (holder.id().equals(requestedRecipeId) && emptyingRecipe.matches(new SingleRecipeInput(stack), level)) {
                ConfigureablecrushingwheelServer.LOGGER.debug("Using preferred emptying recipe {}", requestedRecipeId);
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
