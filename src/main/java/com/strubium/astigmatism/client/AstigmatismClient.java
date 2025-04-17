package com.strubium.astigmatism.client;

import com.strubium.astigmatism.client.config.ModConfigManager;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstigmatismClient implements ClientModInitializer {
    public static final String MOD_ID = "astigmatism";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ModConfigManager.load();
    }
}
