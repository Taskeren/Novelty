package cn.taskeren.novelty.machine;

import cn.taskeren.novelty.NoveltyMod;
import com.github.technus.tectech.util.TT_Utility;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import gregtech.api.gui.modularui.GT_UIInfos;
import gregtech.api.gui.modularui.GT_UITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IWirelessEnergyHatchInformation;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Energy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import static com.github.technus.tectech.util.CommonValues.VN;
import static gregtech.api.enums.GT_Values.V;

public class Novelty_InfinityEnergyHatch extends GT_MetaTileEntity_Hatch_Energy implements IWirelessEnergyHatchInformation {

	private int EUT;
	private int AMP;

	public Novelty_InfinityEnergyHatch(int aID, String aName, String aNameRegional, int aTier) {
		super(aID, aName, aNameRegional, aTier, new String[]{
			"Energy Injector for Multiblocks",
			"Infinity EU, Customizable Output Volt and Amp",
			EnumChatFormatting.RED + "CREATIVE EXCLUSIVE",
			NoveltyMod.PRESENTED_BY
		});
	}

	public Novelty_InfinityEnergyHatch(String aName, int aTier, String[] aDescriptionArray, ITexture[][][] aTextures) {
		super(aName, aTier, aDescriptionArray, aTextures);
	}

	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new Novelty_InfinityEnergyHatch(mName, mTier, mDescriptionArray, mTextures);
	}

	public int getEUT() {
		return EUT;
	}

	public void setEUT(int EUT) {
		this.EUT = EUT;
	}

	public int getAMP() {
		return AMP;
	}

	public void setAMP(int AMP) {
		this.AMP = AMP;
	}

	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
		super.saveNBTData(aNBT);

		aNBT.setInteger("EUT", EUT);
		aNBT.setInteger("AMP", AMP);
	}

	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		super.loadNBTData(aNBT);

		EUT = aNBT.getInteger("EUT");
		AMP = aNBT.getInteger("AMP");
	}

	@Override
	public boolean isEnetInput() {
		return false;
	}

	@Override
	public long getMinimumStoredEU() {
		return 2 * V[EUT];
	}

	@Override
	public long maxEUInput() {
		return EUT;
	}

	@Override
	public long maxEUStore() {
		return totalStorage(EUT);
	}

	@Override
	public long maxAmperesIn() {
		return AMP;
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.WIRELESS;
	}

	@Override
	public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
		super.onFirstTick(aBaseMetaTileEntity);

		if(aBaseMetaTileEntity.isServerSide()) {
			doExtractVoidEnergy();
		}
	}

	@Override
	public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		super.onPreTick(aBaseMetaTileEntity, aTick);

		if(aBaseMetaTileEntity.isServerSide()) {
			if(aTick % ticks_between_energy_addition == 0L) {
				doExtractVoidEnergy();
			}
		}
	}

	public void doExtractVoidEnergy() {
		setEUVar(Long.MAX_VALUE);
	}

	//// GUI


	@Override
	public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
		GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
		return true;
	}

	@Override
	public boolean useModularUI() {
		return true;
	}

	@Override
	public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
		builder
			.widget(new DrawableWidget()
				.setDrawable(GT_UITextures.PICTURE_SCREEN_BLACK)
				.setSize(90, 72)
				.setPos(43, 4)
			)
			.widget(new TextWidget()
				.setStringSupplier(() -> "TIER: " + VN[TT_Utility.getTier(Math.abs(EUT))])
				.setDefaultColor(COLOR_TEXT_WHITE.get())
				.setPos(46, 22)
			)
			.widget(new TextWidget()
				.setStringSupplier(() -> "SUM: " + numberFormat.format((long) AMP * EUT))
				.setDefaultColor(COLOR_TEXT_WHITE.get())
				.setPos(46, 46)
			);

		addLabelledIntegerTextField(builder, "EUT: ", 24, this::getEUT, this::setEUT, 46, 8);
		addLabelledIntegerTextField(builder, "AMP: ", 24, this::getAMP, this::setAMP, 46, 34);

		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> EUT -= val, 512, 64, 7, 4);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> EUT /= val, 512, 64, 7, 22);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> AMP -= val, 512, 64, 7, 40);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> AMP /= val, 512, 64, 7, 58);

		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> EUT -= val, 16, 1, 25, 4);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> EUT /= val, 16, 2, 25, 22);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> AMP -= val, 16, 1, 25, 40);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> AMP /= val, 16, 2, 25, 58);

		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> EUT += val, 16, 1, 133, 4);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> EUT *= val, 16, 2, 133, 22);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> AMP += val, 16, 1, 133, 40);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> AMP *= val, 16, 2, 133, 58);

		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> EUT += val, 512, 64, 151, 4);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> EUT *= val, 512, 64, 151, 22);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> AMP += val, 512, 64, 151, 40);
		addChangeNumberButton(builder, GT_UITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> AMP *= val, 512, 64, 151, 58);
	}

	private void addLabelledIntegerTextField(
		ModularWindow.Builder builder,
		String label,
		int labelWidth,
		IntSupplier getter,
		IntConsumer setter,
		int xPos,
		int yPos
	) {
		builder
			.widget(new TextWidget(label)
				.setDefaultColor(COLOR_TEXT_WHITE.get())
				.setPos(xPos, yPos)
			)
			.widget(new NumericWidget()
				.setGetter(getter::getAsInt)
				.setSetter(val -> setter.accept((int) val))
				.setTextColor(COLOR_TEXT_WHITE.get())
				.setBackground(GT_UITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
				.setPos(xPos + labelWidth, yPos - 1)
				.setSize(56, 10)
			);
	}

	private void addChangeNumberButton(
		ModularWindow.Builder builder,
		IDrawable overlay,
		Consumer<Integer> setter,
		int changeNumberShift,
		int changeNumber,
		int xPos,
		int yPos
	) {
		builder
			.widget(new ButtonWidget()
				.setOnClick((clickData, widget) -> {
					setter.accept(clickData.shift ? changeNumberShift : changeNumber);
					// producing = (long) AMP * EUT >= 0;
				})
				.setBackground(GT_UITextures.BUTTON_STANDARD, overlay)
				.setSize(18, 18)
				.setPos(xPos, yPos)
			);
	}
}
