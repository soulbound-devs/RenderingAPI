package net.vakror.item_rendering_api.impl;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.vakror.item_rendering_api.core.util.ModelUtils;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TextureRenderLayer extends AbstractItemRenderingAPILayer {
    private final ResourceLocation[] textures;
    private boolean blendQuads = false;
    private boolean removeInternalQuads = true;
    private int textureSize = 16;

    public TextureRenderLayer(ResourceLocation... textures) {
        this.textures = textures;
    }

    @Override
    public final void render(List<BakedQuad> quads, Transformation transformation, ItemRenderingAPIQuadRenderData data) {
        List<TextureAtlasSprite> sprites = new ArrayList<>();
        getAdditionalTextures(sprites, data);
        ModelUtils.genQuads(sprites, quads, transformation, blendQuads, data.spriteGetter(), textureSize, removeInternalQuads);
    }

    public void getAdditionalTextures(List<TextureAtlasSprite> sprites, ItemRenderingAPIQuadRenderData data) {
        for (ResourceLocation texture : textures) {
            sprites.add(data.spriteGetter().apply(new Material(InventoryMenu.BLOCK_ATLAS, texture)));
        }
    }

    public final TextureAtlasSprite getSprite(ResourceLocation texture) {
        return data.spriteGetter().apply(new Material(InventoryMenu.BLOCK_ATLAS, texture));
    }

    @Override
    public @NotNull String getCacheKey(ItemRenderingAPIQuadRenderData data) {
        StringBuilder builder = new StringBuilder();
        for (ResourceLocation texture : textures) {
            builder.append(texture.toString());
        }
        List<TextureAtlasSprite> sprites = new ArrayList<>();
        getAdditionalTextures(sprites, data);
        for (TextureAtlasSprite sprite : sprites) {
            builder.append(sprite.contents().name()).append(", ");
        }
        return builder.toString();
    }

    public ResourceLocation[] getTextures() {
        return textures;
    }

    public TextureRenderLayer withBlending(boolean blend) {
        this.blendQuads = blend;
        return this;
    }

    public TextureRenderLayer withBlending() {
        this.blendQuads = true;
        return this;
    }

    public TextureRenderLayer withTextureSize(int textureSize) {
        if (!ModelUtils.isPowerOfTwo(textureSize)) {
            throw new IllegalArgumentException("Texture size must be a power of two!");
        }
        this.textureSize = textureSize;
        return this;
    }

    public TextureRenderLayer showInternalQuads() {
        this.removeInternalQuads = false;
        return this;
    }

    public TextureRenderLayer removeInternalQuads(boolean removeInternalQuads) {
        this.removeInternalQuads = removeInternalQuads;
        return this;
    }
}
