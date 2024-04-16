package net.vakror.item_rendering_api.core.renderapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
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
	private final JsonObject object;

	private final Transformation transform;
	/* Cache the result of quads, using a location combination */
	private static final Map<String, ImmutableList<BakedQuad>> cache = new HashMap<>();

	public ItemRenderingAPIBakedModel(
			List<AbstractItemRenderingAPILayer> layers,
			IItemRenderingAPIModelReader reader,
			JsonObject object
			, Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite particle
			, ImmutableMap<ItemDisplayContext, ItemTransform> transformMap
			, Transformation transformIn, boolean isSideLit) {
		super(ImmutableList.of(), particle, transformMap, new ItemRenderingAPIOverrideList(spriteGetter, reader.getRequiredItems(object)), transformIn.isIdentity(), isSideLit);

		this.layers = layers;
		this.reader = reader;
		this.object = object;

		this.transform = transformIn;

	}

	/**
	 *
	 * When I wrote this god and me knew what it does
	 * Now, only god knows
	 *
	 * @return the quads
	 */
	private ImmutableList<BakedQuad> genQuads() {
		String cacheKey = this.getCacheKeyString();

		/* Check if this sprite location combination is already baked or not  */
		if (ItemRenderingAPIBakedModel.cache.containsKey(cacheKey))
			return ItemRenderingAPIBakedModel.cache.get(cacheKey);

		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		for (AbstractItemRenderingAPILayer layer : layers) {
			layer.render(quads, this.transform, layer.data);
		}

        ImmutableList<BakedQuad> returnQuads = quads.build();
		List<BakedQuad> bakedQuads = new ArrayList<>(quads.build());
		if (!layers.isEmpty() && layers.get(0).data != null) {
			for (AbstractQuadProcessor quadProcessor : reader.getQuadProcessors(object, layers.get(0).data)) {
				quadProcessor.processQuads(bakedQuads, Collections.unmodifiableList(layers), layers.get(0).data);
			}
		}
		ItemRenderingAPIBakedModel.cache.put(cacheKey, returnQuads);

		return returnQuads;
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean applyLeftHandTransform) {
		return super.applyTransform(type, poseStack, applyLeftHandTransform);
	}

	/* Give a BakedItemModel base on data in this, can use directly to display */
	public BakedItemModel getNewBakedItemModel(){
		return new BakedItemModel(this.genQuads(), this.particle, this.transforms, this.overrides, this.transform.isIdentity(), this.isSideLit);
	}

	/* Get a combination string of locations, used in cache's key */
	private String getCacheKeyString(){
		List<String> locations = new ArrayList<>();
		for (AbstractItemRenderingAPILayer layer : layers) {
			locations.add(layer.getCacheKey());
		}
        return String.join(",", locations);
	}
}