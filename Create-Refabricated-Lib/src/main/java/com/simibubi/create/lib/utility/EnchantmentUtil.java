package com.simibubi.create.lib.utility;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantmentUtil {
	private static final Map<Enchantment, Set<Item>> enchantsToItems = new HashMap<>();

	public static void addCompat(Item item, Enchantment... enchants) {
		for (Enchantment enchant : enchants) {
			Set<Item> items = enchantsToItems.get(enchant);
			if (items == null) {
				Set<Item> items2 = new HashSet<>();
				items2.add(item);
				enchantsToItems.put(enchant, items2);
			} else {
				items.add(item);
			}
		}
	}

	public static Map<Enchantment, Set<Item>> getMap() {
		return enchantsToItems;
	}
}
