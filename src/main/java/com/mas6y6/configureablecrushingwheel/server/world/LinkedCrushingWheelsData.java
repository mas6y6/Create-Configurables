package com.mas6y6.configureablecrushingwheel.server.world;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mas6y6.configureablecrushingwheel.server.CrushingWheelsLinkData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LinkedCrushingWheelsData extends SavedData {
    private final List<CrushingWheelsLinkData> linkedCrushingWheels = new ArrayList<>();

    public static LinkedCrushingWheelsData create() {
        return new LinkedCrushingWheelsData();
    }

    public static LinkedCrushingWheelsData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        LinkedCrushingWheelsData data = LinkedCrushingWheelsData.create();

        ListTag listTag = tag.getList("links", Tag.TAG_COMPOUND);

        for (Tag t : listTag) {
            CompoundTag linkTag = (CompoundTag) t;

            BlockPos posA = BlockPos.of(linkTag.getLong("a"));
            BlockPos posB = BlockPos.of(linkTag.getLong("b"));

            data.linkedCrushingWheels.add(new CrushingWheelsLinkData(posA, posB));
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag list = new ListTag();

        for (var e : linkedCrushingWheels) {
            CompoundTag entry = new CompoundTag();

            entry.putLong("a", e.getPos1().asLong());
            entry.putLong("b", e.getPos2().asLong());

            list.add(entry);
        }

        tag.put("links", list);

        return tag;
    }

    public void link(BlockPos a, BlockPos b) {
        if (getLink(a) != null) unlink(a);
        if (getLink(b) != null) unlink(b);
        linkedCrushingWheels.add(new CrushingWheelsLinkData(a, b));
        setDirty();
    }

    public void unlink(BlockPos pos) {
        boolean removed = linkedCrushingWheels.removeIf(link ->
                link.getPos1().equals(pos) || link.getPos2().equals(pos)
        );

        if (removed) {
            setDirty();
            Configureablecrushingwheel.LOGGER.debug("Unlinked {} and marked data dirty", pos);
        }
    }

    public CrushingWheelsLinkData getLink(BlockPos pos) {
        for (var e : linkedCrushingWheels) {
            if (e.getPos1().equals(pos) || e.getPos2().equals(pos)) {
                return e;
            }
        }
        return null;
    }

    public static LinkedCrushingWheelsData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();

        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(LinkedCrushingWheelsData::create, LinkedCrushingWheelsData::load),
                "linked_crushing_wheels"
        );
    }
}
