package net.vakror.item_rendering_api.core.api.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;

public record ExtraModelData(TextureAtlasSprite particle, ImmutableMap<ItemDisplayContext, ItemTransform> transformMap, Transformation transform, boolean useBlockLight, boolean useAmbientOcclusion, boolean isGui3d) {
}
