package com.simibubi.create.content.schematics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;

public class SchematicWorldEntityGetter implements LevelEntityGetter<Entity> {

	public List<Entity> entities;

	public SchematicWorldEntityGetter() {
		this.entities = new ArrayList<>();
	}

	@Nullable
	@Override
	public Entity get(int p_156931_) {
		return entities.get(p_156931_);
	}

	@Nullable
	@Override
	public Entity get(UUID pUuid) {
		for (Entity entity : entities) {
			if(entity.getUUID() == pUuid) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public Iterable<Entity> getAll() {
		return entities;
	}

	@Override
	public <U extends Entity> void get(EntityTypeTest<Entity, U> test, Consumer<U> consumer) {
		getAll().forEach(entity -> {
			U entity2 = test.tryCast(entity);
			if (entity2 != null) {
				consumer.accept(entity2);
			}
		});

	}

	@Override
	public void get(AABB box, Consumer<Entity> consumer) {
		getAll().forEach(entity -> {
			if (entity.getBoundingBox().intersects(box)) {
				consumer.accept(entity);
			}
		});
	}

	@Override
	public <U extends Entity> void get(EntityTypeTest<Entity, U> test, AABB box, Consumer<U> consumer) {
		getAll().forEach(entity -> {
			U entity2 = test.tryCast(entity);
			if (entity2 != null) {
				if (entity2.getBoundingBox().intersects(box)) {
					consumer.accept(entity2);
				}
			}
		});
	}
}
