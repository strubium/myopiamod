package com.strubium.astigmatism.client.mixin;

import com.mojang.blaze3d.platform.TextureUtil;
import com.strubium.astigmatism.client.config.ModConfigManager;
import com.strubium.astigmatism.client.BlurUtils;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.Optional;

@Mixin(ResourceTexture.class)
public abstract class ResourceTextureMixin extends AbstractTexture {

    @Shadow
    private Identifier location;

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void onLoad(ResourceManager resourceManager, CallbackInfo ci) {
        try {
            if (!location.getPath().contains("entity")) return;

            Optional<Resource> optional = resourceManager.getResource(location);
            if (optional.isEmpty()) return;

            Resource resource = optional.get();

            try (InputStream stream = resource.getInputStream()) {
                NativeImage image = NativeImage.read(NativeImage.Format.RGBA, stream);
                if (image != null) {
                    // Apply blur
                    BlurUtils.applyWeightedBoxBlur(image, ModConfigManager.config.entityTextureBlur);

                    // Generate and bind a new OpenGL texture ID
                    int glId = this.getGlId();
                    TextureUtil.prepareImage(glId, image.getWidth(), image.getHeight());
                    image.upload(0, 0, 0, false);

                    ci.cancel(); // Skip original method
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
