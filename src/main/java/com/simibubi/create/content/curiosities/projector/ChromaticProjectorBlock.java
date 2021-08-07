package com.simibubi.create.content.curiosities.projector;

import javax.annotation.Nullable;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class ChromaticProjectorBlock extends Block implements ITE<ChromaticProjectorTileEntity> {
	public ChromaticProjectorBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
								  BlockHitResult hit) {
		ItemStack held = player.getMainHandItem();
		if (AllItems.WRENCH.isIn(held))
			return InteractionResult.PASS;

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> withTileEntityDo(worldIn, pos, te -> this.displayScreen(te, player)));
		return InteractionResult.SUCCESS;
	}

	@OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(ChromaticProjectorTileEntity te, Player player) {
		if (player instanceof LocalPlayer)
			ScreenOpener.open(new ChromaticProjectorScreen(te));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return null;//AllTileEntities.CHROMATIC_PROJECTOR.create();
	}

	@Override
	public Class<ChromaticProjectorTileEntity> getTileEntityClass() {
		return ChromaticProjectorTileEntity.class;
	}
}
