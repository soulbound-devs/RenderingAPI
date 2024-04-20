package net.vakror.item_rendering_api.core.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.vakror.item_rendering_api.ItemRenderingAPI;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import static net.vakror.item_rendering_api.core.util.QuadMaker.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModelUtils {

    public static float NORTH_Z = 7.496f / 16f;
    public static float SOUTH_Z = 8.504f / 16f;

    @Nullable
    public static TextureAtlasSprite getSprite(Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ResourceLocation location) {
        @SuppressWarnings("deprecation")
        Material material = new Material(TextureAtlas.LOCATION_BLOCKS, location);
        TextureAtlasSprite sprite = spriteGetter.apply(material);
        if (sprite != null && !sprite.atlasLocation().equals(MissingTextureAtlasSprite.getLocation())) {
            return sprite;
        }
        return null;
    }

    public static void genQuads(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, Function<Material, TextureAtlasSprite> spriteGetter) {
        genQuads(sprites, quads, transform, false, spriteGetter, -1, null);
    }

    public static void genQuads(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor) {
        addQuadsFromSprite(sprites, quads, transform, blendQuads, spriteGetter, emissivity, tintColor);
    }

    private static void addQuadsFromSprite(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor) {
        List<BakedQuad> tempQuads = new ArrayList<>();
        addQuads(sprites, tempQuads, transform, blendQuads, spriteGetter, emissivity, tintColor);
        if (emissivity >= 0 && emissivity <= 15) {
            QuadTransformers.settingEmissivity(emissivity).processInPlace(tempQuads);
        }
        quads.addAll(tempQuads);
    }

    private static void addQuads(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor) {
        for (int x = 0; x <= 15; x++) {
            for (int y = 0; y <= 15; y++) {
                Vector4f blendCol = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
                int amountOfSpritesBlended = 0;
                boolean doStuff = true;
                for (TextureAtlasSprite sprite : sprites) {
                    if (doStuff) {
                        if (sprite == null) return;
                        if (sprite.contents().isTransparent(0, x, y)) continue;

                        if (blendQuads) {
                            blendCol.add(deconstructCol(sprite.getPixelRGBA(0, x, y), sprite.contents().getOriginalImage().format()));
                            amountOfSpritesBlended++;
                        } else {
                            genLastNotTransparentQuads(sprites, tintColor, x, y, transform, quads, spriteGetter);
                            doStuff = false;
                        }
                    }
                }

                if (blendQuads) {
                    blendCol.div(amountOfSpritesBlended * 255);
                    genBlendedQuads(blendCol, quads, transform, x, y, spriteGetter);
                }
            }
        }
    }

    public static void genBlendedQuads(Vector4f blendCol, List<BakedQuad> quads, Transformation transform, int x, int y, Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite white = getSprite(spriteGetter, new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/white"));

        genFrontBackTextureQuad(white, quads, transform, x, y, blendCol);
        genUpDownTextureQuad(white, quads, transform, x, y, blendCol);
        genLeftRightTextureQuad(white, quads, transform, x, y, blendCol);
    }

    public static void genLastNotTransparentQuads(List<TextureAtlasSprite> sprites, Vector4f tintColor, int x, int y, Transformation transform, List<BakedQuad> quads, Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite sprite = findLastNotTransparent(x, y, sprites);

        genFrontBackTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor);
        genUpDownTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor);
        genLeftRightTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor);
    }

    private static Vector4f deconstructCol(int color, NativeImage.Format format) {
        int a = (color >> format.alphaOffset()) & 0xff; // or color >>> 24
        int r = (color >> format.redOffset()) & 0xff;
        int g = (color >> format.greenOffset()) & 0xff;
        int b = (color >> format.blueOffset()) & 0xff;
        return new Vector4f(r, g, b, a);
    }

    @Nullable
    public static TextureAtlasSprite findLastNotTransparent(int x, int y, List<TextureAtlasSprite> sprites) {
        for (TextureAtlasSprite sprite : sprites) {
            if (sprite != null) {
                if (!sprite.contents().isTransparent(0, x, y)) {
                    return sprite;
                }
            }
        }
        return null;
    }
}