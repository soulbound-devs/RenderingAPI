package net.vakror.item_rendering_api.core.api;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.List;

/**
 * The abstract class representing a quad processor.
 * Quad processors are fired after all layers have finished their modifications to quads.
 * Generally, you wouldn't want to add new quads here, only modify existing quads.
 * Quad Processors are called in the order they are returned in by {@link IItemRenderingAPIModelReader#getQuadProcessors(JsonObject, ItemRenderingAPIQuadRenderData) IItemRenderingAPIModelReader#getQuadProcessors}
 */
public abstract class AbstractQuadProcessor {
    /**
     * This is the method where all quad post-processing should happen.
     * @param bakedQuads the list (mutable) of all baked quads (so far). If you want to modify existing quads, modify this.
     * @param layers an immutable list of all layers
     * @param data extra data to process the quads with
     */
    public abstract void processQuads(List<BakedQuad> bakedQuads, List<AbstractItemRenderingAPILayer> layers, ItemRenderingAPIQuadRenderData data, Transformation transform, ImmutableMap<ItemDisplayContext, ItemTransform> itemTransforms);
}
