package com.strubium.astigmatism.client.config;

public class ModConfig {
    public float blockAtlasBlur = 2;
    public float signAtlasBlur = 2;
    public float bannerAtlasBlur = 2;
    public float shieldAtlasBlur = 2;
    public float chestAtlasBlur = 2;
    public float bedAtlasBlur = 2;
    public float shulkerAtlasBlur = 2;
    public float particlesAtlasBlur = 2;
    public float paintingAtlasBlur = 2;
    public float mobeffectsAtlasBlur = 2;


    public static ModConfig createDefault() {
        return new ModConfig();
    }
}

