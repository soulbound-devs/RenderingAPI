package net.vakror.item_rendering_api.extension.context;

import com.google.common.base.Stopwatch;
import net.minecraft.resources.ResourceLocation;
import net.vakror.item_rendering_api.ItemRenderingAPI;
import net.vakror.item_rendering_api.core.registry.ItemRenderingAPIModelReadersRegistry;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;
import net.vakror.registry.jamesregistryapi.api.context.IRegistrationContext;

public class ModelReaderRegistrationContext implements IRegistrationContext {
    /**
     * Registers a model reader
     *
     * @param name the name of the reader
     * @param reader the reader to register
     */
    public void registerModelReader(ResourceLocation name, IItemRenderingAPIModelReader reader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ItemRenderingAPIModelReadersRegistry.register(name, reader);
        ItemRenderingAPI.LOGGER.info("Registered Model Reader {}, \033[0;31mTook {}\033[0;0m", name, stopwatch);
    }

    public void addItemForReader(ResourceLocation item, ResourceLocation reader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ItemRenderingAPIModelReadersRegistry.addItem(item, reader);
        ItemRenderingAPI.LOGGER.info("Registered Item {} for Model Reader {}, \033[0;31mTook {}\033[0;0m", item, reader, stopwatch);
    }

    public void removeItemForReader(ResourceLocation reader, ResourceLocation item) {
        if (!ItemRenderingAPIModelReadersRegistry.ITEMS.containsKey(item)) {
            ItemRenderingAPI.LOGGER.error("Attempted To Unregister Item {} From Unpaired Model Reader {}", item, reader);
        } else {
            ItemRenderingAPI.LOGGER.info("Starting Unregistration For Item {} From Model Reader {}", item, reader);
            Stopwatch stopwatch = Stopwatch.createStarted();
            ItemRenderingAPIModelReadersRegistry.ITEMS.remove(item, reader);
            ItemRenderingAPI.LOGGER.info("Finished Unregistration For Item {} From Model Reader {}, \033[0;31mTook {}\033[0;0m", item, reader, stopwatch);
        }
    }

    /**
     * Used to unregister a model reader from the registry
     * @param name the name of the model reader to unregister
     */
    @Deprecated
    public void unregisterModelReader(ResourceLocation name) {
        if (!ItemRenderingAPIModelReadersRegistry.READERS.containsKey(name)) {
            ItemRenderingAPI.LOGGER.error("Attempted To Unregister Non Existent Model Reader {}", name);
        } else {
            ItemRenderingAPI.LOGGER.info("Starting Unregistration For Model Reader {}", name);
            Stopwatch stopwatch = Stopwatch.createStarted();
            ItemRenderingAPIModelReadersRegistry.READERS.remove(name);
            ItemRenderingAPI.LOGGER.info("Finished Unregistration On Model Reader {}, \033[0;31mTook {}\033[0;0m", name, stopwatch);
        }
    }

    /**
     * Exchanges one model reader with another to override
     * @param name the name of the reader to modify
     * @param newModel the model reader to replace the old one with
     */
    @Deprecated
    public void modifyModelReader(ResourceLocation name, IItemRenderingAPIModelReader newModel) {
        if (!ItemRenderingAPIModelReadersRegistry.READERS.containsKey(name)) {
            ItemRenderingAPI.LOGGER.error("Attempted To Modify Non Existent Model Reader {}", name);
        } else {
            ItemRenderingAPI.LOGGER.info("Starting Modification On Model Reader {}", name);
            Stopwatch stopwatch = Stopwatch.createStarted();
            ItemRenderingAPIModelReadersRegistry.READERS.replace(name, newModel);
            ItemRenderingAPI.LOGGER.info("Finished Modification On Model Reader {}, \033[0;31mTook {}\033[0;0m", name, stopwatch);
        }
    }

    /**
     * @return the name of all default contexts will always be "default"
     */
    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(ItemRenderingAPI.MOD_ID, "default");
    }
}