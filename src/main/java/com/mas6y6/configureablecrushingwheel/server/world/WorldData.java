package com.mas6y6.configureablecrushingwheel.server.world;

import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import com.mas6y6.configureablecrushingwheel.common.MillstoneConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldData extends SavedData {
    private static final String CRUSHING_WHEELS_TAG = "configured_crushing_wheels";
    private static final String MILLSTONES_TAG = "configured_millstones";

    private final Map<UUID, CrushingWheelsConfig> configuredCrushingWheels = new HashMap<>();
    private final Map<UUID, MillstoneConfig> configuredMillstones = new HashMap<>();

    public static WorldData create() {
        return new WorldData();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        compoundTag.put(CRUSHING_WHEELS_TAG, saveConfigMap(configuredCrushingWheels));
        compoundTag.put(MILLSTONES_TAG, saveConfigMap(configuredMillstones));
        return compoundTag;
    }

    public static WorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        WorldData data = new WorldData();
        loadConfigMap(tag.getCompound(CRUSHING_WHEELS_TAG), data.configuredCrushingWheels, CrushingWheelsConfig::new);
        loadConfigMap(tag.getCompound(MILLSTONES_TAG), data.configuredMillstones, MillstoneConfig::new);

        return data;
    }

    public void putCrushingWheel(UUID uuid, CrushingWheelsConfig config) {
        configuredCrushingWheels.put(uuid, config);
        setDirty();
    }

    public void removeCrushingWheel(UUID uuid) {
        configuredCrushingWheels.remove(uuid);
        setDirty();
    }

    public CrushingWheelsConfig getCrushingWheel(UUID uuid) {
        return getConfig(configuredCrushingWheels, uuid, CrushingWheelsConfig::new);
    }

    public void putMillstone(UUID uuid, MillstoneConfig config) {
        configuredMillstones.put(uuid, config);
        setDirty();
    }

    public void removeMillstone(UUID uuid) {
        configuredMillstones.remove(uuid);
        setDirty();
    }

    public MillstoneConfig getMillstone(UUID uuid) {
        return getConfig(configuredMillstones, uuid, MillstoneConfig::new);
    }

    public static WorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(WorldData::create, WorldData::load), "configured_crushing_wheels");
    }

    private static <T> CompoundTag saveConfigMap(Map<UUID, T> configuredEntries) {
        CompoundTag master = new CompoundTag();

        for (Map.Entry<UUID, T> entry : configuredEntries.entrySet()) {
            CompoundTag configTag = new CompoundTag();
            Map<ResourceLocation, ResourceLocation> config = extractConfig(entry.getValue());

            config.forEach((key, value) -> configTag.putString(key.toString(), value.toString()));
            master.put(entry.getKey().toString(), configTag);
        }

        return master;
    }

    private static <T> void loadConfigMap(CompoundTag master,
                                          Map<UUID, T> destination,
                                          ConfigFactory<T> factory) {
        for (String key : master.getAllKeys()) {
            UUID uuid = UUID.fromString(key);
            CompoundTag configTag = master.getCompound(key);
            Map<ResourceLocation, ResourceLocation> map = new HashMap<>();

            for (String resourceKey : configTag.getAllKeys()) {
                ResourceLocation k = ResourceLocation.tryParse(resourceKey);
                ResourceLocation v = ResourceLocation.tryParse(configTag.getString(resourceKey));

                if (k != null && v != null) {
                    map.put(k, v);
                }
            }

            destination.put(uuid, factory.create(map, uuid));
        }
    }

    private static <T> T getConfig(Map<UUID, T> source, UUID uuid, ConfigFactory<T> factory) {
        T thing = source.get(uuid);

        if (thing == null) {
            return factory.create(new HashMap<>(), uuid);
        }

        return thing;
    }

    private static Map<ResourceLocation, ResourceLocation> extractConfig(Object config) {
        if (config instanceof CrushingWheelsConfig crushingWheelsConfig) {
            return crushingWheelsConfig.config;
        }

        if (config instanceof MillstoneConfig millstoneConfig) {
            return millstoneConfig.config;
        }

        throw new IllegalArgumentException("Unsupported config type: " + config.getClass().getName());
    }

    @FunctionalInterface
    private interface ConfigFactory<T> {
        T create(Map<ResourceLocation, ResourceLocation> config, UUID uuid);
    }
}
