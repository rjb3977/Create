package com.simibubi.create.lib.mixin.accessor;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.Entry.class)
public interface AbstractList$AbstractListEntryAccessor<E extends AbstractSelectionList.Entry<E>> {
	@Accessor("list")
	AbstractSelectionList<E> getList();
}
