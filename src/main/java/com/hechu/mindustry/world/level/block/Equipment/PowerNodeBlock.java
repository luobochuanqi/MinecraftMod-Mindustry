package com.hechu.mindustry.world.level.block.Equipment;

import com.hechu.mindustry.MindustryConstants;
import com.hechu.mindustry.kiwi.BlockEntityModule;
import com.hechu.mindustry.utils.capabilities.MindustryCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author luobochuanqi
 */
public class PowerNodeBlock extends BaseEntityBlock {
    public static final String NAME = "power_node";

    public PowerNodeBlock() {
        super(Properties.of().destroyTime(3).strength(3.0F, 3.0F));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        PowerNodeBlockEntity powerNodeBlockEntity = new PowerNodeBlockEntity(pPos, pState);
        powerNodeBlockEntity.getCapability(MindustryCapabilities.HEALTH_HANDLER, null).ifPresent(healthHandler -> {
            healthHandler.setMaxHealth(40);
            healthHandler.setHealth(40);
        });
        return powerNodeBlockEntity;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityModule.POWER_NODE_BLOCK_ENTITY.get(), PowerNodeBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            PowerNodeBlockEntity powerNodeBlockEntity = (PowerNodeBlockEntity) pLevel.getBlockEntity(pPos);
            MindustryConstants.logger.debug("to:" + powerNodeBlockEntity.getConnectedNodes().toString());
            MindustryConstants.logger.debug("from:" + powerNodeBlockEntity.getPassivelyConnectedNodes().toString());
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        PowerNodeBlockEntity powerNodeBlockEntity = (PowerNodeBlockEntity) pLevel.getBlockEntity(pPos);
        List<BlockPos> passivelyConnectedNodes = powerNodeBlockEntity.getPassivelyConnectedNodes();
        List<BlockPos> connectedNodes = powerNodeBlockEntity.getConnectedNodes();
        for (BlockPos nodePos : passivelyConnectedNodes) {
            PowerNodeBlockEntity blockEntity = (PowerNodeBlockEntity) pLevel.getBlockEntity(nodePos);
            blockEntity.removeConnectedNode(pPos);
        }
        for (BlockPos nodePos : connectedNodes) {
            PowerNodeBlockEntity blockEntity = (PowerNodeBlockEntity) pLevel.getBlockEntity(nodePos);
            blockEntity.removePassivelyConnectedNode(pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
