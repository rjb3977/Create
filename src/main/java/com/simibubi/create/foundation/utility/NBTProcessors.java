package com.simibubi.create.foundation.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.logistics.item.filter.FilterItem;
import com.simibubi.create.lib.utility.Constants;

public final class NBTProcessors {

	private static final Map<BlockEntityType<?>, UnaryOperator<CompoundTag>> processors = new HashMap<>();
	private static final Map<BlockEntityType<?>, UnaryOperator<CompoundTag>> survivalProcessors = new HashMap<>();

	public static synchronized void addProcessor(BlockEntityType<?> type, UnaryOperator<CompoundTag> processor) {
		processors.put(type, processor);
	}

	public static synchronized void addSurvivalProcessor(BlockEntityType<?> type, UnaryOperator<CompoundTag> processor) {
		survivalProcessors.put(type, processor);
	}

	static {
		addProcessor(BlockEntityType.SIGN, data -> {
			for (int i = 0; i < 4; ++i) {
				if (textComponentHasClickEvent(data.getString("Text" + (i + 1))))
					return null;
			}
			return data;
		});
		addProcessor(BlockEntityType.LECTERN, data -> {
			if (!data.contains("Book", Constants.NBT.TAG_COMPOUND))
				return data;
			CompoundTag book = data.getCompound("Book");

			if (!book.contains("tag", Constants.NBT.TAG_COMPOUND))
				return data;
			CompoundTag tag = book.getCompound("tag");

			if (!tag.contains("pages", Constants.NBT.TAG_LIST))
				return data;
			ListTag pages = tag.getList("pages", Constants.NBT.TAG_STRING);

			for (Tag inbt : pages) {
				if (textComponentHasClickEvent(inbt.getAsString()))
					return null;
			}
			return data;
		});
		addSurvivalProcessor(AllTileEntities.FUNNEL.get(), data -> {
			if (data.contains("Filter")) {
				ItemStack filter = ItemStack.of(data.getCompound("Filter"));
				if (filter.getItem() instanceof FilterItem)
					data.remove("Filter");
			}
			return data;
		});
	}

	public static boolean textComponentHasClickEvent(String json) {
		Component component = Component.Serializer.fromJson(json.isEmpty() ? "\"\"" : json);
		return component != null && component.getStyle() != null && component.getStyle().getClickEvent() != null;
	}

	private NBTProcessors() {
	}

	@Nullable
	public static CompoundTag process(BlockEntity tileEntity, CompoundTag compound, boolean survival) {
		if (compound == null)
			return null;
		BlockEntityType<?> type = tileEntity.getType();
		if (survival && survivalProcessors.containsKey(type))
			compound = survivalProcessors.get(type)
				.apply(compound);
		if (compound != null && processors.containsKey(type))
			return processors.get(type)
				.apply(compound);
		if (tileEntity instanceof SpawnerBlockEntity)
			return compound;
		if (tileEntity.onlyOpCanSetNbt())
			return null;
		return compound;
	}

}
