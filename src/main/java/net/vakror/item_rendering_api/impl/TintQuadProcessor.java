package net.vakror.item_rendering_api.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;
import net.vakror.item_rendering_api.core.util.ModelUtils;
import org.joml.Vector4i;

import java.util.List;

import static net.neoforged.neoforge.client.model.QuadTransformers.toABGR;

public class TintQuadProcessor extends AbstractQuadProcessor {
    private final boolean blendWithExistingTint;
    private final Vector4i tint;
    private final double ratio;

    public TintQuadProcessor(boolean blendWithExistingTint, Vector4i tint, double ratio) {
        this.blendWithExistingTint = blendWithExistingTint;
        this.tint = tint;
        this.ratio = ratio;
    }

    @Override
    public void processQuads(List<BakedQuad> bakedQuads, List<AbstractItemRenderingAPILayer> layers, ItemRenderingAPIQuadRenderData data, Transformation transform, ImmutableMap<ItemDisplayContext, ItemTransform> itemTransforms) {
        for (BakedQuad quad : bakedQuads) {
            var vertices = quad.getVertices();
            Vector4i newTint;
            for (int i = 0; i < 4; i++) {

                if (blendWithExistingTint) {
                    int oldTint = vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR];
                    Vector4i oldTintVector = new Vector4i((oldTint >> 16) & 0xff , (oldTint >> 8) & 0xff, (oldTint) & 0xff, (oldTint >> 24) & 0xff);


                    newTint = ModelUtils.colorBlend(tint.x, tint.y, tint.z, tint.w, oldTintVector.x, oldTintVector.y, oldTintVector.z, oldTintVector.y, ratio);
                } else {
                    newTint = tint;
                }

                int color = toABGR(newTint.w << 24 | newTint.x << 16 | newTint.y << 8 | newTint.z);
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = color;
            }
        }
    }

    @Override
    public String getCacheKey(ItemRenderingAPIQuadRenderData data) {
        return "BLEND: " + blendWithExistingTint + ", TINT:" + tint + ", RATIO:" + ratio;
    }

}