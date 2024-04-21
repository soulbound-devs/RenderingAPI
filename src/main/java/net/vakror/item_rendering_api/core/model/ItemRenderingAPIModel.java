package net.vakror.item_rendering_api.core.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.vakror.item_rendering_api.core.api.data.ExtraModelData;
import net.vakror.item_rendering_api.core.registry.ItemRenderingAPIModelReadersRegistry;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;
import net.vakror.item_rendering_api.core.base.CompositeModelState;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class ItemRenderingAPIModel implements IUnbakedGeometry<ItemRenderingAPIModel> {
	private final JsonObject object;

	public ItemRenderingAPIModel(JsonObject object){
		this.object = object;
	}

	@Override
	public @NotNull BakedModel bake(@NotNull IGeometryBakingContext owner, @NotNull ModelBaker bakery
			, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelTransform
			, @NotNull ItemOverrides overrides, ResourceLocation modelLocation) {

		ResourceLocation item = new ResourceLocation(modelLocation.toString().split("#")[0]);
		IItemRenderingAPIModelReader reader = ItemRenderingAPIModelReadersRegistry.READERS.get(ItemRenderingAPIModelReadersRegistry.ITEMS.get(item));
		List<AbstractItemRenderingAPILayer> layers = reader.getLayers(object, owner, spriteGetter, modelTransform, modelLocation);

		ModelState transformsFromModel = new SimpleModelState(owner.getRootTransform(), modelTransform.isUvLocked());

		modelTransform = new CompositeModelState(transformsFromModel, modelTransform);

		Transformation transform = modelTransform.getRotation();


		ExtraModelData data = reader.getExtraData(object, owner, bakery, spriteGetter, modelTransform, modelLocation, getTransforms(owner), transform);

		/* Vanilla'd BakedItemModel but with custom Override List, used in store data, it'll display nothing */
		return new ItemRenderingAPIBakedModel(layers, reader, object, spriteGetter, data.particle(), data.transformMap(), data.transform(), data.useBlockLight(), data.useAmbientOcclusion(), data.isGui3d());
	}

	public static ImmutableMap<ItemDisplayContext, ItemTransform> getTransforms(IGeometryBakingContext owner)
	{
		EnumMap<ItemDisplayContext, ItemTransform> map = new EnumMap<>(ItemDisplayContext.class);
		for(ItemDisplayContext type : ItemDisplayContext.values())
		{
			ItemTransform tr = owner.getTransforms().getTransform(type);
			map.put(type, tr);
		}
		return ImmutableMap.copyOf(map);
	}
}