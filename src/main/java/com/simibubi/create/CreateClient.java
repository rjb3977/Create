package com.simibubi.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.fabric.event.FlywheelEvents;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.contraptions.components.structureMovement.render.SBBContraptionManager;
import com.simibubi.create.content.contraptions.relays.encased.CasingConnectivity;
import com.simibubi.create.content.curiosities.armor.CopperBacktankArmorLayer;
import com.simibubi.create.content.curiosities.bell.SoulPulseEffectHandler;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonRenderHandler;
import com.simibubi.create.content.curiosities.zapper.ZapperRenderHandler;
import com.simibubi.create.content.schematics.ClientSchematicLoader;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import com.simibubi.create.events.ClientEvents;
import com.simibubi.create.events.InputEvents;
import com.simibubi.create.foundation.ClientResourceReloadListener;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.item.render.CustomItemModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItems;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.ponder.content.PonderIndex;
import com.simibubi.create.foundation.ponder.elements.WorldSectionElement;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.render.AllProgramSpecs;
import com.simibubi.create.foundation.render.CreateContexts;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.utility.ModelSwapper;
import com.simibubi.create.foundation.utility.ghost.GhostBlocks;
import com.simibubi.create.foundation.utility.outliner.Outliner;
import com.simibubi.create.lib.event.ModelsBakedCallback;
import com.simibubi.create.lib.event.OnModelRegistryCallback;
import com.simibubi.create.lib.event.OnTextureStitchCallback;
import com.simibubi.create.lib.event.ParticleManagerRegistrationCallback;
import com.simibubi.create.lib.utility.SpecialModelUtil;
import com.simibubi.create.lib.utility.TextureStitchUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CreateClient implements ClientModInitializer {

	public static final SuperByteBufferCache BUFFER_CACHE = new SuperByteBufferCache();
	public static final Outliner OUTLINER = new Outliner();
	public static final GhostBlocks GHOST_BLOCKS = new GhostBlocks();
	public static final Screen EMPTY_SCREEN = new Screen(new TextComponent("")) {};
	public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
	public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();

	public static final ClientSchematicLoader SCHEMATIC_SENDER = new ClientSchematicLoader();
	public static final SchematicHandler SCHEMATIC_HANDLER = new SchematicHandler();
	public static final SchematicAndQuillHandler SCHEMATIC_AND_QUILL_HANDLER = new SchematicAndQuillHandler();

	public static final ZapperRenderHandler ZAPPER_RENDER_HANDLER = new ZapperRenderHandler();
	public static final PotatoCannonRenderHandler POTATO_CANNON_RENDER_HANDLER = new PotatoCannonRenderHandler();
	public static final SoulPulseEffectHandler SOUL_PULSE_EFFECT_HANDLER = new SoulPulseEffectHandler();

	public static final ClientResourceReloadListener RESOURCE_RELOAD_LISTENER = new ClientResourceReloadListener();

	public static void onCtorClient() {
//		modEventBus.addListener(CreateClient::clientInit);
//		modEventBus.addListener(CreateClient::onTextureStitch);
//		modEventBus.addListener(CreateClient::onModelRegistry);
//		modEventBus.addListener(CreateClient::onModelBake);
//		modEventBus.addListener(AllParticleTypes::registerFactories);
//		modEventBus.addListener(ClientEvents::loadCompleted);
//		modEventBus.addListener(CreateContexts::flwInit);
		FlywheelEvents.GATHER_CONTEXT.register(CreateContexts::flwInit);
//		modEventBus.addListener(AllMaterialSpecs::flwInit);
		FlywheelEvents.GATHER_CONTEXT.register(AllMaterialSpecs::flwInit);
//		modEventBus.addListener(ContraptionRenderDispatcher::gatherContext);
		FlywheelEvents.GATHER_CONTEXT.register(ContraptionRenderDispatcher::gatherContext);

		MODEL_SWAPPER.registerListeners(modEventBus);

		ZAPPER_RENDER_HANDLER.registerListeners();
		POTATO_CANNON_RENDER_HANDLER.registerListeners();

		Minecraft mc = Minecraft.getInstance();

		// null during datagen
		if (mc == null) return;

		IResourceManager resourceManager = mc.getResourceManager();
		if (resourceManager instanceof IReloadableResourceManager)
			((IReloadableResourceManager) resourceManager).registerReloadListener(RESOURCE_RELOAD_LISTENER);
	}

	@Override
	public void onInitializeClient() {
		BUFFER_CACHE.registerCompartment(KineticTileEntityRenderer.KINETIC_TILE);
		BUFFER_CACHE.registerCompartment(SBBContraptionManager.CONTRAPTION, 20);
		BUFFER_CACHE.registerCompartment(WorldSectionElement.DOC_WORLD_SECTION, 20);

		AllKeys.register();
		// AllFluids.assignRenderLayers();
		AllBlockPartials.init();
		AllStitchedTextures.init();

		PonderIndex.register();
		PonderIndex.registerTags();

		UIRenderHelper.init();

		ResourceManager resourceManager = Minecraft.getInstance()
				.getResourceManager();
		if (resourceManager instanceof ReloadableResourceManager)
			((ReloadableResourceManager) resourceManager).registerReloadListener(new ResourceReloadHandler());

		// fabric events
		ModelsBakedCallback.EVENT.register(CreateClient::onModelBake);
		OnModelRegistryCallback.EVENT.register(CreateClient::onModelRegistry);
		OnTextureStitchCallback.EVENT.register(CreateClient::onTextureStitch);
		ParticleManagerRegistrationCallback.EVENT.register(() -> {
			AllParticleTypes.registerFactories();
			registerLayerRenderers(Minecraft.getInstance().getEntityRenderDispatcher()); // multi-purpose!
		});
		addClientListeners();
		ClientEvents.register();
		InputEvents.register();
		AllPackets.clientInit();

//		});
		// Replaces ArmorItem#getArmorTexture from a Forge patch
//		ArmorRenderingRegistry.registerSimpleTexture(new ResourceLocation(Create.ID, "copper"),
//				AllItems.COPPER_BACKTANK.get(), AllItems.DIVING_HELMET.get(), AllItems.DIVING_BOOTS.get());

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer == null)
				return;
			new CopperBacktankArmorLayer<>((LivingEntityRenderer<?, ?>) entityRenderer);
		});
	}

	protected static void registerLayerRenderers(EntityRendererManager renderManager) {
		CopperBacktankArmorLayer.registerOnAll(renderManager);
	}

	public static void invalidateRenderers() {
		BUFFER_CACHE.invalidate();

		ContraptionRenderDispatcher.reset();
	}

	public static void checkGraphicsFanciness() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;

		if (mc.options.graphicsMode != GraphicsStatus.FABULOUS)
			return;

		if (AllConfigs.CLIENT.ignoreFabulousWarning.get())
			return;

		MutableComponent text = ComponentUtils.wrapInSquareBrackets(new TextComponent("WARN"))
			.withStyle(ChatFormatting.GOLD)
			.append(new TextComponent(
				" Some of Create's visual features will not be available while Fabulous graphics are enabled!"))
			.withStyle(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/create dismissFabulousWarning"))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new TextComponent("Click here to disable this warning"))));

		mc.gui.handleChat(ChatType.CHAT, text, mc.player.getUUID());
	}

}
