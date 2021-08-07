package com.simibubi.create.content.curiosities.armor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.simibubi.create.foundation.config.AllConfigs;

import com.simibubi.create.lib.item.CustomDurabilityBarItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

public class CopperBacktankItem extends CopperArmorItem implements CustomDurabilityBarItem {

	public static final int DURABILITY_BAR = 0xefefef;
	public static final int RECHARGES_PER_TICK = 4;
	private BlockItem blockItem;

	public CopperBacktankItem(Properties p_i48534_3_, BlockItem blockItem) {
		super(EquipmentSlot.CHEST, p_i48534_3_);
		this.blockItem = blockItem;
	}

	@Override
	public InteractionResult useOn(UseOnContext p_195939_1_) {
		return blockItem.useOn(p_195939_1_);
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return DURABILITY_BAR;
	}

	@Override
	public void fillItemCategory(CreativeModeTab p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
		if (!allowdedIn(p_150895_1_))
			return;

		ItemStack stack = new ItemStack(this);
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("Air", AllConfigs.SERVER.curiosities.maxAirInBacktank.get());
		stack.setTag(nbt);
		p_150895_2_.add(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - Mth
			.clamp(getRemainingAir(stack) / ((float) AllConfigs.SERVER.curiosities.maxAirInBacktank.get()), 0, 1);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	public static int getRemainingAir(ItemStack stack) {
		CompoundTag orCreateTag = stack.getOrCreateTag();
		return orCreateTag.getInt("Air");
	}

	@SubscribeEvent
	public static void rechargePneumaticTools(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		if (event.phase != TickEvent.Phase.START)
			return;
		if (event.side != LogicalSide.SERVER)
			return;
		if (player.isSpectator())
			return;
		ItemStack tankStack = BackTankUtil.get(player);
		if (tankStack.isEmpty())
			return;

		Inventory inv = player.inventory;

		List<ItemStack> toCharge = Streams.concat(Stream.of(player.getMainHandItem()), inv.offhand.stream(),
			inv.armor.stream(), inv.items.stream())
			.filter(s -> s.getItem() instanceof IBackTankRechargeable && s.isDamaged())
			.collect(Collectors.toList());

		int charges = RECHARGES_PER_TICK;
		for (ItemStack stack : toCharge) {
			while (stack.isDamaged()) {
				if (BackTankUtil.canAbsorbDamage(event.player, ((IBackTankRechargeable) stack.getItem()).maxUses())) {
					stack.setDamageValue(stack.getDamageValue() - 1);
					charges--;
					if (charges <= 0)
						return;
				} else {
					return;
				}
			}
		}

	}
}
