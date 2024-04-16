package net.vakror.item_rendering_api;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.vakror.item_rendering_api.core.renderapi.ItemRenderingAPIModelLoader;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemRenderingAPI.MOD_ID)
public class ItemRenderingAPI
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "item_rendering_api";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ItemRenderingAPI(IEventBus modEventBus) {
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
