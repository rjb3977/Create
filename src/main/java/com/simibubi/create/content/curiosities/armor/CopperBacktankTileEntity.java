package com.simibubi.create.content.curiosities.armor;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.particle.AirParticleData;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CopperBacktankTileEntity extends KineticTileEntity implements Nameable {

	public int airLevel;
	public int airLevelTimer;
	private Component customName;

	public CopperBacktankTileEntity(BlockEntityType<?> typeIn) {
		super(typeIn);
	}

	@Override
	public void tick() {
		super.tick();
		if (getSpeed() == 0)
			return;
		if (airLevelTimer > 0) {
			airLevelTimer--;
			return;
		}

		int max = getMaxAir();
		if (level.isClientSide) {
			Vec3 centerOf = VecHelper.getCenterOf(worldPosition);
			Vec3 v = VecHelper.offsetRandomly(centerOf, Create.RANDOM, .65f);
			Vec3 m = centerOf.subtract(v);
			if (airLevel != max)
				level.addParticle(new AirParticleData(1, .05f), v.x, v.y, v.z, m.x, m.y, m.z);
			return;
		}

		if (airLevel == max)
			return;

		float abs = Math.abs(getSpeed());
		int increment = Mth.clamp(((int) abs - 100) / 20, 1, 5);
		airLevel = Math.min(max, airLevel + increment);
		if (airLevel == max)
			sendData();
		airLevelTimer = Mth.clamp((int) (128f - abs / 5f) - 108, 0, 20);
	}

	protected int getMaxAir() {
		return AllConfigs.SERVER.curiosities.maxAirInBacktank.get();
	}

	public int getAirLevel() {
		return airLevel;
	}

	public void setAirLevel(int airLevel) {
		this.airLevel = airLevel;
		sendData();
	}

	public void setCustomName(Component customName) {
		this.customName = customName;
	}

	public Component getCustomName() {
		return customName;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putInt("Air", airLevel);
		compound.putInt("Timer", airLevelTimer);
		if (this.customName != null)
			compound.putString("CustomName", Component.Serializer.toJson(this.customName));
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		int prev = airLevel;
		airLevel = compound.getInt("Air");
		airLevelTimer = compound.getInt("Timer");
		if (compound.contains("CustomName", 8))
			this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
		if (prev != 0 && prev != airLevel && airLevel == getMaxAir() && clientPacket)
			playFilledEffect();
	}

	protected void playFilledEffect() {
		AllSoundEvents.CONFIRM.playAt(level, worldPosition, 0.4f, 1, true);
		Vec3 baseMotion = new Vec3(.25, 0.1, 0);
		Vec3 baseVec = VecHelper.getCenterOf(worldPosition);
		for (int i = 0; i < 360; i += 10) {
			Vec3 m = VecHelper.rotate(baseMotion, i, Axis.Y);
			Vec3 v = baseVec.add(m.normalize()
				.scale(.25f));

			level.addParticle(ParticleTypes.SPIT, v.x, v.y, v.z, m.x, m.y, m.z);
		}
	}

	@Override
	public Component getName() {
		return this.customName != null ? this.customName
			: new TranslatableComponent(AllItems.COPPER_BACKTANK.get()
				.getDescriptionId());
	}

	@Override
	public boolean shouldRenderNormally() {
		return true;
	}

}
