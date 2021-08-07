package com.simibubi.create.foundation.item.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simibubi.create.Create;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

public abstract class CustomRenderedItemModel extends ForwardingBakedModel {

	protected String basePath;
	protected Map<String, BakedModel> partials = new HashMap<>();
	protected DynamicItemRenderer renderer;

	public CustomRenderedItemModel(BakedModel template, String basePath) {
		wrapped = template;
		this.basePath = basePath;
		this.renderer = createRenderer();
	}

	@Override
	public boolean isCustomRenderer() {
		return true;
	}

	public final BakedModel getOriginalModel() {
		return wrapped;
	}

	public DynamicItemRenderer getRenderer() {
		return renderer;
	}

	public abstract DynamicItemRenderer createRenderer();

	public final List<ResourceLocation> getModelLocations() {
		return partials.keySet().stream().map(this::getPartialModelLocation).collect(Collectors.toList());
	}

	protected void addPartials(String... partials) {
		this.partials.clear();
		for (String name : partials)
			this.partials.put(name, null);
	}

	public CustomRenderedItemModel loadPartials(ModelBakery bakery) {
		for (String name : partials.keySet())
			partials.put(name, loadModel(bakery, name));
		return this;
	}

	private BakedModel loadModel(ModelBakery bakery, String name) {
		return bakery.bake(getPartialModelLocation(name), BlockModelRotation.X0_Y0);
	}

	private ResourceLocation getPartialModelLocation(String name) {
		return new ResourceLocation(Create.ID, "item/" + basePath + "/" + name);
	}

	public BakedModel getPartial(String name) {
		return partials.get(name);
	}

}
