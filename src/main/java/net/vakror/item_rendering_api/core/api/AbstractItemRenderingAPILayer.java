package net.vakror.item_rendering_api.core.api;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.vakror.item_rendering_api.core.renderapi.ItemRenderingAPIBakedModel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The abstract class representing an item layer.
 * An item layer is the class that uses some context (such as the item stack), as well as info provided by a Model Reader to add quads to an item.
 * Extend this if you want to implement custom rendering logic for your items that is not present in any of the default layers.
 */
public abstract class AbstractItemRenderingAPILayer {
    public ItemRenderingAPIQuadRenderData data;
    public ItemRenderingAPIBakedModel model;

    /**
     * The function responsible for rendering quads.
     * @param quads a mutable list containing all current quads. If new quads need to be added, this list has to be modified. Existing quads should not be edited here.
     * @param transformation the transformation of this model (generally not needed)
     * @param data extra data, such as the item stack. Very useful for rendering based off nbt data.
     */
    public abstract void render(ImmutableList.Builder<BakedQuad> quads, Transformation transformation, ItemRenderingAPIQuadRenderData data);

    /**
     * gets the key used for caching models.
     * Returning a unique string per model (such as texture strings) is supremely important, as otherwise,
     * models may not render properly because they retrieve the cached version.
     * @return the cache key for this layer
     */
    @NotNull
    public abstract String getCacheKey();

    /**
     * internal method to set up extra data
     * @param data the data to set
     */
    @ApiStatus.Internal
    public void setData(ItemRenderingAPIQuadRenderData data) {
        this.data = data;
    }

    /**
     * internal method to set up the model
     * @param model the model
     */
    @ApiStatus.Internal
    public void setModel(ItemRenderingAPIBakedModel model) {
        this.model = model;
    }
}
