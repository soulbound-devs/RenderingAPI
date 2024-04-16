package net.vakror.item_rendering_api.extension.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.ItemRenderingAPIQuadRenderData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveDuplicateQuadsProcessor extends AbstractQuadProcessor {
    @Override
    public void processQuads(List<BakedQuad> bakedQuads, List<AbstractItemRenderingAPILayer> layers, ItemRenderingAPIQuadRenderData data) {
        List<BakedQuad> copy = new ArrayList<>(bakedQuads);
        bakedQuads.clear();

        Multimap<int[], Direction> positions = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

        for (BakedQuad quad : copy) {
            if (!positions.containsEntry(quad.getVertices(), quad.getDirection())) {
                bakedQuads.add(quad);
                positions.put(quad.getVertices(), quad.getDirection());
            }
        }
    }
}
