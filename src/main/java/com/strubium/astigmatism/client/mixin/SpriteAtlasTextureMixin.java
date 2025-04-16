package com.strubium.astigmatism.client.mixin;

import com.strubium.astigmatism.client.config.ModConfig;
import com.strubium.astigmatism.client.config.ModConfigManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// Mixin class for SpriteAtlasTexture
@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {

    // Inject after sprites are stitched and before being uploaded to GPU
    @Inject(method = "upload", at = @At("HEAD"))
    private void onUpload(SpriteAtlasTexture.Data data, CallbackInfo ci) {
        ModConfig config = ModConfigManager.config;

        // Access the list of sprites
        List<Sprite> sprites = ((SpriteAtlasTextureDataAccessor) data).getSprites();

        SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture)(Object)this;


        // Apply the blur effect to each sprite
        for (Sprite sprite : sprites) {
            // Get the images associated with the sprite
            NativeImage[] images = ((SpriteAccessor) sprite).getImages();

            // Loop through each image and apply the blur effect
            for (NativeImage image : images) {
                if (image != null) {
                    if(spriteAtlasTexture.getId().getPath().contains("blocks")){
                        applyWeightedBoxBlur(image, config.blockAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("signs")){
                        applyWeightedBoxBlur(image, config.signAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("banner")){
                        applyWeightedBoxBlur(image, config.bannerAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("shield")){
                        applyWeightedBoxBlur(image, config.shieldAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("chest")){
                        applyWeightedBoxBlur(image, config.chestAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("beds")){
                        applyWeightedBoxBlur(image, config.bedAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("shulker_boxes")){
                        applyWeightedBoxBlur(image, config.shulkerAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("particles")){
                        applyWeightedBoxBlur(image, config.particlesAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("paintings")){
                        applyWeightedBoxBlur(image, config.paintingAtlasBlur); // Apply box blur to the image
                    }
                    if(spriteAtlasTexture.getId().getPath().contains("mob_effects")){
                        applyWeightedBoxBlur(image, config.mobeffectsAtlasBlur); // Apply box blur to the image
                    }
                }
            }
        }
    }

    // Method to apply a simple box blur to an image
    private void applyWeightedBoxBlur(NativeImage image, float radius) {
        int width = image.getWidth();
        int height = image.getHeight();
        int intRadius = (int)Math.ceil(radius);

        if (radius > 0) {

            NativeImage copy = new NativeImage(width, height, false);

            float radiusSq = radius * radius;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    float r = 0, g = 0, b = 0, a = 0;
                    float weightSum = 0;

                    for (int dx = -intRadius; dx <= intRadius; dx++) {
                        for (int dy = -intRadius; dy <= intRadius; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;

                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                float distSq = dx * dx + dy * dy;
                                if (distSq > radiusSq) continue;

                                float weight = (float)Math.exp(-distSq / (2 * radiusSq)); // Gaussian falloff

                                int color = image.getColor(nx, ny);
                                a += NativeImage.getAlpha(color) * weight;
                                r += NativeImage.getRed(color) * weight;
                                g += NativeImage.getGreen(color) * weight;
                                b += NativeImage.getBlue(color) * weight;

                                weightSum += weight;
                            }
                        }
                    }

                    if (weightSum == 0) continue;

                    int avgA = Math.min(255, Math.max(0, Math.round(a / weightSum)));
                    int avgR = Math.min(255, Math.max(0, Math.round(r / weightSum)));
                    int avgG = Math.min(255, Math.max(0, Math.round(g / weightSum)));
                    int avgB = Math.min(255, Math.max(0, Math.round(b / weightSum)));

                    int avgColor = (avgA << 24) | (avgB << 16) | (avgG << 8) | avgR;
                    copy.setColor(x, y, avgColor);
                }
            }

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setColor(x, y, copy.getColor(x, y));
                }
            }

            copy.close();
        }
    }
}
