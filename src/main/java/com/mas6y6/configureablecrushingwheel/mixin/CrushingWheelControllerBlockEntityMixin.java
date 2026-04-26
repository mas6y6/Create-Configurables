package com.mas6y6.configureablecrushingwheel.mixin;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredCrushingWheelUUID;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import com.mas6y6.configureablecrushingwheel.server.world.ConfiguredCrushingWheelsWorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(CrushingWheelControllerBlockEntity.class)
public class CrushingWheelControllerBlockEntityMixin implements IConfiguredCrushingWheelUUID {
    @Unique
    private UUID customUUID;

    @Shadow
    private RecipeWrapper wrapper;

    @Inject(method = "write", at = @At("TAIL"))
    private void writeUUID(CompoundTag compound,
                           HolderLookup.Provider registries,
                           boolean clientPacket,
                           CallbackInfo ci) {

        if (customUUID != null) {
            compound.putUUID("configureable_crushing_wheel_id", customUUID);
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readUUID(CompoundTag compound,
                          HolderLookup.Provider registries,
                          boolean clientPacket,
                          CallbackInfo ci) {

        if (compound.hasUUID("configureable_crushing_wheel_id")) {
            this.customUUID = compound.getUUID("configureable_crushing_wheel_id");
        } else {
            this.customUUID = UUID.randomUUID();

            if ((Object) this instanceof BlockEntity be) {
                be.setChanged();
            }
        }
    }

    @Override
    public UUID getUUID() {
        if (customUUID == null) {
            customUUID = UUID.randomUUID();

            if ((Object) this instanceof BlockEntity be) {
                be.setChanged();
            }
        }
        return customUUID;
    }

    @Inject(
            method = {"findRecipe"},
            at = {@At("RETURN")},
            cancellable = true,
            remap = false,
            require = 0
    )
    public void onFindRecipe(CallbackInfoReturnable<Optional<RecipeHolder<? extends AbstractCrushingRecipe>>> cir) {
        try {
            CrushingWheelControllerBlockEntity blockEntity = (CrushingWheelControllerBlockEntity)(Object)this;
            Level level = blockEntity.getLevel();
            if (!(level instanceof ServerLevel)) {
                return;
            }

            ConfiguredCrushingWheelsWorldData data = ConfiguredCrushingWheelsWorldData.get((ServerLevel) level);
            ResourceLocation itemID = BuiltInRegistries.ITEM.getKey(wrapper.getItem(0).getItem());
            ResourceLocation requestedRecipeId = data.get(getUUID()).config.get(itemID);

            if (requestedRecipeId == null) {
                return;
            }

            RecipeHolder<? extends AbstractCrushingRecipe> preferredRecipe = null;

            for (RecipeHolder<?> holder : level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.CRUSHING.getType())) {
                if (!(holder.value() instanceof AbstractCrushingRecipe)) {
                    continue;
                }

                if (!Objects.equals(holder.id(), requestedRecipeId)) {
                    continue;
                }

                preferredRecipe = (RecipeHolder<? extends AbstractCrushingRecipe>) holder;
                break;
            }

            if (preferredRecipe != null) {
                ConfigureablecrushingwheelServer.LOGGER.debug("Using preferred crushing recipe {}", preferredRecipe.id());
                cir.setReturnValue(Optional.of(preferredRecipe));
            } else {
                ConfigureablecrushingwheelServer.LOGGER.debug("Preferred crushing recipe {} was not found for item {}", requestedRecipeId, itemID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
