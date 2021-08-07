package com.simibubi.create.content.curiosities.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class CopperArmorItem extends ArmorItem {

	public CopperArmorItem(EquipmentSlot p_i48534_2_, Properties p_i48534_3_) {
		super(AllArmorMaterials.COPPER, p_i48534_2_, p_i48534_3_);
	}

	public boolean isWornBy(Entity entity) {
		for (ItemStack itemStack : entity.getArmorSlots())
			if (itemStack.getItem() == this)
				return true;
		return false;
	}

}
