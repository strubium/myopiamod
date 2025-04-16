package com.strubium.astigmatism.client.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SpriteAtlasTexture.Data.class)
public interface SpriteAtlasTextureDataAccessor {
    @Accessor("sprites")
    List<Sprite> getSprites();
}

