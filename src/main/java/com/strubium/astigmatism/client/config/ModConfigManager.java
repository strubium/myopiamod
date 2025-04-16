package com.strubium.astigmatism.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strubium.astigmatism.client.AstigmatismClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigManager {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(AstigmatismClient.MOD_ID + ".json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ModConfig config;

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    config = GSON.fromJson(reader, ModConfig.class);
                }
            } else {
                config = ModConfig.createDefault();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
            config = ModConfig.createDefault();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

