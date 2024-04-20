package net.vakror.item_rendering_api;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.vakror.item_rendering_api.core.renderapi.ItemRenderingAPIModelLoader;
import net.vakror.item_rendering_api.test.TestItems;
import net.vakror.item_rendering_api.test.extension.TestExtension;
import net.vakror.registry.jamesregistryapi.api.event.RegistryEvents;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemRenderingAPI.MOD_ID)
public class ItemRenderingAPI
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "item_rendering_api";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ItemRenderingAPI(IEventBus modEventBus) {
        if (!FMLLoader.isProduction()) {
            TestItems.ITEM_REGISTER.register(modEventBus);
            RegistryEvents.SETUP_REGISTRY_EVENT.register(event -> {
                event.addRegistry(new TestExtension());
                return EventResult.pass();
            });
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onModelsRegistered(ModelEvent.RegisterGeometryLoaders event) {
            event.register(new ResourceLocation(MOD_ID, "item"), ItemRenderingAPIModelLoader.INSTANCE);
        }
    }
}
