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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.strubium.astigmatism.client.BlurUtils.applyWeightedBoxBlur;

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

        int atlasWidth = ((SpriteAtlasTextureDataAccessor) data).getWidth();
        int atlasHeight = ((SpriteAtlasTextureDataAccessor) data).getHeight();

        NativeImage stitchedImage = null;
        if (config.exportAtlases) {
            stitchedImage = new NativeImage(NativeImage.Format.RGBA, atlasWidth, atlasHeight, false);
        }

        for (Sprite sprite : sprites) {
            NativeImage[] images = ((SpriteAccessor) sprite).getImages();
            if (images == null || images.length == 0) continue;

            for (NativeImage image : images) {
                if (image == null) continue;

                String path = spriteAtlasTexture.getId().getPath();

                // Apply blur based on path
                if (path.contains("blocks")) applyWeightedBoxBlur(image, config.blockAtlasBlur);
                if (path.contains("signs")) applyWeightedBoxBlur(image, config.signAtlasBlur);
                if (path.contains("banner")) applyWeightedBoxBlur(image, config.bannerAtlasBlur);
                if (path.contains("shield")) applyWeightedBoxBlur(image, config.shieldAtlasBlur);
                if (path.contains("chest")) applyWeightedBoxBlur(image, config.chestAtlasBlur);
                if (path.contains("beds")) applyWeightedBoxBlur(image, config.bedAtlasBlur);
                if (path.contains("shulker_boxes")) applyWeightedBoxBlur(image, config.shulkerAtlasBlur);
                if (path.contains("particles")) applyWeightedBoxBlur(image, config.particlesAtlasBlur);
                if (path.contains("paintings")) applyWeightedBoxBlur(image, config.paintingAtlasBlur);
                if (path.contains("mob_effects")) applyWeightedBoxBlur(image, config.mobeffectsAtlasBlur);

                // Export logic
                if (config.exportAtlases && stitchedImage != null) {
                    int spriteX = sprite.getX();
                    int spriteY = sprite.getY();
                    int spriteWidth = sprite.getWidth();
                    int spriteHeight = sprite.getHeight();
                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();

                    // Clamp to avoid out-of-bounds
                    int exportWidth = Math.min(spriteWidth, imageWidth);
                    int exportHeight = Math.min(spriteHeight, imageHeight);

                    for (int x = 0; x < exportWidth; x++) {
                        for (int y = 0; y < exportHeight; y++) {
                            int color = image.getColor(x, y);
                            stitchedImage.setColor(spriteX + x, spriteY + y, color);
                        }
                    }
                }
            }
        }

        // Save the atlas image to disk
        if (config.exportAtlases && stitchedImage != null) {
            String filename = spriteAtlasTexture.getId().getNamespace() + "_" + spriteAtlasTexture.getId().getPath().replace('/', '_') + ".png";
            File outputFile = new File("exported_atlases", filename);
            outputFile.getParentFile().mkdirs();

            try {
                stitchedImage.writeTo(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stitchedImage.close();
            }
        }
    }
}
