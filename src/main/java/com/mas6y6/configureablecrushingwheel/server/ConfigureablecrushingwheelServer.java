package com.mas6y6.configureablecrushingwheel.server;

import com.mas6y6.configureablecrushingwheel.Configureablecrushingwheel;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(value = Configureablecrushingwheel.MODID, dist = Dist.DEDICATED_SERVER)
public class ConfigureablecrushingwheelServer {
    public static final String MODID = "configureablecrushingwheel";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ConfigureablecrushingwheelServer instance;

    public ConfigureablecrushingwheelServer(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
    }

    public ConfigureablecrushingwheelServer getInstance() {
        return instance;
    }
}
