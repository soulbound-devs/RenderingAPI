/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.vakror.item_rendering_api.core.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The baked item model representing an item model
 */
public class BakedItemModel implements BakedModel {
    protected final ImmutableList<BakedQuad> quads;
    protected final TextureAtlasSprite particle;
    protected final ImmutableMap<ItemDisplayContext, ItemTransform> transforms;
    protected final ItemOverrides overrides;
    protected final BakedModel guiModel;
    protected final boolean isSideLit;
    protected final boolean useAmbientOcclusion;
    protected final boolean isGui3d;

    /**
     * The constructor for this model
     * @param quads the list of quads to render (resolved in {@link #getQuads})
     * @param particle the particle sprite for this model
     * @param transforms a map mapping {@link ItemDisplayContext ItemDisplayContexts} to {@link ItemTransform ItemTransforms}
     * @param overrides the item overrides to use for this model
     * @param untransformed whether this model is untransformed
     * @param isSideLit whether this model should be side lit in the gui
     * @param useAmbientOcclusion whether to use ambient occlusion
     * @param isGui3d whether to render as 3d in the gui
     */
    public BakedItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<ItemDisplayContext, ItemTransform> transforms, ItemOverrides overrides, boolean untransformed, boolean isSideLit, boolean useAmbientOcclusion, boolean isGui3d) {
        this.quads = quads;
        this.particle = particle;
        this.transforms = transforms;
        this.overrides = overrides;
        this.isSideLit = isSideLit;
        this.guiModel = untransformed && hasGuiIdentity(transforms) ? new BakedGuiItemModel<>(this) : null;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.isGui3d = isGui3d;
    }

    /**
     * @param transforms the transforms to use for this model
     * @return whether this model is untransformed in the gui
     */
    private static boolean hasGuiIdentity(ImmutableMap<ItemDisplayContext, ItemTransform> transforms) {
        ItemTransform guiTransform = transforms.get(ItemDisplayContext.GUI);
        return guiTransform == null || guiTransform.equals(ItemTransform.NO_TRANSFORM);
    }

    /**
     * @return whether to use ambient occlusion
     */
    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }

    /**
     * @return whether to appear 3d in the gui
     */
    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    /**
     * @return whether to be shaded like a block in the gui
     */
    @Override
    public boolean usesBlockLight() {
        return isSideLit;
    }

    /**
     * @return whether this model uses a {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer BlockEntityWithoutLevelRenderer}
     */
    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    /**
     * @return the {@link TextureAtlasSprite} representing the model's particle
     */
    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    /**
     * @return the {@link ItemOverrides} for this model
     */
    @Override
    public @NotNull ItemOverrides getOverrides() {
        return overrides;
    }

    /**
     * @return a list of quads to render
     */
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side == null) {
            return quads;
        }
        return ImmutableList.of();
    }

    /**
     * used to apply model transforms
     * @param type the {@link ItemDisplayContext} to apply the transform to
     * @param stack the {@link PoseStack}
     * @param applyLeftHandTransform whether the model is in the left hand
     * @return the transformed baked model to render
     */
    @Override
    public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext type, @NotNull PoseStack stack, boolean applyLeftHandTransform) {
        if (type == ItemDisplayContext.GUI && this.guiModel != null) {
            return this.guiModel.applyTransform(type, stack, applyLeftHandTransform);
        }
        return handlePerspective(this, transforms, type, stack, applyLeftHandTransform);
    }

    /**
     * utility method called by {@link #applyTransform} to handle perspective
     * @param model the baked model
     * @param transforms a map mapping {@link ItemDisplayContext ItemDisplayContexts} to {@link ItemTransform ItemTransforms}
     * @param cameraTransformType the current {@link ItemDisplayContext Transform Type}
     * @param mat the {@link PoseStack}
     * @param leftHand whether the model is in the left hand
     * @return the transformed baked model to render
     */
    @SuppressWarnings("all")
    public static BakedModel handlePerspective(BakedModel model, ImmutableMap<ItemDisplayContext, ItemTransform> transforms, ItemDisplayContext cameraTransformType, PoseStack mat, boolean leftHand) {
        ItemTransform tr = transforms.getOrDefault(cameraTransformType, ItemTransform.NO_TRANSFORM);
        if (!tr.equals(tr.NO_TRANSFORM)) {
            tr.apply(leftHand, mat);
        }
        return model;
    }

    /**
     * a wrapper around the {@link BakedItemModel} for use in GUI rendering
     * @param <T> the type of the original {@link BakedItemModel}
     */
    public static class BakedGuiItemModel<T extends BakedItemModel> extends BakedModelWrapper<T> {
        private final ImmutableList<BakedQuad> quads;

        /**
         * @param originalModel the original model
         */
        public BakedGuiItemModel(T originalModel) {
            super(originalModel);
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            if (originalModel.quads != null) {
                for (BakedQuad quad : originalModel.quads) {
                    if (quad.getDirection() == Direction.SOUTH) {
                        builder.add(quad);
                    }
                }
            }
            this.quads = builder.build();
        }

        /**
         * used to apply model transforms
         * @param type the {@link ItemDisplayContext} to apply the transform to
         * @param poseStack the {@link PoseStack}
         * @param doLeftHandTransformation whether the model is in the left hand
         * @return the transformed baked model to render
         */
        @Override
        public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext type, @NotNull PoseStack poseStack, boolean doLeftHandTransformation) {
            if (type == ItemDisplayContext.GUI) {
                return handlePerspective(this, originalModel.transforms, type, poseStack, doLeftHandTransformation);
            }
            return handlePerspective(this.originalModel, this.originalModel.transforms, type, poseStack, doLeftHandTransformation);
        }
    }
}
