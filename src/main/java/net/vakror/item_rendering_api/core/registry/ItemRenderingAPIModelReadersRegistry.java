package net.vakror.item_rendering_api.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderingAPIModelReadersRegistry {
    public static final Map<ResourceLocation, IItemRenderingAPIModelReader> READERS = new HashMap<>();

    public static final Map<ResourceLocation, ResourceLocation> ITEMS = new HashMap<>();

    public static void register(ResourceLocation name, IItemRenderingAPIModelReader reader) {
        READERS.put(name, reader);
    }

    public static void addItem(ResourceLocation item, ResourceLocation reader) {
        ITEMS.put(item, reader);
    }
}
