package cn.taskeren.novelty.machine;

import cn.taskeren.novelty.NoveltyMod;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_InputBus;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static gregtech.api.enums.GT_Values.TIER_COLORS;
import static gregtech.api.enums.GT_Values.VN;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER;

public class Novelty_UniHatch extends GT_MetaTileEntity_Hatch_InputBus
	implements IDualInputHatch, IAddGregtechLogo, IConfigurationCircuitSupport, IAddUIWidgets {

	// tier     - ULV     LV      MV      HV       EV
	// total    - 1x2(2)  2x2(4)  3x3(9)  4x4(16)  4x9(36)
	// items    - 1x1(1)  1x2(2)  2x3(6)  2x4(8)   2x9(18)
	// fluids   - 1x1(1)  1x2(2)  1x3(3)  2x4(8)   2x9(18)
	// capacity - 8B      16B     64B     128B     1024B

	// slots for items per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_ITEM_TIER = new int[]{1, 2, 6, 8, 18};
	// slots for fluids per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_FLUID_TIER = new int[]{1, 2, 3, 8, 18};
	// capacity of fluids per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_FLUID_CAPACITY_TIER = new int[]{8_000, 16_000, 64_000, 128_000, 1_024_000};

	// slots shown per row
	public static final int[] UI_SLOTS_PER_ROW = new int[]{1, 2, 3, 4, 9};

	@Nullable
	public final FluidStack[] fluidStacks;

	public final FluidStackTank[] fluidStackTanks;

	public final UniInventory uniInventory = new UniInventory(this);

	public static class UniInventory implements IDualInputInventory {
		private final Novelty_UniHatch self;

		public UniInventory(Novelty_UniHatch self) {
			this.self = self;
		}

		@Override
		public ItemStack[] getItemInputs() {
			return Arrays.stream(self.mInventory).filter(Objects::nonNull).toArray(ItemStack[]::new);
		}

		@Override
		public FluidStack[] getFluidInputs() {
			return Arrays.stream(self.fluidStacks).filter(Objects::nonNull).toArray(FluidStack[]::new);
		}
	}

	public Novelty_UniHatch(int aID, String aName, String aNameRegional, int aTier) {
		super(
			aID,
			aName,
			aNameRegional,
			aTier,
			getItemSlots(aTier) + 1,
			new String[]{
				"Advanced input for Multiblocks",
				"Tier: " + TIER_COLORS[aTier] + VN[aTier],
				"Item/fluid slots: " + TIER_COLORS[aTier] + getItemSlots(aTier) + EnumChatFormatting.GRAY +
					"/" + TIER_COLORS[aTier] + getFluidSlots(aTier),
				"Fluid capacity: " + TIER_COLORS[aTier] + GT_Utility.formatNumbers(getFluidCapacity(aTier)) + "L",
				"Can be used as both Input Bus and Input Hatch",
				NoveltyMod.PRESENTED_BY,
			}
		);

		this.fluidStacks = new FluidStack[getFluidSlots(aTier)];
		this.fluidStackTanks = new FluidStackTank[getFluidSlots(aTier)];
	}

	public Novelty_UniHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(aName, aTier, getItemSlots(aTier) + 1, aDescription, aTextures);

		this.fluidStacks = new FluidStack[getFluidSlots(aTier)];
		this.fluidStackTanks = new FluidStackTank[getFluidSlots(aTier)];

		for(int i = 0; i < getFluidSlots(aTier); i++) {
			final int index = i;
			fluidStackTanks[i] = new FluidStackTank(
				() -> fluidStacks[index],
				fluid -> fluidStacks[index] = fluid,
				getFluidCapacity(aTier)
			);
		}
	}

	public static int getItemSlots(int tier) {
		return UNI_HATCH_MAX_ITEM_TIER[Math.min(tier, 4)];
	}

	public static int getFluidSlots(int tier) {
		return UNI_HATCH_MAX_FLUID_TIER[Math.min(tier, 4)];
	}

	public static int getSlotsPerRow(int tier) {
		return UI_SLOTS_PER_ROW[Math.min(tier, 4)];
	}

	public static int getFluidCapacity(int tier) {
		return UNI_HATCH_MAX_FLUID_CAPACITY_TIER[Math.min(tier, 4)];
	}

	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new Novelty_UniHatch(mName, mTier, mDescriptionArray, mTextures);
	}

	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return getTexturesInactive(aBaseTexture);
	}

	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		// TODO: use other textures instead of me crafting input buffer
		return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_ME_CRAFTING_INPUT_BUFFER)};
	}

	@Override
	public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		onPreTickFluid(aBaseMetaTileEntity, aTick);

		super.onPreTick(aBaseMetaTileEntity, aTick);
	}

	@Override
	public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
		onPostTickFluid(aBaseMetaTileEntity, aTimer);

		super.onPostTick(aBaseMetaTileEntity, aTimer);
	}

	@Override
	public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
		return super.onRightclick(aBaseMetaTileEntity, aPlayer);
	}

	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
		super.saveNBTData(aNBT);

		NBTTagList itemsList = new NBTTagList();
		for(ItemStack itemStack : mInventory) {
			if(itemStack != null) {
				var itemTag = new NBTTagCompound();
				itemStack.writeToNBT(itemTag);
				itemsList.appendTag(itemTag);
			}
		}

		NBTTagList fluidsList = new NBTTagList();
		for(FluidStack fluidStack : fluidStacks) {
			if(fluidStack != null) {
				var fluidTag = new NBTTagCompound();
				fluidStack.writeToNBT(fluidTag);
				fluidsList.appendTag(fluidTag);
			}
		}

		aNBT.setTag("Items", itemsList);
		aNBT.setTag("Fluids", fluidsList);
	}

	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		super.loadNBTData(aNBT);

		var itemsList = aNBT.getTagList("Items", 10);
		for(int i = 0; i < itemsList.tagCount(); i++) {
			var itemTag = itemsList.getCompoundTagAt(i);
			mInventory[i] = ItemStack.loadItemStackFromNBT(itemTag);
		}

		var fluidsList = aNBT.getTagList("Fluids", 10);
		for(int i = 0; i < fluidsList.tagCount(); i++) {
			var fluidTag = fluidsList.getCompoundTagAt(i);
			fluidStacks[i] = FluidStack.loadFluidStackFromNBT(fluidTag);
		}
	}

	@Override
	public boolean isGivingInformation() {
		return true;
	}

	@Override
	public String[] getInfoData() {
		var ret = new ArrayList<String>();

		for(int i = 0; i < mInventory.length; i++) {
			var item = mInventory[i];
			if(item != null) {
				ret.add("Slot " + i + " " + EnumChatFormatting.BLUE + item.getItem().getItemStackDisplayName(item) + EnumChatFormatting.RESET);
			}
		}

		for(int i = 0; i < fluidStacks.length; i++) {
			var fluid = fluidStacks[i];
			if(fluid != null) {
				ret.add("Slot " + i + " " + EnumChatFormatting.BLUE + fluid.getFluid().getLocalizedName(fluid) + EnumChatFormatting.RESET);
			}
		}

		return ret.toArray(new String[0]);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////                   GUI                                                                                      ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getGUIWidth() {
		return super.getGUIWidth() + 18;
	}

	@Override
	public int getCircuitSlot() {
		return getItemSlots(mTier);
	}

	@Override
	public int getCircuitSlotX() {
		return 171;
	}

	@Override
	public int getCircuitSlotY() {
		return 60;
	}

	/**
	 * Convert Array of {@code fluidStackTanks} to List.
	 */
	private List<IFluidTank> getFluidTanks() {
		return Arrays.asList(fluidStackTanks);
	}

	@Override
	public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
		builder.widget(
			SlotGroup.ofItemHandler(getInventoryHandler(), getSlotsPerRow(mTier))
				.endAtSlot(getItemSlots(mTier) - 1)
				.background(getGUITextureSet().getItemSlot())
				.build()
				.setPos(7, 5)
		);
		builder.widget(
			SlotGroup.ofFluidTanks(getFluidTanks(), getSlotsPerRow(mTier))
				.background(getGUITextureSet().getFluidSlot())
				.build()
				.setPos(7, mTier > 1 ? 41 : 23)
		);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////                   DUAL HATCH                                                                               ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean justUpdated() {
		return true;
	}

	@Override
	public Iterator<? extends IDualInputInventory> inventories() {
		return Stream.of(uniInventory).iterator();
	}

	@Override
	public Optional<IDualInputInventory> getFirstNonEmptyInventory() {
		return Optional.of(uniInventory);
	}

	@Override
	public boolean supportsFluids() {
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////                   FLUIDS  -  GT_MetaTileEntity_Hatch_Input                                                 ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean doesEmptyContainers() {
		return true;
	}

	@Override
	public boolean canTankBeFilled() {
		return true;
	}

	@Override
	public boolean canTankBeEmptied() {
		return true;
	}

	@Override
	public boolean displaysItemStack() {
		return true;
	}

	@Override
	public boolean isFluidInputAllowed(FluidStack aFluid) {
		return mRecipeMap == null || mRecipeMap.containsInput(aFluid);
	}

	@Override
	public int getTankPressure() {
		return -100;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////                   FLUIDS  -  GT_MetaTileEntity_Hatch_MultiInput                                            ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean displaysStackSize() {
		return true;
	}

	public FluidStack[] getStoredFluid() {
		return fluidStacks;
	}

	/**
	 * Returns the maximum types of fluid slots.
	 * <p>
	 * The original name is {@code getMaxType()}.
	 */
	public int getMaxFluidTypes() {
		return getFluidSlots(mTier);
	}

	@Override
	public FluidStack getFluid() {
		for(FluidStack fluidStack : fluidStacks) {
			if(fluidStack != null && fluidStack.amount > 0) {
				return fluidStack;
			}
		}
		return null;
	}

	public FluidStack getFluid(int aSlot) {
		if(fluidStacks == null || aSlot < 0 || aSlot >= getMaxFluidTypes()) return null;
		return fluidStacks[aSlot];
	}

	@Override
	public int getFluidAmount() {
		var fluid = getFluid();
		return fluid != null ? fluid.amount : 0;
	}

	/**
	 * The fluid capacity per slot.
	 */
	@Override
	public int getCapacity() {
		return getFluidCapacity(mTier);
	}

	/**
	 * The first empty fluid slot.
	 * <p>
	 * The original name is {@code getFirstEmptySlot()}.
	 */
	public int getFirstEmptyFluidSlot() {
		for(int i = 0; i < fluidStacks.length; i++) {
			if(fluidStacks[i] == null) return i;
		}
		return -1;
	}

	public boolean hasFluid(FluidStack aFluid) {
		if(aFluid == null) return false;
		for(FluidStack fluidStack : fluidStacks) {
			if(aFluid.isFluidEqual(fluidStack)) return true;
		}
		return false;
	}

	public int getFluidSlot(FluidStack tFluid) {
		if(tFluid == null) return -1;
		for(int i = 0; i < fluidStacks.length; i++) {
			if(tFluid.isFluidEqual(fluidStacks[i])) return i;
		}
		return -1;
	}

	public int getFluidAmount(FluidStack tFluid) {
		int tSlot = getFluidSlot(tFluid);
		if(tSlot != -1) {
			//noinspection DataFlowIssue - tSlot != -1 can assert that the fluid is not null
			return fluidStacks[tSlot].amount;
		}
		return 0;
	}

	public void setFluid(FluidStack aFluid, int aSlot) {
		if(aSlot < 0 || aSlot >= getMaxFluidTypes()) return;
		fluidStacks[aSlot] = aFluid;
	}

	public void addFluid(FluidStack aFluid, int aSlot) {
		if(aSlot < 0 || aSlot >= getMaxFluidTypes()) return;
		if(aFluid.equals(fluidStacks[aSlot])) fluidStacks[aSlot].amount += aFluid.amount;
		if(fluidStacks[aSlot] == null) fluidStacks[aSlot] = aFluid.copy();
	}

	protected void onPreTickFluid(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		if(aBaseMetaTileEntity.isServerSide()) {
			mFluid = getFluid();
		}
	}

	@Override
	public int fill(FluidStack aFluid, boolean doFill) {
		if(aFluid == null ||
			aFluid.getFluid().getID() <= 0 ||
			aFluid.amount <= 0 ||
			!canTankBeFilled() ||
			!isFluidInputAllowed(aFluid)
		) return 0;

		if(!hasFluid(aFluid) && getFirstEmptyFluidSlot() != -1) {
			int tFilled = Math.min(aFluid.amount, getCapacity());
			if(doFill) {
				FluidStack tFluid = aFluid.copy();
				tFluid.amount = tFilled;
				addFluid(tFluid, getFirstEmptyFluidSlot());
				getBaseMetaTileEntity().markDirty();
			}
			return tFilled;
		}

		if(hasFluid(aFluid)) {
			int tLeft = getCapacity() - getFluidAmount(aFluid);
			int tFilled = Math.min(tLeft, aFluid.amount);
			if(doFill) {
				FluidStack tFluid = aFluid.copy();
				tFluid.amount = tFilled;
				addFluid(tFluid, getFluidSlot(tFluid));
				getBaseMetaTileEntity().markDirty();
			}
			return tFilled;
		}

		return 0;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(getFluid() == null || !canTankBeEmptied()) return null;
		if(getFluid().amount <= 0 && isFluidChangingAllowed()) {
			setFluid(null, getFluidSlot(getFluid()));
			getBaseMetaTileEntity().markDirty();
			return null;
		}
		FluidStack tRemove = getFluid().copy();
		tRemove.amount = Math.min(maxDrain, tRemove.amount);
		if(doDrain) {
			getFluid().amount -= tRemove.amount;
			getBaseMetaTileEntity().markDirty();
		}
		if(getFluid() == null || getFluid().amount <= 0 && isFluidChangingAllowed()) {
			setFluid(null, getFluidSlot(getFluid()));
			getBaseMetaTileEntity().markDirty();
		}
		return tRemove;
	}

	@Override
	public int fill(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		return fill(aFluid, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
		if(aFluid == null || !hasFluid(aFluid)) return null;
		FluidStack tStored = fluidStacks[getFluidSlot(aFluid)];
		if(tStored.amount <= 0 && isFluidChangingAllowed()) {
			setFluid(null, getFluidSlot(aFluid));
			getBaseMetaTileEntity().markDirty();
			return null;
		}
		FluidStack tRemove = tStored.copy();
		tRemove.amount = Math.min(aFluid.amount, tRemove.amount);
		if(doDrain) {
			tStored.amount -= tRemove.amount;
			getBaseMetaTileEntity().markDirty();
		}
		if(tStored.amount <= 0 && isFluidChangingAllowed()) {
			setFluid(null, getFluidSlot(aFluid));
			getBaseMetaTileEntity().markDirty();
		}
		return tRemove;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		FluidTankInfo[] infos = new FluidTankInfo[getMaxFluidTypes()];
		for(int i = 0; i < getMaxFluidTypes(); i++) {
			infos[i] = new FluidTankInfo(fluidStacks[i], getCapacity());
		}
		return infos;
	}

	protected void onPostTickFluid(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		if(aBaseMetaTileEntity.isServerSide() && fluidStacks != null) {
			for(int i = 0; i < getMaxFluidTypes(); i++) {
				if(fluidStacks[i] != null && fluidStacks[i].amount <= 0) {
					fluidStacks[i] = null;
				}
			}
		}
	}
}
