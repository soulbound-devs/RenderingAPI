package net.vakror.item_rendering_api.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;

import java.util.List;

public class EmissivityQuadProcessor extends AbstractQuadProcessor {
    private int emissivity;

    public EmissivityQuadProcessor(int emissivity) {
        this.emissivity = emissivity;
    }

    @Override
    public void processQuads(List<BakedQuad> bakedQuads, List<AbstractItemRenderingAPILayer> layers, ItemRenderingAPIQuadRenderData data, Transformation transform, ImmutableMap<ItemDisplayContext, ItemTransform> itemTransforms) {
        if (emissivity >= 0 && emissivity <= 15) {
            QuadTransformers.settingEmissivity(emissivity).processInPlace(bakedQuads);
        }
    }

    @Override
    public String getCacheKey(ItemRenderingAPIQuadRenderData data) {
        return "EMISSIVITY: " + emissivity;
    }

    public EmissivityQuadProcessor setEmissivity(int emissivity) {
        if (emissivity >= 0 && emissivity <= 15) {
            this.emissivity = emissivity;
        } else {
            throw new IllegalArgumentException("Invalid emissivity, value needs to be between 0 and 16: " + emissivity);
        }
        return this;
    }

    public EmissivityQuadProcessor maxEmissivity() {
        this.emissivity = 15;
        return this;
    }
}
