package com.strubium.astigmatism.client;

import com.strubium.astigmatism.client.config.ModConfigManager;
import net.fabricmc.api.ClientModInitializer;

public class AstigmatismClient implements ClientModInitializer {
    public static final String MOD_ID = "astigmatism";

    @Override
    public void onInitializeClient() {
        ModConfigManager.load();
    }
}
