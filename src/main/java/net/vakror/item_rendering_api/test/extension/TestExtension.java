package net.vakror.item_rendering_api.test.extension;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.vakror.item_rendering_api.ItemRenderingAPI;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.AbstractQuadProcessor;
import net.vakror.item_rendering_api.core.api.IItemRenderingAPIModelReader;
import net.vakror.item_rendering_api.core.api.ItemRenderingAPIQuadRenderData;
import net.vakror.item_rendering_api.core.extension.context.ModelReaderRegistrationContext;
import net.vakror.item_rendering_api.impl.TextureRenderLayer;
import net.vakror.registry.jamesregistryapi.api.AbstractExtension;

import java.util.List;

public class TestExtension extends AbstractExtension<ModelReaderRegistrationContext> {
    @Override
    public ResourceLocation getExtensionName() {
        return new ResourceLocation(ItemRenderingAPI.MOD_ID, "test");
    }

    @Override
    public void register() {
        context.registerModelReader(new ResourceLocation(ItemRenderingAPI.MOD_ID, "test"), new IItemRenderingAPIModelReader() {
            @Override
            public List<AbstractItemRenderingAPILayer> getLayers(JsonObject object) {
                return List.of(
                        new TextureRenderLayer(
                                new ResourceLocation(ItemRenderingAPI.MOD_ID ,"item/red"),
                                new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/black"))
                                .fullBright()
                                .withTextureSize(32)
                                .withBlending()
                );
            }

            @Override
            public List<Class<? extends Item>> getRequiredItems(JsonObject object) {
                return null;
            }

            @Override
            public List<AbstractQuadProcessor> getQuadProcessors(JsonObject object, ItemRenderingAPIQuadRenderData data) {
                return List.of();
            }
        });
        context.addItemForReader(new ResourceLocation(ItemRenderingAPI.MOD_ID, "test"), new ResourceLocation(ItemRenderingAPI.MOD_ID, "test"));
    }

    @Override
    public ModelReaderRegistrationContext getDefaultContext() {
        return new ModelReaderRegistrationContext();
    }
}
