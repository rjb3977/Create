package com.simibubi.create.content.curiosities.weapons;

import javax.annotation.Nullable;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.particle.AirParticleData;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class PotatoProjectileEntity extends AbstractHurtingProjectile implements IEntityAdditionalSpawnData {

	ItemStack stack = ItemStack.EMPTY;
	PotatoCannonProjectileTypes type;

	public PotatoProjectileEntity(EntityType<? extends AbstractHurtingProjectile> type, Level world) {
		super(type, world);
	}

	public ItemStack getItem() {
		return stack;
	}

	public void setItem(ItemStack stack) {
		this.stack = stack;
	}

	public PotatoCannonProjectileTypes getProjectileType() {
		if (type == null)
			type = PotatoCannonProjectileTypes.getProjectileTypeOf(stack)
				.orElse(PotatoCannonProjectileTypes.FALLBACK);
		return type;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		stack = ItemStack.of(nbt.getCompound("Item"));
		super.readAdditionalSaveData(nbt);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		nbt.put("Item", stack.serializeNBT());
		super.addAdditionalSaveData(nbt);
	}

	public void tick() {
		PotatoCannonProjectileTypes projectileType = getProjectileType();
		setDeltaMovement(getDeltaMovement().add(0, -.05 * projectileType.getGravityMultiplier(), 0)
			.scale(projectileType.getDrag()));
		super.tick();
	}

	@Override
	protected float getInertia() {
		return 1;
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		return new AirParticleData(1, 10);
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	protected void onHitEntity(EntityHitResult ray) {
		super.onHitEntity(ray);

		Vec3 hit = ray.getLocation();
		Entity target = ray.getEntity();
		PotatoCannonProjectileTypes projectileType = getProjectileType();
		int damage = projectileType.getDamage();
		float knockback = projectileType.getKnockback();
		Entity owner = this.getOwner();

		if (!target.isAlive())
			return;
		if (owner instanceof LivingEntity)
			((LivingEntity) owner).setLastHurtMob(target);
		if (target instanceof PotatoProjectileEntity && tickCount < 10 && target.tickCount < 10)
			return;

		pop(hit);

		boolean targetIsEnderman = target.getType() == EntityType.ENDERMAN;
		int k = target.getRemainingFireTicks();
		if (this.isOnFire() && !targetIsEnderman)
			target.setSecondsOnFire(5);

		if (!target.hurt(causePotatoDamage(), (float) damage)) {
			target.setRemainingFireTicks(k);
			remove();
			return;
		}

		if (targetIsEnderman)
			return;

		projectileType.onEntityHit(ray);

		if (!(target instanceof LivingEntity)) {
			playHitSound(level, position());
			remove();
			return;
		}

		LivingEntity livingentity = (LivingEntity) target;

		if (type.getReloadTicks() < 10)
			livingentity.invulnerableTime = type.getReloadTicks() + 10;

		if (knockback > 0) {
			Vec3 appliedMotion = this.getDeltaMovement()
				.multiply(1.0D, 0.0D, 1.0D)
				.normalize()
				.scale(knockback * 0.6);
			if (appliedMotion.lengthSqr() > 0.0D)
				livingentity.push(appliedMotion.x, 0.1D, appliedMotion.z);
		}

		boolean onServer = !level.isClientSide;
		if (onServer && owner instanceof LivingEntity) {
			EnchantmentHelper.doPostHurtEffects(livingentity, owner);
			EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingentity);
		}

		if (owner != null && livingentity != owner && livingentity instanceof Player
			&& owner instanceof ServerPlayer && !this.isSilent()) {
			((ServerPlayer) owner).connection
				.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
		}

		if (onServer && owner instanceof ServerPlayer) {
			ServerPlayer serverplayerentity = (ServerPlayer) owner;
			if (!target.isAlive() && target.getType()
				.getCategory() == MobCategory.MONSTER
				|| (target instanceof Player && target != owner))
				AllTriggers.POTATO_KILL.trigger(serverplayerentity);
		}

		remove();
	}

	public static void playHitSound(Level world, Vec3 location) {
		AllSoundEvents.POTATO_HIT.playOnServer(world, new BlockPos(location));
	}

	public static void playLaunchSound(Level world, Vec3 location, float pitch) {
		AllSoundEvents.FWOOMP.playAt(world, location, 1, pitch, true);
	}

	@Override
	protected void onHitBlock(BlockHitResult ray) {
		Vec3 hit = ray.getLocation();
		pop(hit);
		getProjectileType().onBlockHit(level, ray);
		super.onHitBlock(ray);
		remove();
	}

	@Override
	public boolean hurt(DamageSource source, float amt) {
		if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE)
			return false;
		if (this.isInvulnerableTo(source))
			return false;
		pop(position());
		remove();
		return true;
	}

	private void pop(Vec3 hit) {
		if (!stack.isEmpty()) {
			for (int i = 0; i < 7; i++) {
				Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, this.random, .25f);
				level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), hit.x, hit.y, hit.z, m.x, m.y, m.z);
			}
		}
		if (!level.isClientSide)
			playHitSound(level, position());
	}

	private DamageSource causePotatoDamage() {
		return new PotatoDamageSource(this, getOwner()).setProjectile();
	}

	public static class PotatoDamageSource extends IndirectEntityDamageSource {

		public PotatoDamageSource(Entity source, @Nullable Entity trueSource) {
			super("create.potato_cannon", source, trueSource);
		}

	}

	@SuppressWarnings("unchecked")
	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		EntityType.Builder<PotatoProjectileEntity> entityBuilder = (EntityType.Builder<PotatoProjectileEntity>) builder;
		return entityBuilder.sized(.25f, .25f);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		CompoundTag compound = new CompoundTag();
		addAdditionalSaveData(compound);
		buffer.writeNbt(compound);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		readAdditionalSaveData(additionalData.readNbt());
	}

}
