package net.vakror.item_rendering_api.extension.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.ItemRenderingAPIQuadRenderData;
import net.vakror.item_rendering_api.core.util.ModelUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TextureOverlayRenderLayer extends AbstractItemRenderingAPILayer {
    private final ResourceLocation texture;

    public TextureOverlayRenderLayer(ResourceLocation overlayTexture) {
        this.texture = overlayTexture;
    }

    @Override
    public void render(List<BakedQuad> quads, Transformation transformation, ItemRenderingAPIQuadRenderData data) {
        List<BakedQuad> copyQuads = ImmutableList.copyOf(quads);
        quads.clear();
        for (BakedQuad quad : copyQuads) {
            //maybe
        }
    }

    @Override
    public @NotNull String getCacheKey() {
        return "";
    }
}
