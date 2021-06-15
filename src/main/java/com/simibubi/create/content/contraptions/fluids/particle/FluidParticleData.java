package com.simibubi.create.content.contraptions.fluids.particle;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.LongStream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.particle.ICustomParticleData;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;

public class FluidParticleData implements IParticleData, ICustomParticleData<FluidParticleData> {

	// oh god
	public static PrimitiveCodec<FluidAmount> FLUID_AMOUNT = new PrimitiveCodec<FluidAmount>() {
		@Override
		public <T> DataResult<FluidAmount> read(final DynamicOps<T> ops, final T input) {
			DataResult<LongStream> stream = ops.getLongStream(input);
			long[] longArray = stream.get().left().get().toArray();
			FluidAmount result = FluidAmount.of(longArray[0], longArray[1], longArray[2]);
			return DataResult.success(result);
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final FluidAmount value) {
			return ops.createLongList(LongStream.of(value.whole, value.numerator, value.denominator));
		}

		@Override
		public String toString() {
			return "FluidAmount";
		}
	};

	private ParticleType<FluidParticleData> type;
	private FluidVolume fluid;

	public FluidParticleData() {}

	@SuppressWarnings("unchecked")
	public FluidParticleData(ParticleType<?> type, FluidVolume fluid) {
		this.type = (ParticleType<FluidParticleData>) type;
		this.fluid = fluid;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public IParticleFactory<FluidParticleData> getFactory() {
		return (data, world, x, y, z, vx, vy, vz) -> FluidStackParticle.create(data.type, world, data.fluid, x, y, z,
			vx, vy, vz);
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}

	@Override
	public void write(PacketBuffer buffer) {
//		buffer.writeFluidStack(fluid);
	}

	@Override
	public String getParameters() {
		return Registry.PARTICLE_TYPE.getKey(type) + " " + Registry.FLUID.getKey(fluid.getRawFluid());
	}

	public static final Codec<FluidVolume> FLUID_CODEC = RecordCodecBuilder.create(i -> i.group(
		Registry.FLUID.fieldOf("FluidName")
			.forGetter(FluidVolume::getRawFluid),
		FLUID_AMOUNT.fieldOf("Amount")
			.forGetter(FluidVolume::getAmount_F),
		CompoundNBT.CODEC.optionalFieldOf("tag")
			.forGetter((fs) -> {
				return Optional.ofNullable(fs.toTag());
			}))
		.apply(i, (f, a, t) -> t.isPresent() ? FluidKeys.get(f).withAmount(a).fromTag(t.get()) : FluidKeys.get(f).withAmount(a)));

	public static final Codec<FluidParticleData> CODEC = RecordCodecBuilder.create(i -> i
		.group(FLUID_CODEC.fieldOf("fluid")
			.forGetter(p -> p.fluid))
		.apply(i, fs -> new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), fs)));

	public static final Codec<FluidParticleData> BASIN_CODEC = RecordCodecBuilder.create(i -> i
		.group(FLUID_CODEC.fieldOf("fluid")
			.forGetter(p -> p.fluid))
		.apply(i, fs -> new FluidParticleData(AllParticleTypes.BASIN_FLUID.get(), fs)));

	public static final Codec<FluidParticleData> DRIP_CODEC = RecordCodecBuilder.create(i -> i
		.group(FLUID_CODEC.fieldOf("fluid")
			.forGetter(p -> p.fluid))
		.apply(i, fs -> new FluidParticleData(AllParticleTypes.FLUID_DRIP.get(), fs)));

	public static final IParticleData.IDeserializer<FluidParticleData> DESERIALIZER =
		new IParticleData.IDeserializer<FluidParticleData>() {

			// TODO Fluid particles on command
			public FluidParticleData deserialize(ParticleType<FluidParticleData> particleTypeIn, StringReader reader)
				throws CommandSyntaxException {
				return new FluidParticleData(particleTypeIn, FluidKeys.WATER.withAmount(FluidAmount.ZERO));
			}

			public FluidParticleData read(ParticleType<FluidParticleData> particleTypeIn, PacketBuffer buffer) {
				FluidVolume volume = null;
				try {
					FluidVolume.fromMcBuffer(buffer);
				} catch (IOException e) {
					Create.LOGGER.fatal("Failed to read FluidVolume from packet!", e);
				}
				return new FluidParticleData(particleTypeIn, volume);
			}
		};

	@Override
	public IDeserializer<FluidParticleData> getDeserializer() {
		return DESERIALIZER;
	}

	@Override
	public Codec<FluidParticleData> getCodec(ParticleType<FluidParticleData> type) {
		if (type == AllParticleTypes.BASIN_FLUID.get())
			return BASIN_CODEC;
		if (type == AllParticleTypes.FLUID_DRIP.get())
			return DRIP_CODEC;
		return CODEC;
	}

}
