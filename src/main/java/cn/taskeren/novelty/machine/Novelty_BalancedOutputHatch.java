package cn.taskeren.novelty.machine;

import cn.taskeren.novelty.NoveltyMod;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output;
import gregtech.api.util.GT_Utility;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import static gregtech.api.enums.GT_Values.TIER_COLORS;

public class Novelty_BalancedOutputHatch extends GT_MetaTileEntity_Hatch_Output {

	private final String[] description;

	public Novelty_BalancedOutputHatch(int aID, String aName, String aNameRegional, int aTier) {
		super(aID, aName, aNameRegional, aTier);

		description = new String[]{
			"Fluid Output for Multiblocks",
			"Capacity: " + TIER_COLORS[aTier] + GT_Utility.formatNumbers(getCapacity()) + "L",
			"Right click with screwdriver to restrict output",
			"Can be restricted to put out Items and/or Steam/No Steam/1 specific Fluid",
			"Restricted Output Hatches are given priority for Multiblock Fluid Output",
			EnumChatFormatting.BLUE + "Balanced Output Hatches will keep half amount of fluids always.",
			NoveltyMod.PRESENTED_BY
		};
	}

	public Novelty_BalancedOutputHatch(String aName, int aTier, String[] aDescriptionArray, ITexture[][][] aTextures) {
		super(aName, aTier, aDescriptionArray, aTextures);

		description = new String[]{
			"Fluid Output for Multiblocks",
			"Capacity: " + TIER_COLORS[aTier] + GT_Utility.formatNumbers(getCapacity()) + "L",
			"Right click with screwdriver to restrict output",
			"Can be restricted to put out Items and/or Steam/No Steam/1 specific Fluid",
			"Restricted Output Hatches are given priority for Multiblock Fluid Output",
			EnumChatFormatting.BLUE + "Balanced Output Hatches will keep half amount of fluids always.",
			NoveltyMod.PRESENTED_BY
		};
	}

	@Override
	public String[] getDescription() {
		return description;
	}

	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new Novelty_BalancedOutputHatch(mName, mTier, mDescriptionArray, mTextures);
	}

	public int getDrainableAmount() {
		if(getDrainableStack() == null || !canTankBeEmptied()) return 0;
		var capacity = getCapacity();
		var drainable = getDrainableStack();

		var halfCapacity = capacity / 2;

		return Math.max(0, drainable.amount - halfCapacity);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (getDrainableStack() == null || !canTankBeEmptied()) return null;
		if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) {
			setDrainableStack(null);
			getBaseMetaTileEntity().markDirty();
			return null;
		}

		if(getDrainableAmount() <= 0) return null;

		int used = Math.min(getDrainableAmount(), maxDrain);
		if (getDrainableStack().amount < used) used = getDrainableStack().amount;

		if (doDrain) {
			getDrainableStack().amount -= used;
			getBaseMetaTileEntity().markDirty();
		}

		FluidStack drained = getDrainableStack().copy();
		drained.amount = used;

		if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) {
			setDrainableStack(null);
			getBaseMetaTileEntity().markDirty();
		}

		return drained;
	}
}
