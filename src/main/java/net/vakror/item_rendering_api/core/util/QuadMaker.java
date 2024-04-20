package net.vakror.item_rendering_api.core.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.client.model.pipeline.TransformingVertexPipeline;
import net.vakror.item_rendering_api.ItemRenderingAPI;
import org.joml.Vector4f;

import java.util.List;

import static net.vakror.item_rendering_api.core.util.ModelUtils.NORTH_Z;
import static net.vakror.item_rendering_api.core.util.ModelUtils.SOUTH_Z;

public class QuadMaker {
    public static void genFrontBackTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col, int textureSize) {
        if (sprite == null) return;

        float xStart = (float) x / textureSize;
        float xEnd = (float) (x + 1) / textureSize;

        float yStart = (float) (textureSize - (y + 1)) / textureSize;
        float yEnd = (float) (textureSize - y) / textureSize;

        BakedQuad a = createQuad(
                new Vec3(xStart, yStart, NORTH_Z / textureSize)
                , new Vec3(xStart, yEnd, NORTH_Z / textureSize)
                , new Vec3(xEnd, yEnd, NORTH_Z / textureSize)
                , new Vec3(xEnd, yStart, NORTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.NORTH, transform, col, textureSize);

        BakedQuad b = createQuad(
                new Vec3(xStart, yStart, SOUTH_Z / textureSize)
                , new Vec3(xEnd, yStart, SOUTH_Z / textureSize)
                , new Vec3(xEnd, yEnd, SOUTH_Z / textureSize)
                , new Vec3(xStart, yEnd, SOUTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.SOUTH, transform, col, textureSize);

        if (a != null) {
            quads.add(a);
        }
        if (b != null) {
            quads.add(b);
        }
    }

    public static void genUpDownTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col, int textureSize) {
        float xStart = (float) x / textureSize;
        float xEnd = (float) (x + 1) / textureSize;

        quads.add(createQuad(
                new Vec3(xStart, (double) (textureSize - y) / textureSize, NORTH_Z / textureSize)
                , new Vec3(xStart, (double) (textureSize - y) / textureSize, SOUTH_Z / textureSize)
                , new Vec3(xEnd, (double) (textureSize - y) / textureSize, SOUTH_Z / textureSize)
                , new Vec3(xEnd, (double) (textureSize - y) / textureSize, NORTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.UP, transform, col, textureSize));


        quads.add(createQuad(
                new Vec3(xStart, (double) (textureSize - (y + 1)) / textureSize, NORTH_Z / textureSize)
                , new Vec3(xEnd, (double) (textureSize - (y + 1)) / textureSize, NORTH_Z / textureSize)
                , new Vec3(xEnd, (double) (textureSize - (y + 1)) / textureSize, SOUTH_Z / textureSize)
                , new Vec3(xStart, (double) (textureSize - (y + 1)) / textureSize, SOUTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.DOWN, transform, col, textureSize));

    }

    public static void genLeftRightTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col, int textureSize) {
        float yStart = (float) (textureSize - (y + 1)) / textureSize;
        float yEnd = (float) (textureSize - y) / textureSize;

        quads.add(createQuad(
                new Vec3((double) x / textureSize, yStart, NORTH_Z / textureSize)
                , new Vec3((double) x / textureSize, yStart, SOUTH_Z / textureSize)
                , new Vec3((double) x / textureSize, yEnd, SOUTH_Z / textureSize)
                , new Vec3((double) x / textureSize, yEnd, NORTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.WEST, transform, col, textureSize));

        quads.add(createQuad(
                new Vec3((double) (x + 1) / textureSize, yStart, NORTH_Z / textureSize)
                , new Vec3((double) (x + 1) / textureSize, yEnd, NORTH_Z / textureSize)
                , new Vec3((double) (x + 1) / textureSize, yEnd, SOUTH_Z / textureSize)
                , new Vec3((double) (x + 1) / textureSize, yStart, SOUTH_Z / textureSize)
                , x, x + 1, y, y + 1
                , sprite, Direction.EAST, transform, col, textureSize));

    }

    public static BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, int xStart, int xEnd, int yStart, int yEnd, TextureAtlasSprite sprite, Direction orientation, Transformation transform, Vector4f col, int textureSize) {
        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(q -> quad[0] = q);
        VertexConsumer consumer = new TransformingVertexPipeline(builder, transform);

        builder.setSprite(sprite);

        putVertex(consumer, v1, xStart, yEnd, sprite, orientation, col, textureSize);
        putVertex(consumer, v2, xStart, yStart, sprite, orientation, col, textureSize);
        putVertex(consumer, v3, xEnd, yStart, sprite, orientation, col, textureSize);
        putVertex(consumer, v4, xEnd, yEnd, sprite, orientation, col, textureSize);

        return quad[0];
    }

    /* Put data into the consumer */
    public static void putVertex(VertexConsumer consumer, Vec3 vec, double u, double v, TextureAtlasSprite sprite, Direction orientation, Vector4f color, int textureSize) {
        if (sprite.contents().name().equals(new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/white"))) {
            u = 0;
            v = 0;
        }
        float fu = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * (float)u / textureSize;
        float fv = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * (float)v / textureSize;

        consumer.vertex((float) vec.x, (float) vec.y, (float) vec.z)
                .color(color.x, color.y, color.z, color.w)
                .normal((float) orientation.getStepX(), (float) orientation.getStepY(), (float) orientation.getStepZ())
                .uv(fu, fv)
                .uv2(0, 0)
                .endVertex();
    }
}
