package net.vakror.item_rendering_api.test;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vakror.item_rendering_api.ItemRenderingAPI;

public class TestItems {
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(Registries.ITEM, ItemRenderingAPI.MOD_ID);

    public static final DeferredHolder<Item, Item> TEST = ITEM_REGISTER.register("test", () -> new Item(new Item.Properties()));
}
