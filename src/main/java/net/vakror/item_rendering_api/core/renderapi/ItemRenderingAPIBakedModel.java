package net.vakror.item_rendering_api.core.renderapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;
import net.vakror.item_rendering_api.core.api.ItemRenderingAPIQuadRenderData;
import net.vakror.item_rendering_api.core.base.BakedItemModel;

import java.util.*;
import java.util.function.Function;

public class ItemRenderingAPIBakedModel extends BakedItemModel {
	protected final List<AbstractItemRenderingAPILayer> layers;

	private final IItemRenderingAPIModelReader reader;
	private final JsonObject object;

	private final Transformation transform;
	/* Cache the result of quads, using a location combination */
	private static final Map<String, List<BakedQuad>> cache = new HashMap<>();

	public ItemRenderingAPIBakedModel(
			List<AbstractItemRenderingAPILayer> layers,
			IItemRenderingAPIModelReader reader,
			JsonObject object
			, Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite particle
			, ImmutableMap<ItemDisplayContext, ItemTransform> transformMap
			, Transformation transformIn, boolean isSideLit) {
		super(ImmutableList.of(), particle, transformMap, new ItemRenderingAPIOverrideList(spriteGetter, reader.getRequiredItems(object), transformIn), transformIn.isIdentity(), isSideLit);

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
			return ImmutableList.copyOf(ItemRenderingAPIBakedModel.cache.get(cacheKey));

		List<BakedQuad> quads = new ArrayList<>();

		for (AbstractItemRenderingAPILayer layer : layers) {
			layer.render(quads, this.transform, layer.data);
		}

		List<BakedQuad> bakedQuads = new ArrayList<>(quads);
		if (!layers.isEmpty() && layers.get(0).data != null) {
			for (AbstractQuadProcessor quadProcessor : reader.getQuadProcessors(object, layers.get(0).data)) {
				quadProcessor.processQuads(bakedQuads, Collections.unmodifiableList(layers), layers.get(0).data, this.transform, transforms);
			}
		}
		ItemRenderingAPIBakedModel.cache.put(cacheKey, bakedQuads);

		return ImmutableList.copyOf(bakedQuads);
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean applyLeftHandTransform) {
		return super.applyTransform(type, poseStack, applyLeftHandTransform);
	}

	@Override
	public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
		return super.getRenderTypes(itemStack, fabulous);
	}

	/* Give a BakedItemModel base on data in this, can use directly to display */
	public BakedItemModel getNewBakedItemModel(){
		return new BakedItemModel(this.genQuads(), this.particle, this.transforms, this.overrides, this.transform.isIdentity(), this.isSideLit);
	}

	/* Get a combination string of locations, used in cache's key */
	private String getCacheKeyString(){
		List<String> locations = new ArrayList<>();
		for (AbstractItemRenderingAPILayer layer : layers) {
			locations.add(layer.getCacheKey(layer.data));
		}
        return String.join(",", locations);
	}
}