package com.mas6y6.createconfigurables.mixin;

import com.mas6y6.createconfigurables.common.IConfiguredCrushingWheelUUID;
import com.mas6y6.createconfigurables.server.world.WorldData;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
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

@SuppressWarnings({"all"}) // The mixin should work fine so i am gonna suppress the warnings
@Mixin(CrushingWheelControllerBlockEntity.class)
public class CrushingWheelControllerBlockEntityMixin implements IConfiguredCrushingWheelUUID {
    @Unique
    private static final String CONFIGUREABLE_CRUSHING_WHEEL_ID = "configureable_crushing_wheel_id";

    @Unique
    private UUID customUUID;

    @Shadow
    private RecipeWrapper wrapper;

    @Inject(method = "write", at = @At("TAIL"))
    private void writeUUID(CompoundTag compound,
                           HolderLookup.Provider registries,
                           boolean clientPacket,
                           CallbackInfo ci) {
        writeCustomUuid(compound);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), require = 0)
    private void saveAdditionalUUID(CompoundTag compound,
                                    HolderLookup.Provider registries,
                                    CallbackInfo ci) {
        writeCustomUuid(compound);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readUUID(CompoundTag compound,
                          HolderLookup.Provider registries,
                          boolean clientPacket,
                          CallbackInfo ci) {
        readCustomUuid(compound);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"), require = 0)
    private void loadAdditionalUUID(CompoundTag compound,
                                    HolderLookup.Provider registries,
                                    CallbackInfo ci) {
        readCustomUuid(compound);
    }

    @Override
    public UUID getUUID() {
        if (customUUID == null) {
            assignNewUuid();
        }
        return customUUID;
    }

    @Unique
    private void writeCustomUuid(CompoundTag compound) {
        if (customUUID != null) {
            compound.putUUID(CONFIGUREABLE_CRUSHING_WHEEL_ID, customUUID);
        }
    }

    @Unique
    private void readCustomUuid(CompoundTag compound) {
        if (compound.hasUUID(CONFIGUREABLE_CRUSHING_WHEEL_ID)) {
            this.customUUID = compound.getUUID(CONFIGUREABLE_CRUSHING_WHEEL_ID);
            return;
        }

        if (this.customUUID == null) {
            assignNewUuid();
        }
    }

    @Unique
    private void assignNewUuid() {
        customUUID = UUID.randomUUID();

        if ((Object) this instanceof BlockEntity be) {
            be.setChanged();
        }
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

            WorldData data = WorldData.get((ServerLevel) level);
            ResourceLocation itemID = BuiltInRegistries.ITEM.getKey(wrapper.getItem(0).getItem());
            ResourceLocation requestedRecipeId = data.getCrushingWheel(getUUID()).config.get(itemID);

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
                cir.setReturnValue(Optional.of(preferredRecipe));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
