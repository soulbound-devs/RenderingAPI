package net.vakror.item_rendering_api.core.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.vakror.item_rendering_api.ItemRenderingAPI;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4i;

import static net.vakror.item_rendering_api.core.util.QuadMaker.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtils {
    public static final float NORTH_Z = 7.496f;
    public static final float SOUTH_Z = 8.504f;

    /**
     * utility function to get a {@link TextureAtlasSprite} using a sprite getter and a {@link ResourceLocation}
     *
     * @param spriteGetter the function that maps {@link Material Materials} to {@link TextureAtlasSprite TextureAtlasSprites}
     * @param location     the location of the texture
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
        genQuads(sprites, quads, transform, false, spriteGetter, 16, true);
    }

    public static void genQuads(List<TextureAtlasSprite> sprites, List<BakedQuad> quads, Transformation transform, boolean blendQuads, Function<Material, TextureAtlasSprite> spriteGetter, int textureSize, boolean removeInternalQuads) {
        addQuads(getColorMap(sprites, blendQuads, textureSize, removeInternalQuads), quads, transform, spriteGetter, textureSize);
    }

    private static Multimap<Vector2i, Direction> getPositionsMap(List<TextureAtlasSprite> sprites, int textureSize, boolean removeInternalQuads) {
        Multimap<Vector2i, Direction> quadPositions;
        boolean[][] positions = getPositions(sprites, textureSize);
        if (removeInternalQuads) {
            quadPositions = getListOfExternalQuads(positions);
        } else {
            quadPositions = getAllQuads(positions);
        }
        return quadPositions;
    }

    public static Map<Pair<Vector2i, Direction>, Vector4i> getColorMap(List<TextureAtlasSprite> sprites, boolean blendQuads, int textureSize, boolean removeInternalQuads) {
        Multimap<Vector2i, Direction> quadPositions = getPositionsMap(sprites, textureSize, removeInternalQuads);
        Map<Pair<Vector2i, Direction>, Vector4i> colors;

        if (blendQuads) colors = getBlendedColors(quadPositions, sprites);
        else colors = getColors(quadPositions, sprites);

        return colors;
    }

    public static Multimap<Vector2i, Direction> getAllQuads(boolean[][] positions) {
        int size = positions.length;
        Multimap<Vector2i, Direction> allPositions = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (int x = 0; x < size - 1; x++) {
            for (int y = 0; y < size - 1; y++) {
                if (positions[x][y]) {
                    for (Direction dir : Direction.values()) {
                        allPositions.put(new Vector2i(x, y), dir);
                    }
                }
            }
        }
        return allPositions;
    }

    public static void addQuads(Map<Pair<Vector2i, Direction>, Vector4i> colors, List<BakedQuad> quads, Transformation transform, Function<Material, TextureAtlasSprite> spriteGetter, int textureSize) {
        colors.forEach((pair, color) -> {
            Direction direction = pair.getSecond();
            TextureAtlasSprite whiteSprite = getSprite(spriteGetter, new ResourceLocation(ItemRenderingAPI.MOD_ID, "item/white"));

            switch (direction) {
                case NORTH ->
                        genFrontTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
                case SOUTH ->
                        genBackTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
                case EAST ->
                        genRightTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
                case WEST ->
                        genLeftTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
                case UP ->
                        genUpTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
                case DOWN ->
                        genDownTextureQuad(whiteSprite, quads, transform, pair.getFirst().x, pair.getFirst().y, color, textureSize);
            }
        });
    }

    public static Map<Pair<Vector2i, Direction>, Vector4i> getColors(Multimap<Vector2i, Direction> quadPositions, List<TextureAtlasSprite> sprites) {
        Map<Pair<Vector2i, Direction>, Vector4i> colors = new HashMap<>();

        for (Vector2i vector : quadPositions.keySet()) {
            TextureAtlasSprite sprite = findLastNotTransparent(vector.x, vector.y, sprites);
            if (sprite != null) {
                for (Direction direction : quadPositions.get(vector)) {
                    colors.put(new Pair<>(vector, direction), deconstructCol(sprite.getPixelRGBA(0, vector.x, vector.y), sprite.contents().getOriginalImage().format()));
                }
            }
        }

        return colors;
    }

    public static Map<Pair<Vector2i, Direction>, Vector4i> getBlendedColors(Multimap<Vector2i, Direction> quadPositions, List<TextureAtlasSprite> sprites) {
        Map<Pair<Vector2i, Direction>, Vector4i> colors = new HashMap<>();

        for (Vector2i vector2i : quadPositions.keySet()) {
            Vector4i blendCol;
            List<Vector4i> cols = new ArrayList<>();
            for (TextureAtlasSprite sprite : sprites) {
                if (sprite.contents().isTransparent(0, vector2i.x, vector2i.y)) continue;

                cols.add(deconstructCol(sprite.getPixelRGBA(0, vector2i.x, vector2i.y), sprite.contents().getOriginalImage().format()));
            }

            if (findLastNotTransparent(vector2i.x, vector2i.y, sprites) != null) {
                if (cols.size() > 1) {
                    Vector4i tempCol = new Vector4i();
                    Vector4i col1 = null;
                    Vector4i col2 = null;
                    for (Vector4i col : cols) {
                        if (col1 != null) {
                            col2 = col;
                            col1 = colorBlend(col1.x, col1.y, col1.z, col1.w, col2.x, col2.y, col2.z, col2.w, 0.5);
                        } else {
                            col1 = col;
                        }
                    }
                    blendCol = col1;
                } else {
                    blendCol = cols.get(0);
                }
                for (Direction direction : quadPositions.get(new Vector2i(vector2i.x, vector2i.y))) {
                    colors.put(new Pair<>(new Vector2i(vector2i.x, vector2i.y), direction), blendCol);
                }
            }
        }

        return colors;
    }

    public static boolean[][] getPositions(List<TextureAtlasSprite> sprites, int textureSize) {
        boolean[][] positions = new boolean[textureSize][textureSize];
        for (int x = 0; x < textureSize - 1; x++) {
            for (int y = 0; y < textureSize - 1; y++) {
                if (findLastNotTransparent(x, y, sprites) != null) {
                    positions[x][y] = true;
                }
            }
        }
        return positions;
    }

    public static Multimap<Vector2i, Direction> getListOfExternalQuads(boolean[][] positions) {
        Multimap<Vector2i, Direction> externalPositions = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        externalPositions.putAll(getAllLeftRightExternalQuads(positions));
        externalPositions.putAll(getAllUpDownExternalQuads(positions));
        return externalPositions;
    }

    public static Multimap<Vector2i, Direction> getAllUpDownExternalQuads(boolean[][] positions) {
        int size = positions.length;
        Multimap<Vector2i, Direction> externalPositions = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        externalPositions.putAll(getAllLeftRightExternalQuads(positions));
        for (int x = 0; x <= size - 1; x++) {

            boolean hasFoundTransparent = true;

            for (int y = 0; y <= size - 1; y++) {
                if (positions[x][y]) {
                    if (hasFoundTransparent) {
                        externalPositions.put(new Vector2i(x, y), Direction.UP);
                        hasFoundTransparent = false;
                    }
                } else {
                    if (!hasFoundTransparent) {
                        externalPositions.put(new Vector2i(x, y - 1), Direction.DOWN);
                    }
                    hasFoundTransparent = true;
                }
            }
        }
        return externalPositions;
    }

    public static Multimap<? extends Vector2i, Direction> getAllLeftRightExternalQuads(boolean[][] positions) {
        int size = positions.length;
        Multimap<Vector2i, Direction> externalPositions = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (int y = 0; y <= size - 1; y++) {

            boolean hasFoundTransparent = true;

            for (int x = 0; x <= size - 1; x++) {
                if (positions[x][y]) {
                    externalPositions.put(new Vector2i(x, y), Direction.NORTH);
                    externalPositions.put(new Vector2i(x, y), Direction.SOUTH);
                    if (hasFoundTransparent) {
                        externalPositions.put(new Vector2i(x, y), Direction.WEST);
                        hasFoundTransparent = false;
                    }
                } else {
                    if (!hasFoundTransparent) {
                        externalPositions.put(new Vector2i(x - 1, y), Direction.EAST);
                    }
                    hasFoundTransparent = true;
                }
            }
        }
        return externalPositions;
    }

    public static Vector4i deconstructCol(int color, NativeImage.Format format) {
        int a = (color >> format.alphaOffset()) & 0xff; // or color >>> 24
        int r = (color >> format.redOffset()) & 0xff;
        int g = (color >> format.greenOffset()) & 0xff;
        int b = (color >> format.blueOffset()) & 0xff;
        return new Vector4i(r, g, b, a);
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

    /**
     * @param ratio a number between 0-1 that determines how the colors are mixed. 0 means only color 2, while 1 means only color 1. Any range between 0 and 1 is acceptable
     */
    public static Vector4i colorBlend(int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2, double ratio) {
        int r = (int) Math.round((r1 * (1 - ratio)) + (r2 * ratio));
        int g = (int) Math.round((g1 * (1 - ratio)) + (g2 * ratio));
        int b = (int) Math.round((b1 * (1 - ratio)) + (b2 * ratio));
        int a = (int) Math.round((a1 * (1 - ratio)) + (a2 * ratio));

        // Clamp values to stay within 0-255 range
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(255, a));

        return new Vector4i(r, g, b, a);
    }

    public static Vector4i blend(double ratio, Vector4i... colors) {
        if (ratio < 0 || ratio > 1) {
            throw new IllegalArgumentException("Ratio must be between 0 and 1");
        }

        if (colors.length < 2) {
            throw new IllegalArgumentException("At least two colors must be provided");
        }

        // Initialize accumulator for blended color
        Vector4i result = new Vector4i(0, 0, 0, 0);

        // Iterate through colors and accumulate weighted components
        for (Vector4i color : colors) {
            double weight = (color == colors[colors.length - 1]) ? ratio : (1 - ratio) / (colors.length - 1);
            result.x += (int) Math.round(color.x * weight);
            result.y += (int) Math.round(color.y * weight);
            result.z += (int) Math.round(color.z * weight);
            result.w += (int) Math.round(color.w * weight);
        }

        // Clamp values to stay within 0-255 range
        result.x = Math.max(0, Math.min(255, result.x));
        result.y = Math.max(0, Math.min(255, result.y));
        result.z = Math.max(0, Math.min(255, result.z));
        result.w = Math.max(0, Math.min(255, result.w));

        return result;
    }
}