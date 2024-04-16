package net.vakror.item_rendering_api.core.api;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

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

    List<Class<? extends Item>> getRequiredItems(JsonObject object);
}
