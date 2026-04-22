package com.mas6y6.configureablecrushingwheel.server.world;

import com.mas6y6.configureablecrushingwheel.server.CrushingWheelsLinkData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

            BlockPos posA = BlockPos.of(linkTag.getInt("a"));
            BlockPos posB = BlockPos.of(linkTag.getInt("b"));

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
        linkedCrushingWheels.add(new CrushingWheelsLinkData(a, b));
        setDirty();
    }

    public CrushingWheelsLinkData getLink(BlockPos pos) {
        for (var e : linkedCrushingWheels) {
            if (e.getPos1().equals(pos) || e.getPos2().equals(pos)) {
                return e;
            }
        }
        return null;
    }
}
