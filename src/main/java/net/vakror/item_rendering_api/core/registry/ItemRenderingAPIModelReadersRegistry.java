package net.vakror.item_rendering_api.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry class for models
 */
public class ItemRenderingAPIModelReadersRegistry {
    /**
     * maps a reader's name to the reader
     */
    public static final Map<ResourceLocation, IItemRenderingAPIModelReader> READERS = new HashMap<>();

    /**
     * maps an item name to a reader name
     */
    public static final Map<ResourceLocation, ResourceLocation> ITEMS = new HashMap<>();

    /**
     * registers a reader
     * @param name the name of the reader
     * @param reader the reader
     */
    public static void register(ResourceLocation name, IItemRenderingAPIModelReader reader) {
        READERS.put(name, reader);
    }

    /**
     * registers an item for a reader
     * @param item the item
     * @param reader the reader
     */
    public static void addItem(ResourceLocation item, ResourceLocation reader) {
        ITEMS.put(item, reader);
    }
}
