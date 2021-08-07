package com.simibubi.create.content.contraptions.components.actors;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import com.mojang.authlib.GameProfile;

import com.simibubi.create.lib.helper.FakePlayerHelper;

public class PloughBlock extends AttachedActorBlock {

	public PloughBlock(Properties p_i48377_1_) {
		super(p_i48377_1_);
	}

	/**
	 * The OnHoeUse event takes a player, so we better not pass null
	 */
	static class PloughFakePlayer extends ServerPlayer {


		public static final GameProfile PLOUGH_PROFILE =
				new GameProfile(UUID.fromString("9e2faded-eeee-4ec2-c314-dad129ae971d"), "Plough");

		public PloughFakePlayer(ServerLevel world) {
			super(world.getServer(), world, PLOUGH_PROFILE, new ServerPlayerGameMode(world)); // this should work?
			FakePlayerHelper.setFake(this, true);
		}
	}
}
