package com.mas6y6.configureablecrushingwheel.mixin;

import com.mas6y6.configureablecrushingwheel.common.IConfiguredMillstoneUUID;
import com.mas6y6.configureablecrushingwheel.server.ConfigureablecrushingwheelServer;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin implements IConfiguredMillstoneUUID {
    @Unique
    private static final String CONFIGUREABLE_MILLSTONE_WHEEL_ID = "configureable_millstone_wheel_id";

    @Unique
    private UUID customUUID;

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
            compound.putUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID, customUUID);
        }
    }

    @Unique
    private void readCustomUuid(CompoundTag compound) {
        if (compound.hasUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID)) {
            this.customUUID = compound.getUUID(CONFIGUREABLE_MILLSTONE_WHEEL_ID);
            ConfigureablecrushingwheelServer.LOGGER.debug("Loaded Millstone Wheel UUID {}", customUUID);
            return;
        }

        if (this.customUUID == null) {
            assignNewUuid();
        }
    }

    @Unique
    private void assignNewUuid() {
        customUUID = UUID.randomUUID();
        ConfigureablecrushingwheelServer.LOGGER.debug("Generated new UUID for Millstone {}", customUUID);

        if ((Object) this instanceof BlockEntity be) {
            be.setChanged();
        }
    }


}
