package net.vakror.item_rendering_api.core.api;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.vakror.item_rendering_api.core.api.data.ExtraModelData;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The interface representing a Model Reader.
 * This is the class that is responsible for reading models from JSON files. It is what provides quad processors, as well as layers and required item classes.
 */
public interface IItemRenderingAPIModelReader {
    /**
     * This method gets a list of {@link AbstractItemRenderingAPILayer}
     * @param object the {@link JsonObject} representing the model JSON file
     * @param owner the context used for baking a model, it is generally not useful
     * @param spriteGetter used to get sprites from {@link ResourceLocation ResourceLocations}. Very useful for passing sprites directly to the layer.
     * @param modelTransform the transform of the model
     * @param modelLocation where the model is located.
     * @return a list of {@link AbstractItemRenderingAPILayer AbstractItemRenderingAPILayers} for this model
     */
    default List<AbstractItemRenderingAPILayer> getLayers(JsonObject object, IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        return getLayers(object);
    }

    /**
     * A simpler version of {@link #getLayers(JsonObject, IGeometryBakingContext, Function, ModelState, ResourceLocation) getLayers} with only the model {@link JsonObject} as a parameter
     * @param object the json object of the model
     * @return a list of {@link AbstractItemRenderingAPILayer AbstractItemRenderingAPILayers} for this model
     */
    List<AbstractItemRenderingAPILayer> getLayers(JsonObject object);

    /**
     * This method is used to retrieve quad processors
     * @param object the json object of the
     * @param data extra data used to retrieve quad processors
     * @return a list of {@link AbstractQuadProcessor QuadProcessors}
     */
    default List<AbstractQuadProcessor> getQuadProcessors(JsonObject object, ItemRenderingAPIQuadRenderData data) {
        return new ArrayList<>();
    }

    /**
     * Returns a list of item classes. If not null, any item rendered with this reader has to be a subclass of this item class.
     * If an item attempts to render with this reader, but it is not a subclass of one of these items, it will not render.
     * If null, subclass will always pass.
     * @param object the json object representing this model
     * @return a list of item classes
     */
    @Nullable
    default List<Class<? extends Item>> getRequiredItems(JsonObject object) {
        return null;
    }

    /**
     * @param object the {@link JsonObject} representing this model
     * @param owner the {@link IGeometryBakingContext} for this model
     * @param bakery {@link ModelBaker} the bakery
     * @param spriteGetter used to get {@link TextureAtlasSprite TextureAtlasSprites} using a {@link Material}
     * @param modelTransform the {@link ModelState}
     * @param modelLocation the {@link ResourceLocation} of this model (where it is located)
     * @param oldTransforms a map of transforms
     * @param transform the base transform
     * @return extra data used to render the model
     */
    default ExtraModelData getExtraData(JsonObject object, IGeometryBakingContext owner, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation, ImmutableMap<ItemDisplayContext, ItemTransform> oldTransforms, Transformation transform) {
        TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
        return new ExtraModelData(particle, oldTransforms, transform, false, true, false);
    }

    default boolean shouldCache(JsonObject object, IGeometryBakingContext owner) {
        return true;
    }
}
