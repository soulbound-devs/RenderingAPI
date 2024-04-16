package net.vakror.item_rendering_api.core.api;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.vakror.item_rendering_api.core.renderapi.ItemRenderingAPIBakedModel;
import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Function;

/**
 * Extra data passed to {@link AbstractItemRenderingAPILayer Layers} and {@link AbstractQuadProcessor QuadProcessors}
 * @param model the {@link ItemRenderingAPIBakedModel}
 * @param stack the {@link ItemStack} that the model is being made for
 * @param worldIn the {@link Level} this model is being made in
 * @param entityIn the entity this item is being rendered for
 * @param seed the seed number
 * @param spriteGetter a function to get {@link TextureAtlasSprite TextureAtlasSprites} from {@link ResourceLocation ResourceLocations}
 */
public record ItemRenderingAPIQuadRenderData(BakedModel model, ItemStack stack, ClientLevel worldIn, @Nullable LivingEntity entityIn, int seed, Function<Material, TextureAtlasSprite> spriteGetter) {
}
