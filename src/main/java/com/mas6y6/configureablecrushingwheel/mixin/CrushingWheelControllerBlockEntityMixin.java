package com.mas6y6.configureablecrushingwheel.mixin;

import com.mas6y6.configureablecrushingwheel.client.ConfigureablecrushingwheelClient;
import com.mas6y6.configureablecrushingwheel.common.IConfiguredCrushingWheelUUID;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CrushingWheelControllerBlockEntity.class)
public class CrushingWheelControllerBlockEntityMixin implements IConfiguredCrushingWheelUUID {
    @Unique
    private UUID customUUID;

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
}