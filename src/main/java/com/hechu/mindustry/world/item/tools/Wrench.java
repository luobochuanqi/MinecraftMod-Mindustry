package com.hechu.mindustry.world.item.tools;

import com.hechu.mindustry.MindustryConstants;
import com.hechu.mindustry.utils.Utils;
import com.hechu.mindustry.world.level.block.Equipment.PowerNodeBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.util.NBTHelper;

import java.util.List;

/**
 * @author luobochuanqi
 */
public class Wrench extends ModItem {
    public static final String NAME = "wrench";
    private static final String NBT_KEY = "PowerNodeData";

    public Wrench(Properties builder) {
        super(builder);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos clickedPos = pContext.getClickedPos();
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(clickedPos);
        ItemStack itemStack = pContext.getItemInHand();
        NBTHelper tag = NBTHelper.of(itemStack).get() == null ? NBTHelper.create() : NBTHelper.of(itemStack);
        if (pContext.getLevel().isClientSide()) {

            return InteractionResult.SUCCESS;
        }
        if (!(blockEntity instanceof PowerNodeBlockEntity powerNodeBlockEntityDst)) {
            tag.remove(NBT_KEY);
            itemStack.setTag(tag.get());
            Utils.chatSendInfo(pContext.getPlayer(), "clear_select_block");
            return InteractionResult.SUCCESS;
        }
        // 如果shift被按下
        if (Screen.hasShiftDown()) {
            tag.setPos(NBT_KEY, clickedPos);
            itemStack.setTag(tag.get());
            Utils.chatSendInfo(pContext.getPlayer(), "select_block", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
            return InteractionResult.SUCCESS;
        }
        if (!(tag.get().contains(NBT_KEY))) {
            return InteractionResult.SUCCESS;
        }

        // powerNodeBlockEntitySrc 连接 powerNodeBlockEntityDst
        PowerNodeBlockEntity powerNodeBlockEntitySrc = (PowerNodeBlockEntity) pContext.getLevel().getBlockEntity(tag.getPos(NBT_KEY));

        // 提前检查是否在连接范围内
        double distance = powerNodeBlockEntitySrc.distanceTo(powerNodeBlockEntityDst);
        if (distance > powerNodeBlockEntitySrc.getPowerRange()) {
            Utils.chatSendWarn(pContext.getPlayer(), "out_of_range");
//            MindustryConstants.logger.debug(String.valueOf(distance));
            return InteractionResult.PASS;
        }

        // 提前检查是否超出可连接数
        int connectedNodesCount = powerNodeBlockEntityDst.getConnectedNodes().size();
        if (connectedNodesCount >= powerNodeBlockEntitySrc.getMaxConnections()) {
            Utils.chatSendWarn(pContext.getPlayer(), "max_connections_reached");
            return InteractionResult.PASS;
        }

        if (powerNodeBlockEntitySrc == powerNodeBlockEntityDst) {
            Utils.chatSendWarn(pContext.getPlayer(), "link_self");
            return InteractionResult.PASS;
        }

        // 如果点击的节点是已经被连接的，那么就取消连接
        if (powerNodeBlockEntitySrc.getConnectedNodes().contains(powerNodeBlockEntityDst.getBlockPos())
                && powerNodeBlockEntityDst.getPassivelyConnectedNodes().contains(powerNodeBlockEntitySrc.getBlockPos())) {
            powerNodeBlockEntitySrc.removeConnectedNode(powerNodeBlockEntityDst.getBlockPos());
            powerNodeBlockEntityDst.removePassivelyConnectedNode(powerNodeBlockEntitySrc.getBlockPos());
            Utils.chatSendInfo(pContext.getPlayer(), "disconnected");
            return InteractionResult.SUCCESS;
        } else {
            powerNodeBlockEntityDst.connectFromOtherNode(powerNodeBlockEntitySrc.getBlockPos());
            powerNodeBlockEntitySrc.connectToOtherNode(powerNodeBlockEntityDst.getBlockPos());

            Utils.chatSendInfo(pContext.getPlayer(), "connected");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        Tag tag = pPlayer.getItemInHand(pUsedHand).getTag();
        if (tag != null) {
            MindustryConstants.logger.debug("yes: " + tag.toString());
        } else {
            MindustryConstants.logger.debug("no tag");
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (tooltip.isEmpty()) {
            return;
        }
        String key = stack.getDescriptionId() + ".tip";
        BlockPos pos = NBTHelper.of(stack).getPos(NBT_KEY);
        if (NBTHelper.of(stack).get() == null || pos == null) {
            tooltip.add(Component.translatable(key, 0, 0, 0));
            return;
        }
        tooltip.add(Component.translatable(key, pos.getX(), pos.getY(), pos.getZ()));
    }
}
