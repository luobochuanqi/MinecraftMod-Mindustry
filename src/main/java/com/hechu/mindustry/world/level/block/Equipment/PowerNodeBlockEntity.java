package com.hechu.mindustry.world.level.block.Equipment;

import com.google.common.collect.Lists;
import com.hechu.mindustry.kiwi.BlockEntityModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import snownee.kiwi.block.entity.ModBlockEntity;

import java.util.List;

/**
 * @author luobochuanqi
 */
public class PowerNodeBlockEntity extends ModBlockEntity {
    public static final String NAME = "power_node";
    /**
     * A list of beam segments for this PowerNode.
     */
    List<PowerNodeBeamSection> beamSections = Lists.newArrayList();
    private int lastCheckY;
    private List<PowerNodeBeamSection> checkingBeamSections = Lists.newArrayList();
    public boolean isLinking = false;
    List<BlockPos> connectedNodes = Lists.newArrayList();
    List<BlockPos> passivelyConnectedNodes = Lists.newArrayList();

    public PowerNodeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityModule.POWER_NODE_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PowerNodeBlockEntity pBlockEntity) {

        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();

        BlockPos blockpos;
        if (pBlockEntity.lastCheckY < j) {
            blockpos = pPos;
            pBlockEntity.checkingBeamSections = Lists.newArrayList();
            pBlockEntity.lastCheckY = pPos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, pBlockEntity.lastCheckY + 1, k);
        }

        // 检查 checkingBeamSections 是否为空，如果不为空，则获取最后一个 PowerNodeBeamSection
        PowerNodeBeamSection powerNodeBeamSection = pBlockEntity.checkingBeamSections.isEmpty() ? null : pBlockEntity.checkingBeamSections.get(pBlockEntity.checkingBeamSections.size() - 1);
        // 获取世界表面高度
        int l = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        // 在高度范围内迭代,最多十次
        for (int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (pBlockEntity.checkingBeamSections.size() <= 1) {
                powerNodeBeamSection = new PowerNodeBeamSection();
                pBlockEntity.checkingBeamSections.add(powerNodeBeamSection);
            } else if (powerNodeBeamSection != null) {
                powerNodeBeamSection.increaseHeight();
            }
            powerNodeBeamSection.increaseHeight();

            blockpos = blockpos.above();
            ++pBlockEntity.lastCheckY;
        }

        if (pBlockEntity.lastCheckY >= l) {
            pBlockEntity.lastCheckY = pLevel.getMinBuildHeight() - 1;
            pBlockEntity.beamSections = pBlockEntity.checkingBeamSections;
        }
    }

    public int getMaxConnections() {
        return 10;
    }

    public int getPowerRange() {
        return 6;
    }

    public List<PowerNodeBeamSection> getBeamSections() {
        return (List<PowerNodeBeamSection>) this.beamSections;
    }

    public List<BlockPos> getConnectedNodes() {
        return this.connectedNodes;
    }

    public List<BlockPos> getPassivelyConnectedNodes() {
        return this.passivelyConnectedNodes;
    }

    public void connectToOtherNode(BlockPos pBlockPos) {
        this.connectedNodes.add(pBlockPos);
        this.refresh();
    }

    public void connectFromOtherNode(BlockPos pBlockPos) {
        this.passivelyConnectedNodes.add(pBlockPos);
        this.refresh();
    }

    public boolean removeConnectedNode(BlockPos pBlockPos) {
        boolean removed = this.getConnectedNodes().remove(pBlockPos);
        if (removed) {
            this.refresh();
        }
        return removed;
    }

    public boolean removePassivelyConnectedNode(BlockPos pBlockPos) {
        boolean removed = this.getPassivelyConnectedNodes().remove(pBlockPos);
        if (removed) {
            this.refresh();
        }
        return removed;
    }

    /**
     * @return 此BlockEntity到pBlockEntity的双精度距离
     */
    public double distanceTo(PowerNodeBlockEntity pBlockEntity) {
        BlockPos pos1 = this.getBlockPos();
        BlockPos pos2 = pBlockEntity.getBlockPos();

        double dx = pos1.getX() - pos2.getX();
        double dy = pos1.getY() - pos2.getY();
        double dz = pos1.getZ() - pos2.getZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void load(CompoundTag pTag) {
        readPacketData(pTag);
        super.load(pTag);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        writePacketData(pTag);
        super.saveAdditional(pTag);
    }

    @Override
    protected void readPacketData(CompoundTag compoundTag) {
        ListTag nodeList = compoundTag.getList("nodes", 10);
        ListTag passivelyNodeList = compoundTag.getList("passively_nodes", 10);
        this.connectedNodes.clear();
        this.passivelyConnectedNodes.clear();
        for (int i = 0; i < nodeList.size(); i++) {
            CompoundTag nodeTag = nodeList.getCompound(i);
            int x = nodeTag.getInt("x");
            int y = nodeTag.getInt("y");
            int z = nodeTag.getInt("z");
            this.connectedNodes.add(new BlockPos(x, y, z));
        }
        for (int i = 0; i < passivelyNodeList.size(); i++) {
            CompoundTag nodeTag = passivelyNodeList.getCompound(i);
            int x = nodeTag.getInt("x");
            int y = nodeTag.getInt("y");
            int z = nodeTag.getInt("z");
            this.passivelyConnectedNodes.add(new BlockPos(x, y, z));
        }
    }

    @NotNull
    @Override
    protected CompoundTag writePacketData(CompoundTag compoundTag) {
        ListTag nodeList = new ListTag();
        ListTag passivelyNodeList = new ListTag();
        for (BlockPos nodePos : this.connectedNodes) {
            CompoundTag nodeTag = new CompoundTag();
            nodeTag.putInt("x", nodePos.getX());
            nodeTag.putInt("y", nodePos.getY());
            nodeTag.putInt("z", nodePos.getZ());
            nodeList.add(nodeTag);
        }
        for (BlockPos passivelyNodePos : this.passivelyConnectedNodes) {
            CompoundTag nodeTag = new CompoundTag();
            nodeTag.putInt("x", passivelyNodePos.getX());
            nodeTag.putInt("y", passivelyNodePos.getY());
            nodeTag.putInt("z", passivelyNodePos.getZ());
            passivelyNodeList.add(nodeTag);
        }
        compoundTag.put("nodes", nodeList);
        compoundTag.put("passively_nodes", passivelyNodeList);
        return compoundTag;
    }

    public static class PowerNodeBeamSection {
        private int height;

        public PowerNodeBeamSection() {
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
