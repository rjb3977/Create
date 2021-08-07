package com.simibubi.create.content.contraptions.processing.burner;

import java.util.Random;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LitBlazeBurnerBlock extends Block implements BlockPickInteractionAware {

// 	1.16: add a soul fire variant

//	public enum FlameType implements IStringSerializable {
//		REGULAR, SOULFIRE;
//
//		@Override
//		public String getName() {
//			return Lang.asId(name());
//		}
//
//	}

	public LitBlazeBurnerBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
		BlockHitResult blockRayTraceResult) {
		ItemStack heldItem = player.getItemInHand(hand);

		// Check for 'Shovels'
		if (!heldItem.isCorrectToolForDrops(Blocks.SNOW.defaultBlockState()))
			return InteractionResult.PASS;

		world.playSound(player, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, .5f, 2);

		if (world.isClientSide)
			return InteractionResult.SUCCESS;
		if (!player.isCreative())
			heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

		world.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return AllBlocks.BLAZE_BURNER.get()
			.getShape(state, reader, pos, context);
	}

	@Override
	public ItemStack getPickedStack(BlockState state, BlockGetter world, BlockPos pos, Player player, HitResult target) {
		return AllItems.EMPTY_BLAZE_BURNER.asStack();
	}

	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState p_180655_1_, Level world, BlockPos pos, Random random) {
		world.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, true,
			(double) pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1),
			(double) pos.getY() + random.nextDouble() + random.nextDouble(),
			(double) pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D,
			0.07D, 0.0D);

		if (random.nextInt(10) == 0) {
			world.playLocalSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
				(double) ((float) pos.getZ() + 0.5F), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
				0.25F + random.nextFloat() * .25f, random.nextFloat() * 0.7F + 0.6F, false);
		}

		if (random.nextInt(5) == 0) {
			for (int i = 0; i < random.nextInt(1) + 1; ++i) {
				world.addParticle(ParticleTypes.LAVA, (double) ((float) pos.getX() + 0.5F),
					(double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F),
					(double) (random.nextFloat() / 2.0F), 5.0E-5D, (double) (random.nextFloat() / 2.0F));
			}
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
		return 1;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos,
		CollisionContext context) {
		return AllBlocks.BLAZE_BURNER.get()
			.getCollisionShape(state, reader, pos, context);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

}
