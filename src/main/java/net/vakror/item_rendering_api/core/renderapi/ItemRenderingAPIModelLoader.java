package net.vakror.item_rendering_api.core.renderapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/* Used in read json of items */
public enum ItemRenderingAPIModelLoader implements IGeometryLoader<ItemRenderingAPIModel> {
	INSTANCE;

	public static final List<ResourceLocation> textures = new ArrayList<ResourceLocation>();

	@Override
	public @NotNull ItemRenderingAPIModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext deserializationContext) {
		return new ItemRenderingAPIModel(modelContents);
	}
}