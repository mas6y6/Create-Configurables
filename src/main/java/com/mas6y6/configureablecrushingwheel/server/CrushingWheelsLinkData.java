package com.mas6y6.configureablecrushingwheel.server;

import net.minecraft.core.BlockPos;

public class CrushingWheelsLinkData {
    private BlockPos[] positions = new BlockPos[0];

    public CrushingWheelsLinkData(BlockPos posA, BlockPos posB) {
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
