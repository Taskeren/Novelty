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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static gregtech.api.enums.GT_Values.TIER_COLORS;
import static gregtech.api.enums.GT_Values.VN;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER;

public class Novelty_UniHatch
	extends GT_MetaTileEntity_Hatch_InputBus
	implements IDualInputHatch, IAddGregtechLogo, IConfigurationCircuitSupport, IAddUIWidgets {

	// tier   - ULV     LV      MV      HV       EV
	// total  - 1x2(2)  2x2(4)  3x3(9)  4x4(16)  4x9(36)
	// items  - 1x1(1)  1x2(2)  2x3(6)  2x4(8)   2x9(18)
	// fluids - 1x1(1)  1x2(2)  1x3(3)  2x4(8)   2x9(18)

	// slots for items per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_ITEM_TIER = new int[]{1, 2, 6, 8, 18};
	// slots for fluids per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_FLUID_TIER = new int[]{1, 2, 3, 8, 18};
	// capacity of fluids per tier (0-4, ULV to EV)
	public static final int[] UNI_HATCH_MAX_FLUID_CAPACITY_TIER = new int[] {8_000, 16_000, 64_000, 128_000, 1_024_000};

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
			return self.mInventory;
		}

		@Override
		public FluidStack[] getFluidInputs() {
			return self.fluidStacks;
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

		disableSort = true;
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

		disableSort = true;
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
	public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
		super.onPostTick(aBaseMetaTileEntity, aTimer);

		if(getBaseMetaTileEntity().isServerSide()) {
			// do something
		}
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

	public List<IFluidTank> getFluidTanks() {
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
}
