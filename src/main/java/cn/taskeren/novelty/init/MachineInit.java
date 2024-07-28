package cn.taskeren.novelty.init;

import cn.taskeren.novelty.NoveltyMod;
import cn.taskeren.novelty.machine.Novelty_InfinityEnergyHatch;
import cn.taskeren.novelty.machine.Novelty_UniHatch;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_RecipeBuilder;
import net.minecraft.item.ItemStack;

public class MachineInit {

	public static ItemStack UniHatch_ULV;
	public static ItemStack UniHatch_LV;
	public static ItemStack UniHatch_MV;
	public static ItemStack UniHatch_HV;
	public static ItemStack UniHatch_EV;

	public static ItemStack InfinityEnergyHatch;

	public static void initMachines() {
		NoveltyMod.LOG.info("Register machines with ids start from {}", NoveltyId.peek());

		UniHatch_ULV = new Novelty_UniHatch(
			NoveltyId.take(),
			"hatch.uni_hatch.tier.0",
			"UniHatch ULV",
			0
		).getStackForm(1);

		UniHatch_LV = new Novelty_UniHatch(
			NoveltyId.take(),
			"hatch.uni_hatch.tier.1",
			"UniHatch LV",
			1
		).getStackForm(1);

		UniHatch_MV = new Novelty_UniHatch(
			NoveltyId.take(),
			"hatch.uni_hatch.tier.2",
			"UniHatch MV",
			2
		).getStackForm(1);

		UniHatch_HV = new Novelty_UniHatch(
			NoveltyId.take(),
			"hatch.uni_hatch.tier.3",
			"UniHatch HV",
			3
		).getStackForm(1);

		UniHatch_EV = new Novelty_UniHatch(
			NoveltyId.take(),
			"hatch.uni_hatch.tier.4",
			"UniHatch EV",
			4
		).getStackForm(1);

		// preserved for ongoing Balanced Output Hatches
		NoveltyId.skip(3);

		InfinityEnergyHatch = new Novelty_InfinityEnergyHatch(
			NoveltyId.take(),
			"hatch.infinity_energy_hatch",
			"Ascendant Realm Paracausal Manipulating Unit",
			14
		).getStackForm(1);
	}

	public static void initMachineRecipes() {
		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_ULV.get(1), ItemList.Hatch_Input_Bus_ULV.get(1), ItemList.Hatch_Input_ULV.get(1))
			.fluidInputs(Materials.Glue.getFluid(2 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_ULV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_ULV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_ULV.get(1), ItemList.Hatch_Input_Bus_ULV.get(1), ItemList.Hatch_Input_ULV.get(1))
			.fluidInputs(Materials.AdvancedGlue.getFluid(GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_ULV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_ULV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_LV.get(1), ItemList.Hatch_Input_Bus_LV.get(1), ItemList.Hatch_Input_LV.get(1))
			.fluidInputs(Materials.AdvancedGlue.getFluid(4 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_LV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_LV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_LV.get(1), ItemList.Hatch_Input_Bus_LV.get(1), ItemList.Hatch_Input_LV.get(1))
			.fluidInputs(Materials.Plastic.getMolten(2 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_LV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_LV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_MV.get(1), ItemList.Hatch_Input_Bus_MV.get(1), ItemList.Hatch_Input_MV.get(1))
			.fluidInputs(Materials.Plastic.getMolten(16 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_MV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_MV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_MV.get(1), ItemList.Hatch_Input_Bus_MV.get(1), ItemList.Hatch_Input_MV.get(1))
			.fluidInputs(Materials.PolyvinylChloride.getMolten(12 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_MV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_MV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_HV.get(1), ItemList.Hatch_Input_Bus_HV.get(1), ItemList.Hatch_Input_HV.get(1))
			.fluidInputs(Materials.PolyvinylChloride.getMolten(24 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_HV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_HV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_HV.get(1), ItemList.Hatch_Input_Bus_HV.get(1), ItemList.Hatch_Input_HV.get(1))
			.fluidInputs(Materials.Polytetrafluoroethylene.getMolten(6 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_HV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_HV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_EV.get(1), ItemList.Hatch_Input_Bus_HV.get(1), ItemList.Hatch_Input_EV.get(1))
			.fluidInputs(Materials.Polytetrafluoroethylene.getMolten(32 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_EV)
			.duration(16 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_EV)
			.addTo(RecipeMaps.assemblerRecipes);

		GT_Values.RA.stdBuilder()
			.itemInputs(ItemList.Hull_EV.get(1), ItemList.Hatch_Input_Bus_HV.get(1), ItemList.Hatch_Input_EV.get(1))
			.fluidInputs(Materials.Polybenzimidazole.getMolten(12 * GT_RecipeBuilder.INGOTS))
			.itemOutputs(UniHatch_EV)
			.duration(12 * GT_RecipeBuilder.SECONDS)
			.eut(TierEU.RECIPE_EV)
			.addTo(RecipeMaps.assemblerRecipes);
	}

}
