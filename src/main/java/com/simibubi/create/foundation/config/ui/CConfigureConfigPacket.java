package com.simibubi.create.foundation.config.ui;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.lib.config.ConfigValue;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class CConfigureConfigPacket<T> implements C2SPacket {

	private String path;
	private String value;

	protected CConfigureConfigPacket() {}

	public CConfigureConfigPacket(String path, T value) {
		this.path = path;
		this.value = serialize(value);
	}

	@Override
	public void read(FriendlyByteBuf buffer) {
		this.modID = buffer.readUtf(32767);
		this.path = buffer.readUtf(32767);
		this.value = buffer.readUtf(32767);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(modID);
		buffer.writeUtf(path);
		buffer.writeUtf(value);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, SimpleChannel.ResponseTarget responseTarget) {
		if (sender == null || !sender.hasPermissions(2))
			return;

//		ForgeConfigSpec.ValueSpec valueSpec = AllConfigs.SERVER.specification.getRaw(path);
		ConfigValue<T> configValue = (ConfigValue<T>) AllConfigs.SERVER.getConfig().get(path);

		T v = (T) deserialize(configValue.get(), value);
		if (!configValue.fitsConstraint(v))
			return;

		configValue.set(v);

	}

	public String serialize(T value) {
		if (value instanceof Boolean)
			return Boolean.toString((Boolean) value);
		if (value instanceof Enum<?>)
			return ((Enum<?>) value).name();
		if (value instanceof Integer)
			return Integer.toString((Integer) value);
		if (value instanceof Float)
			return Float.toString((Float) value);
		if (value instanceof Double)
			return Double.toString((Double) value);

		throw new IllegalArgumentException("unknown type " + value + ": " + value.getClass().getSimpleName());
	}

	public static Object deserialize(Object type, String sValue) {
		if (type instanceof Boolean)
			return Boolean.parseBoolean(sValue);
		if (type instanceof Enum<?>)
			return Enum.valueOf(((Enum<?>) type).getClass(), sValue);
		if (type instanceof Integer)
			return Integer.parseInt(sValue);
		if (type instanceof Float)
			return Float.parseFloat(sValue);
		if (type instanceof Double)
			return Double.parseDouble(sValue);

		throw new IllegalArgumentException("unknown type " + type + ": " + type.getClass().getSimpleName());
	}
}
