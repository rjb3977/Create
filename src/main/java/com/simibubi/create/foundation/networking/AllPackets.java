package com.simibubi.create.foundation.networking;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionDisassemblyPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionStallPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.gantry.GantryContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.GlueEffectPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ClientMotionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionFluidPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionInteractionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionSeatMappingPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.LimbSwingUpdatePacket;
import com.simibubi.create.content.contraptions.components.structureMovement.train.CouplingCreationPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.MinecartControllerUpdatePacket;
import com.simibubi.create.content.contraptions.fluids.actors.FluidSplashPacket;
import com.simibubi.create.content.contraptions.relays.advanced.sequencer.ConfigureSequencedGearshiftPacket;
import com.simibubi.create.content.curiosities.bell.SoulPulseEffectPacket;
import com.simibubi.create.content.curiosities.symmetry.ConfigureSymmetryWandPacket;
import com.simibubi.create.content.curiosities.symmetry.SymmetryEffectPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxDisposeAllPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.curiosities.tools.BlueprintAssignCompleteRecipePacket;
import com.simibubi.create.content.curiosities.tools.ExtendoGripInteractionPacket;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonPacket;
import com.simibubi.create.content.curiosities.weapons.PotatoProjectileTypeManager;
import com.simibubi.create.content.curiosities.zapper.ZapperBeamPacket;
import com.simibubi.create.content.curiosities.zapper.terrainzapper.ConfigureWorldshaperPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorElytraPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorPlacementPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorTriggerPacket;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmPlacementPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerBindPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerInputPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerStopLecternPacket;
import com.simibubi.create.content.logistics.item.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.packet.ConfigureFlexcratePacket;
import com.simibubi.create.content.logistics.packet.ConfigureStockswitchPacket;
import com.simibubi.create.content.logistics.packet.FunnelFlapPacket;
import com.simibubi.create.content.logistics.packet.TunnelFlapPacket;
import com.simibubi.create.content.schematics.packet.ConfigureSchematicannonPacket;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import com.simibubi.create.content.schematics.packet.SchematicSyncPacket;
import com.simibubi.create.content.schematics.packet.SchematicUploadPacket;
import com.simibubi.create.foundation.command.HighlightPacket;
import com.simibubi.create.foundation.command.SConfigureConfigPacket;
import com.simibubi.create.foundation.config.ui.CConfigureConfigPacket;
import com.simibubi.create.foundation.gui.ClearContainerPacket;
import com.simibubi.create.foundation.gui.GhostItemSubmitPacket;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringCountUpdatePacket;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueUpdatePacket;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.Packet;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public enum AllPackets {

	// Client to Server
	CONFIGURE_SCHEMATICANNON(ConfigureSchematicannonPacket.class),
	CONFIGURE_FLEXCRATE(ConfigureFlexcratePacket.class),
	CONFIGURE_STOCKSWITCH(ConfigureStockswitchPacket.class),
	CONFIGURE_SEQUENCER(ConfigureSequencedGearshiftPacket.class),
	PLACE_SCHEMATIC(SchematicPlacePacket.class),
	UPLOAD_SCHEMATIC(SchematicUploadPacket.class),
	CLEAR_CONTAINER(ClearContainerPacket.class),
	CONFIGURE_FILTER(FilterScreenPacket.class),
	CONFIGURE_FILTERING_AMOUNT(FilteringCountUpdatePacket.class),
	CONFIGURE_SCROLLABLE(ScrollValueUpdatePacket.class),
	EXTENDO_INTERACT(ExtendoGripInteractionPacket.class),
	CONTRAPTION_INTERACT(ContraptionInteractionPacket.class),
	CLIENT_MOTION(ClientMotionPacket.class),
	PLACE_ARM(ArmPlacementPacket.class),
	MINECART_COUPLING_CREATION(CouplingCreationPacket.class),
	INSTANT_SCHEMATIC(InstantSchematicPacket.class),
	SYNC_SCHEMATIC(SchematicSyncPacket.class),
	LEFT_CLICK(LeftClickPacket.class),
	PLACE_EJECTOR(EjectorPlacementPacket.class),
	TRIGGER_EJECTOR(EjectorTriggerPacket.class),
	EJECTOR_ELYTRA(EjectorElytraPacket.class),
	LINKED_CONTROLLER_INPUT(LinkedControllerInputPacket.class),
	LINKED_CONTROLLER_BIND(LinkedControllerBindPacket.class),
	LINKED_CONTROLLER_USE_LECTERN(LinkedControllerStopLecternPacket.class),
	C_CONFIGURE_CONFIG(CConfigureConfigPacket.class),
	SUBMIT_GHOST_ITEM(GhostItemSubmitPacket.class),
	BLUEPRINT_COMPLETE_RECIPE(BlueprintAssignCompleteRecipePacket.class),
	CONFIGURE_SYMMETRY_WAND(ConfigureSymmetryWandPacket.class),
	CONFIGURE_WORLDSHAPER(ConfigureWorldshaperPacket.class),
	TOOLBOX_EQUIP(ToolboxEquipPacket.class),
	TOOLBOX_DISPOSE_ALL(ToolboxDisposeAllPacket.class),

	// Server to Client
	SYMMETRY_EFFECT(SymmetryEffectPacket.class),
	SERVER_SPEED(ServerSpeedProvider.Packet.class),
	BEAM_EFFECT(ZapperBeamPacket.class),
	S_CONFIGURE_CONFIG(SConfigureConfigPacket.class),
	CONTRAPTION_STALL(ContraptionStallPacket.class),
	CONTRAPTION_DISASSEMBLE(ContraptionDisassemblyPacket.class),
	GLUE_EFFECT(GlueEffectPacket.class),
	CONTRAPTION_SEAT_MAPPING(ContraptionSeatMappingPacket.class),
	LIMBSWING_UPDATE(LimbSwingUpdatePacket.class),
	MINECART_CONTROLLER(MinecartControllerUpdatePacket.class),
	FLUID_SPLASH(FluidSplashPacket.class),
	CONTRAPTION_FLUID(ContraptionFluidPacket.class),
	GANTRY_UPDATE(GantryContraptionUpdatePacket.class),
	BLOCK_HIGHLIGHT(HighlightPacket.class),
	TUNNEL_FLAP(TunnelFlapPacket.class),
	FUNNEL_FLAP(FunnelFlapPacket.class),
	POTATO_CANNON(PotatoCannonPacket.class),
	SOUL_PULSE(SoulPulseEffectPacket.class),
	PERSISTENT_DATA(ISyncPersistentData.Packet.class),
	SYNC_POTATO_PROJECTILE_TYPES(PotatoProjectileTypeManager.SyncPacket.class)

	;

	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Create.ID, "network");
	public static final String NETWORK_VERSION = new ResourceLocation(Create.ID, "1").toString();
	public static SimpleChannel channel = new SimpleChannel(CHANNEL_NAME);

	private Class<? extends Packet> type;

	private <T extends Packet> AllPackets(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public static void registerPackets() {
		int id = 0;
		for (AllPackets packet : values()) {
			boolean registered = false;
			if (C2SPacket.class.isAssignableFrom(packet.type)) {
				channel.registerC2SPacket((Class<C2SPacket>) packet.type, id++);
				registered = true;
			}
			if (S2CPacket.class.isAssignableFrom(packet.type)) {
				channel.registerS2CPacket((Class<S2CPacket>) packet.type, id++);
				registered = true;
			}
			if (!registered) {
				Create.LOGGER.error("Could not register packet with type " + packet.type.getClass());
			}
		}
		channel.initServerListener();
	}

	public static void sendToNear(ServerLevel world, BlockPos pos, int range, S2CPacket message) {
		channel.sendToClientsAround(message, world, pos, range);
	}

	public static void clientInit() {
		channel.initClientListener();
	}
}
