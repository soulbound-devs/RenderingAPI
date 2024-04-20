package net.vakror.item_rendering_api.core.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.client.model.pipeline.TransformingVertexPipeline;
import org.joml.Vector4f;

import java.util.List;

import static net.vakror.item_rendering_api.core.util.ModelUtils.NORTH_Z;
import static net.vakror.item_rendering_api.core.util.ModelUtils.SOUTH_Z;

public class QuadMaker {
    public static void genFrontBackTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col) {
        if (sprite == null) return;
        if (sprite.contents().isTransparent(0, x, y)) return;

        float xStart = x / 16.0f;
        float xEnd = (x + 1) / 16.0f;

        float yStart = (16 - (y + 1)) / 16.0f;
        float yEnd = (16 - y) / 16.0f;

        BakedQuad a = createQuad(
                new Vec3(xStart, yStart, NORTH_Z)
                , new Vec3(xStart, yEnd, NORTH_Z)
                , new Vec3(xEnd, yEnd, NORTH_Z)
                , new Vec3(xEnd, yStart, NORTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.NORTH, transform, col);

        BakedQuad b = createQuad(
                new Vec3(xStart, yStart, SOUTH_Z)
                , new Vec3(xEnd, yStart, SOUTH_Z)
                , new Vec3(xEnd, yEnd, SOUTH_Z)
                , new Vec3(xStart, yEnd, SOUTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.SOUTH, transform, col);

        if (a != null) {
            quads.add(a);
        }
        if (b != null) {
            quads.add(b);
        }
    }

    public static void genUpDownTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col) {
        float xStart = x / 16.0f;
        float xEnd = (x + 1) / 16.0f;

        quads.add(createQuad(
                new Vec3(xStart, (16 - y) / 16.0f, NORTH_Z)
                , new Vec3(xStart, (16 - y) / 16.0f, SOUTH_Z)
                , new Vec3(xEnd, (16 - y) / 16.0f, SOUTH_Z)
                , new Vec3(xEnd, (16 - y) / 16.0f, NORTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.UP, transform, col));


        quads.add(createQuad(
                new Vec3(xStart, (16 - (y + 1)) / 16.0f, NORTH_Z)
                , new Vec3(xEnd, (16 - (y + 1)) / 16.0f, NORTH_Z)
                , new Vec3(xEnd, (16 - (y + 1)) / 16.0f, SOUTH_Z)
                , new Vec3(xStart, (16 - (y + 1)) / 16.0f, SOUTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.DOWN, transform, col));

    }

    public static void genLeftRightTextureQuad(TextureAtlasSprite sprite, List<BakedQuad> quads, Transformation transform, int x, int y, Vector4f col) {
        float yStart = (16 - (y + 1)) / 16.0f;
        float yEnd = (16 - y) / 16.0f;

        quads.add(createQuad(
                new Vec3(x / 16.0f, yStart, NORTH_Z)
                , new Vec3(x / 16.0f, yStart, SOUTH_Z)
                , new Vec3(x / 16.0f, yEnd, SOUTH_Z)
                , new Vec3(x / 16.0f, yEnd, NORTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.WEST, transform, col));

        quads.add(createQuad(
                new Vec3((x + 1) / 16.0f, yStart, NORTH_Z)
                , new Vec3((x + 1) / 16.0f, yEnd, NORTH_Z)
                , new Vec3((x + 1) / 16.0f, yEnd, SOUTH_Z)
                , new Vec3((x + 1) / 16.0f, yStart, SOUTH_Z)
                , x, x + 1, y, y + 1
                , sprite, Direction.EAST, transform, col));

    }

    public static BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, int xStart, int xEnd, int yStart, int yEnd, TextureAtlasSprite sprite, Direction orientation, Transformation transform, Vector4f col) {
        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(q -> quad[0] = q);
        VertexConsumer consumer = new TransformingVertexPipeline(builder, transform);

        builder.setSprite(sprite);

        putVertex(consumer, v1, xStart, yEnd, sprite, orientation, col);
        putVertex(consumer, v2, xStart, yStart, sprite, orientation, col);
        putVertex(consumer, v3, xEnd, yStart, sprite, orientation, col);
        putVertex(consumer, v4, xEnd, yEnd, sprite, orientation, col);

        return quad[0];
    }

    /* Put data into the consumer */
    public static void putVertex(VertexConsumer consumer, Vec3 vec, double u, double v, TextureAtlasSprite sprite, Direction orientation, Vector4f color) {
        float fu = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * (float)u / 16.0F;
        float fv = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * (float)v / 16.0F;

        consumer.vertex((float) vec.x, (float) vec.y, (float) vec.z)
                .color(color.x, color.y, color.z, color.w)
                .normal((float) orientation.getStepX(), (float) orientation.getStepY(), (float) orientation.getStepZ())
                .uv(fu, fv)
                .uv2(0, 0)
                .endVertex();
    }
}
