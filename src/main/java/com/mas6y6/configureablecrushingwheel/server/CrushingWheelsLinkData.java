package com.mas6y6.configureablecrushingwheel.server;

import net.minecraft.core.BlockPos;

public class CrushingWheelsLinkData {
    private final BlockPos[] positions;

    public CrushingWheelsLinkData(BlockPos posA, BlockPos posB) {
        positions = new BlockPos[]{posA, posB};
    }

    public BlockPos[] getPositions() {
        return positions;
    }

    public BlockPos getPos1() {
        return positions[0];
    }

    public BlockPos getPos2() {
        return positions[1];
    }
}
