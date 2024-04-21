package net.vakror.item_rendering_api.core.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class ItemRenderingAPIOverrideList extends ItemOverrides {
	private final Function<Material, TextureAtlasSprite> spriteGetter;
	private final List<Class<? extends Item>> itemClass;

	public ItemRenderingAPIOverrideList(Function<Material, TextureAtlasSprite> spriteGetterIn, List<Class<? extends Item>> itemclass) {
		this.spriteGetter = spriteGetterIn;
		this.itemClass = itemclass;
	}

	@Override
	public BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack, ClientLevel worldIn, LivingEntity entityIn, int seed) {
		if (model instanceof ItemRenderingAPIBakedModel apiModel) {
			if (itemClass == null) {
				return getModel(model, stack, worldIn, entityIn, seed, apiModel);
			} else {
                for (Class<? extends Item> clazz : itemClass) {
                    if (clazz.isInstance(stack.getItem())) {
                        return getModel(model, stack, worldIn, entityIn, seed, apiModel);
                    }
                }
            }
		}
		return model;
	}

	private BakedModel getModel(BakedModel model, ItemStack stack, ClientLevel worldIn, LivingEntity entityIn, int seed, ItemRenderingAPIBakedModel apiModel) {
		for (AbstractItemRenderingAPILayer layer : apiModel.layers) {
			layer.setData(new ItemRenderingAPIQuadRenderData(model, stack, worldIn, entityIn, seed, spriteGetter));
			layer.setModel(apiModel);
		}
		return apiModel.getNewBakedItemModel();
	}
}