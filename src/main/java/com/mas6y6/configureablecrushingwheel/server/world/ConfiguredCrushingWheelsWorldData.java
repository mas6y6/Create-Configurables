package com.mas6y6.configureablecrushingwheel.server.world;

import com.mas6y6.configureablecrushingwheel.common.CrushingWheelsConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfiguredCrushingWheelsWorldData extends SavedData {
    private final Map<UUID, CrushingWheelsConfig> configuredCrushingWheels = new HashMap<>();

    public static ConfiguredCrushingWheelsWorldData create() {
        return new ConfiguredCrushingWheelsWorldData();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        CompoundTag master = new CompoundTag();

        for (Map.Entry<UUID, CrushingWheelsConfig> entry : configuredCrushingWheels.entrySet()) {
            CompoundTag configTag = new CompoundTag();

            entry.getValue().config.forEach((key, value) -> configTag.putString(key.toString(), value.toString()));

            master.put(entry.getKey().toString(),configTag);
        }

        compoundTag.put("configured_crushing_wheels",master);
        return compoundTag;
    }

    public static ConfiguredCrushingWheelsWorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        ConfiguredCrushingWheelsWorldData data = new ConfiguredCrushingWheelsWorldData();

        CompoundTag master = tag.getCompound("configured_crushing_wheels");

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

            data.configuredCrushingWheels.put(uuid, new CrushingWheelsConfig(map, uuid));
        }

        return data;
    }

    public void put(UUID uuid, CrushingWheelsConfig config) {
        configuredCrushingWheels.put(uuid, config);
        setDirty();
    }

    public void remove(UUID uuid) {
        configuredCrushingWheels.remove(uuid);
        setDirty();
    }

    public CrushingWheelsConfig get(UUID uuid) {
        CrushingWheelsConfig thing = configuredCrushingWheels.get(uuid);

        if (thing == null) {
            return new CrushingWheelsConfig(new HashMap<>(), uuid);
        }

        return thing;
    }

    public static ConfiguredCrushingWheelsWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(ConfiguredCrushingWheelsWorldData::create, ConfiguredCrushingWheelsWorldData::load), "configured_crushing_wheels");
    }
}
