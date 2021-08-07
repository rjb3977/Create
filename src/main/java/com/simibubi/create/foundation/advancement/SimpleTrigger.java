package com.simibubi.create.foundation.advancement;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.JsonObject;

import com.simibubi.create.lib.annotation.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleTrigger extends CriterionTriggerBase<SimpleTrigger.Instance> implements ITriggerable {

	public SimpleTrigger(String id) {
		super(id);
	}

	@Override
	public com.simibubi.create.foundation.advancement.SimpleTrigger.Instance createInstance(JsonObject json, DeserializationContext context) {
		return new com.simibubi.create.foundation.advancement.SimpleTrigger.Instance(getId());
	}

	public void trigger(ServerPlayer player) {
		super.trigger(player, null);
	}

	public com.simibubi.create.foundation.advancement.SimpleTrigger.Instance instance() {
		return new com.simibubi.create.foundation.advancement.SimpleTrigger.Instance(getId());
	}

	public static class Instance extends CriterionTriggerBase.Instance {

		public Instance(ResourceLocation idIn) {
			super(idIn, EntityPredicate.Composite.ANY); // FIXME: Is this right?
		}

		@Override
		protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
			return true;
		}
	}
}
