package com.mas6y6.configureablecrushingwheel.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class MillstoneConfig {
    public Map<ResourceLocation, ResourceLocation> config;
    public UUID uuid;

    public MillstoneConfig(Map<ResourceLocation, ResourceLocation> config, UUID uuid) {
        this.config = config;
        this.uuid = uuid;
    }

    public static StreamCodec<FriendlyByteBuf, MillstoneConfig> STREAM_CODEC = StreamCodec.of(
            (buf, config) -> {
                buf.writeUUID(config.uuid);
                buf.writeMap(config.config, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeResourceLocation);
            },
            (buf) -> {
                UUID uuid = buf.readUUID();
                Map<ResourceLocation, ResourceLocation> config = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readResourceLocation);

                return new MillstoneConfig(config, uuid);
            }
    );

    public void setConfig(ResourceLocation itemId, ResourceLocation recipeId) {
        this.config.put(itemId, recipeId);
    }

    public void removeConfig(ResourceLocation itemId) {
        this.config.remove(itemId);
    }
}
