package net.vakror.item_rendering_api.core.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;
import net.vakror.item_rendering_api.core.base.BakedItemModel;

import java.util.*;
import java.util.function.Function;

public class ItemRenderingAPIBakedModel extends BakedItemModel {
	protected final List<AbstractItemRenderingAPILayer> layers;

	private final IItemRenderingAPIModelReader reader;
	private final List<AbstractQuadProcessor> processors;
	private final JsonObject object;
	private final boolean shouldCache;

	private final Transformation transform;
	/* Cache the result of quads, using a location combination */
	private static final Map<String, List<BakedQuad>> cache = new HashMap<>();

	public ItemRenderingAPIBakedModel(
			List<AbstractItemRenderingAPILayer> layers,
			IItemRenderingAPIModelReader reader, JsonObject object,
			Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite particle,
			ImmutableMap<ItemDisplayContext, ItemTransform> transformMap,
			Transformation transformIn, boolean isSideLit,
			boolean useAmbientOcclusion, boolean isGui3d,
			boolean shouldCache) {
		super(ImmutableList.of(), particle, transformMap, new ItemRenderingAPIOverrideList(spriteGetter, reader.getRequiredItems(object)), transformIn.isIdentity(), isSideLit, useAmbientOcclusion, isGui3d);

		this.layers = layers;
		this.reader = reader;
		this.processors = new ArrayList<>();
		this.object = object;

		this.transform = transformIn;
		this.shouldCache = shouldCache;

	}

	/**
	 *
	 * When I wrote this god and me knew what it does
	 * Now, only god knows
	 *
	 * @return the quads
	 */
	private ImmutableList<BakedQuad> genQuads() {
		if (!layers.isEmpty() && layers.get(0).data != null) {
			if (processors.isEmpty()) {
				processors.addAll(reader.getQuadProcessors(object, layers.get(0).data));
			}
		}

		String cacheKey = this.getCacheKeyString();
		if (shouldCache) {

			/* Check if this sprite location combination is already baked or not  */
			if (ItemRenderingAPIBakedModel.cache.containsKey(cacheKey))
				return ImmutableList.copyOf(ItemRenderingAPIBakedModel.cache.get(cacheKey));
		}

		List<BakedQuad> quads = new ArrayList<>();

		for (AbstractItemRenderingAPILayer layer : layers) {
			layer.render(quads, this.transform, layer.data);
		}

		List<BakedQuad> bakedQuads = new ArrayList<>(quads);

		for (AbstractQuadProcessor quadProcessor : processors) {
			quadProcessor.processQuads(bakedQuads, Collections.unmodifiableList(layers), layers.get(0).data, this.transform, transforms);
		}

		if (shouldCache) {
			ItemRenderingAPIBakedModel.cache.put(cacheKey, bakedQuads);
		}

		return ImmutableList.copyOf(bakedQuads);
	}

	/* Give a BakedItemModel base on data in this, can use directly to display */
	public BakedItemModel getNewBakedItemModel(){
		return new BakedItemModel(this.genQuads(), this.particle, this.transforms, this.overrides, this.transform.isIdentity(), this.isSideLit, this.useAmbientOcclusion, this.isGui3d);
	}

	/* Get a combination string of locations, used in cache's key */
	private String getCacheKeyString(){
		List<String> locations = new ArrayList<>();
		for (AbstractItemRenderingAPILayer layer : layers) {
			locations.add(layer.getCacheKey(layer.data) + ", ");
		}
		for (AbstractQuadProcessor processor : processors) {
			if (!layers.isEmpty() && layers.get(0).data != null) {
				locations.add(processor.getCacheKey(layers.get(0).data) + ", ");
			}
		}
        return String.join(",", locations);
	}
}