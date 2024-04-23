package net.vakror.item_rendering_api.impl;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.vakror.item_rendering_api.core.api.AbstractItemRenderingAPILayer;
import net.vakror.item_rendering_api.core.api.data.ItemRenderingAPIQuadRenderData;
import net.vakror.item_rendering_api.core.util.ModelUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector4i;

import java.util.List;
import java.util.Map;

public class ShapeRenderLayer extends AbstractItemRenderingAPILayer {
    private final Map<Pair<Vector2i, Direction>, Vector4i> colors;
    private final int size;

    public ShapeRenderLayer(Map<Pair<Vector2i, Direction>, Vector4i> colors, int size) {
        this.colors = colors;
        this.size = size;
    }

    @Override
    public void render(List<BakedQuad> quads, Transformation transformation, ItemRenderingAPIQuadRenderData data) {
        ModelUtils.addQuads(colors, quads, transformation, data.spriteGetter(), size);
    }

    @Override
    public @NotNull String getCacheKey(ItemRenderingAPIQuadRenderData data) {
        StringBuilder stringBuilder = new StringBuilder();
        colors.forEach((position, color) -> {
            stringBuilder.append("ENTRY: ").append(position).append(", ").append(color).append(" END ENTRY");
        });
        return stringBuilder.toString();
    }
}
