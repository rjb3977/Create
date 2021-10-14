package com.simibubi.create.lib.helper;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import com.simibubi.create.lib.extensions.ItemExtensions;

public class ItemSupplierHelper {
	public static Supplier<Item> getSupplier(Item item) {
		return ((ItemExtensions) item).create$getSupplier();
	}
}
