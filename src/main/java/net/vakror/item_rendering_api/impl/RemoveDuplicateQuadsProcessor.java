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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RemoveDuplicateQuadsProcessor extends AbstractQuadProcessor {
    @Override
    public void processQuads(List<BakedQuad> bakedQuads, List<AbstractItemRenderingAPILayer> layers, ItemRenderingAPIQuadRenderData data, Transformation transform, ImmutableMap<ItemDisplayContext, ItemTransform> itemTransforms) {
        List<BakedQuad> copy = new ArrayList<>(bakedQuads);
        bakedQuads.clear();

        List<Vector3f[]> positions = new ArrayList<>();

        for (BakedQuad quad : copy) {
            if (!positions.contains(getPos(quad.getVertices()))) {
                bakedQuads.add(quad);
                positions.add(getPos(quad.getVertices()));
            }
        }
    }
    public Vector3f[] getPos(int[] vertices) {
        Vector3f[] positions = new Vector3f[4];
        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            positions[i] = new Vector3f(vertices[offset], vertices[offset + 1], vertices[offset + 2]);
        }
        return positions;
    }
}
