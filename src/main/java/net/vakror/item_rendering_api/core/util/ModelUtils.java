package net.vakror.item_rendering_api.core.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
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
    public static final float NORTH_Z = 7.496f;
    public static final float SOUTH_Z = 8.504f;

    /**
     * utility function to get a {@link TextureAtlasSprite} using a sprite getter and a {@link ResourceLocation}
     * @param spriteGetter the function that maps {@link Material Materials} to {@link TextureAtlasSprite TextureAtlasSprites}
     * @param location the location of the texture
     * @return the {@link TextureAtlasSprite} representing the file
     */
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
        genQuads(sprites, quads, transform, false, spriteGetter, -1, null, 16, true);
    }

    public static void genQuads(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor, int textureSize, boolean removeInternalQuads) {
        List<BakedQuad> tempQuads = new ArrayList<>();
        if (removeInternalQuads) {
            addExternalQuadsFromSprite(sprites, tempQuads, transform, blendQuads, spriteGetter, emissivity, tintColor, textureSize);
        } else {
            addAllQuadsFromSprite(sprites, tempQuads, transform, blendQuads, spriteGetter, emissivity, tintColor, textureSize);
        }
        if (emissivity >= 0 && emissivity <= 15) {
            QuadTransformers.settingEmissivity(emissivity).processInPlace(tempQuads);
        }
        quads.addAll(tempQuads);
    }

    private static void addAllQuadsFromSprite(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor, int textureSize) {
        for (int y = 0; y <= textureSize - 1; y++) {
            for (int x = 0; x <= textureSize - 1; x++) {
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
                            genLeftRightFrontBackLastNotTransparentQuads(sprites, tintColor, x, y, transform, quads, textureSize, new ArrayList<>(), new ArrayList<>());
                            genUpDownLastNotTransparentQuads(sprites, tintColor, x, y, transform, quads, textureSize, new ArrayList<>(), new ArrayList<>());
                            doStuff = false;
                        }
                        if (blendQuads) {
                            blendCol.add(deconstructCol(sprite.getPixelRGBA(0, x, y), sprite.contents().getOriginalImage().format()));
                            amountOfSpritesBlended++;
                        }
                    }
                }

                if (blendQuads && findLastNotTransparent(x, y, sprites) != null) {
                    blendCol.div(amountOfSpritesBlended * 255);
                    genLeftRightFrontBackBlendedQuads(blendCol, quads, transform, x, y, spriteGetter, textureSize, new ArrayList<>(), new ArrayList<>());
                    genUpDownBlendedQuads(blendCol, quads, transform, x, y, spriteGetter, textureSize, new ArrayList<>(), new ArrayList<>());
                }
            }
        }
    }

    private static void addExternalQuadsFromSprite(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int emissivity, Vector4f tintColor, int textureSize) {
        for (int y = 0; y <= textureSize - 1; y++) {
            List<Integer> leftMost = new ArrayList<>();
            List<Integer> rightMost = new ArrayList<>();

            boolean hasFoundTransparent = true;

            for (int x = 0; x <= textureSize - 1; x++) {
                if (findLastNotTransparent(x, y, sprites) != null && hasFoundTransparent) {
                    leftMost.add(x);
                    hasFoundTransparent = false;
                }
                if (findLastNotTransparent(x, y, sprites) == null) {
                    if (!hasFoundTransparent) {
                        rightMost.add(x - 1);
                    }
                    hasFoundTransparent = true;
                }
            }

            for (int x = 0; x <= textureSize - 1; x++) {
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
                            genLeftRightFrontBackLastNotTransparentQuads(sprites, tintColor, x, y, transform, quads, textureSize, leftMost, rightMost);
                            doStuff = false;
                        }
                    }
                }

                if (blendQuads && findLastNotTransparent(x, y, sprites) != null) {
                    blendCol.div(amountOfSpritesBlended * 255);
                    genLeftRightFrontBackBlendedQuads(blendCol, quads, transform, x, y, spriteGetter, textureSize, leftMost, rightMost);
                }
            }
        }

        for (int x = 0; x <= textureSize - 1; x++) {
            List<Integer> topMost = new ArrayList<>();
            List<Integer> bottomMost = new ArrayList<>();

            boolean hasFoundTransparent = true;

            for (int y = 0; y <= textureSize - 1; y++) {
                if (findLastNotTransparent(x, y, sprites) != null && hasFoundTransparent) {
                    topMost.add(y);
                    hasFoundTransparent = false;
                }
                if (findLastNotTransparent(x, y, sprites) == null) {
                    if (!hasFoundTransparent) {
                        bottomMost.add(y - 1);
                    }
                    hasFoundTransparent = true;
                }
            }
            for (int y = 0; y <= textureSize - 1; y++) {
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
                            genUpDownLastNotTransparentQuads(sprites, tintColor, x, y, transform, quads, textureSize, topMost, bottomMost);
                            doStuff = false;
                        }
                    }
                }

                if (blendQuads && findLastNotTransparent(x, y, sprites) != null) {
                    blendCol.div(amountOfSpritesBlended * 255);
                    genUpDownBlendedQuads(blendCol, quads, transform, x, y, spriteGetter, textureSize, topMost, bottomMost);
                }
            }
        }
    }

    public static void genLeftRightFrontBackBlendedQuads(Vector4f blendCol, List<BakedQuad> quads, Transformation transform, int x, int y, Function<Material, TextureAtlasSprite> spriteGetter, int textureSize, List<Integer> leftMost, List<Integer> rightMost) {
        TextureAtlasSprite white = getSprite(spriteGetter, new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/white"));
        genFrontBackTextureQuad(white, quads, transform, x, y, blendCol, textureSize);

        if (leftMost.isEmpty() && rightMost.isEmpty()) {
            genLeftTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
            genRightTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        }

        if (leftMost.contains(x)) {
            genLeftTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        } if (rightMost.contains(x)) {
            genRightTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        }
    }


    public static void genLeftRightFrontBackLastNotTransparentQuads(List<TextureAtlasSprite> sprites, Vector4f tintColor, int x, int y, Transformation transform, List<BakedQuad> quads, int textureSize, List<Integer> leftMost, List<Integer> rightMost) {
        TextureAtlasSprite sprite = findLastNotTransparent(x, y, sprites);
        genFrontBackTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);

        if (leftMost.isEmpty() && rightMost.isEmpty()) {
            genLeftTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
            genRightTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        }

        if (leftMost.contains(x)) {
            genLeftTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        } if (rightMost.contains(x)) {
            genRightTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        }
    }

    public static void genUpDownBlendedQuads(Vector4f blendCol, List<BakedQuad> quads, Transformation transform, int x, int y, Function<Material, TextureAtlasSprite> spriteGetter, int textureSize, List<Integer> topMost, List<Integer> bottomMost) {
        TextureAtlasSprite white = getSprite(spriteGetter, new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/white"));

        if (topMost.isEmpty() && bottomMost.isEmpty()) {
            genUpTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
            genDownTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        }

        if (topMost.contains(y)) {
            genUpTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        } if (bottomMost.contains(y)) {
            genDownTextureQuad(white, quads, transform, x, y, blendCol, textureSize);
        }
    }


    public static void genUpDownLastNotTransparentQuads(List<TextureAtlasSprite> sprites, Vector4f tintColor, int x, int y, Transformation transform, List<BakedQuad> quads, int textureSize, List<Integer> topMost, List<Integer> bottomMost) {
        TextureAtlasSprite sprite = findLastNotTransparent(x, y, sprites);

        if (topMost.isEmpty() && bottomMost.isEmpty()) {
            genUpTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
            genDownTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        }

        if (topMost.contains(y)) {
            genUpTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        } if (bottomMost.contains(y)) {
            genDownTextureQuad(sprite, quads, transform, x, y, tintColor == null ? new Vector4f(1.0f, 1.0f, 1.0f, 1.0f): tintColor, textureSize);
        }
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

    public static boolean isPowerOfTwo(int x) {
        return x != 0 && ((x & (x - 1)) == 0);
    }
}