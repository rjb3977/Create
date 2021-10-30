package com.simibubi.create.foundation.data;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.simibubi.create.lib.event.InstanceRegistrationCallback;
import com.jozufozu.flywheel.backend.instancing.tile.ITileInstanceFactory;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.TileEntityBuilder;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.fabricmc.api.EnvType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Function;

public class CreateTileEntityBuilder<T extends BlockEntity, P> extends TileEntityBuilder<T, P> {

	@Nullable
	private NonNullSupplier<ITileInstanceFactory<? super T>> instanceFactory;

	public static <T extends BlockEntity, P> TileEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent,
		String name, BuilderCallback callback, TileEntityBuilder.BlockEntityFactory<T> factory) {
		return new CreateTileEntityBuilder<T, P>(owner, parent, name, callback, factory);
	}

	protected CreateTileEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback,
									  TileEntityBuilder.BlockEntityFactory<T> factory) {
		super(owner, parent, name, callback, factory);

	}

	public CreateTileEntityBuilder<T, P> instance(NonNullSupplier<ITileInstanceFactory<? super T>> instanceFactory) {
		this.instanceFactory = instanceFactory;
		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> InstanceRegistrationCallback.EVENT.register(this::registerInstance));
		return this;
	}

	protected void registerInstance() {
		if (instanceFactory != null) {
			InstancedRenderRegistry.getInstance().tile(getEntry())
					.factory(instanceFactory.get());
		}
	}
}
