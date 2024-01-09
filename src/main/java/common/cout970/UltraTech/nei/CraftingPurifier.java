package common.cout970.UltraTech.nei;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import ultratech.api.recipes.Cutter_Recipe;
import ultratech.api.recipes.Purifier_Recipe;
import ultratech.api.recipes.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;

public class CraftingPurifier extends TemplateRecipeHandler {

	List<Purifier_Recipe> recipes = new ArrayList<Purifier_Recipe>();

	@Override
	public String getRecipeName() {
		return "Purifier";
	}

	@Override
	public String getGuiTexture() {
		return "nei:textures/gui/purifier.png";
	}
	
	@Override
	public void loadTransferRects() {

		transferRects.add(new RecipeTransferRect(new Rectangle(80, 20, 24, 15), getRecipesID()));
	}

	private String getRecipesID() {
		return "purifier";
	}

	
	
	@Override
    public void loadCraftingRecipes(String outputId, Object... results) {
    
        if (outputId.equals(getRecipesID())) {
            for (Purifier_Recipe recipe : RecipeRegistry.purifier)
                recipes.add(recipe);
        }  else super.loadCraftingRecipes(outputId, results);
    }

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		for(int x = 0; x < RecipeRegistry.purifier.size();x++ ){
			if(NEIUltraTechConfig.matches(result, RecipeRegistry.purifier.get(x).getResult())){
				recipes.add(RecipeRegistry.purifier.get(x));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient){
		for(int x = 0; x < RecipeRegistry.purifier.size();x++ ){
			if(NEIUltraTechConfig.matches(ingredient, RecipeRegistry.purifier.get(x).getInput(0))){
				recipes.add(RecipeRegistry.purifier.get(x));
			}
		}
	}
	@Override
	public PositionedStack getResultStack(int recipe)
	{
		return new PositionedStack(recipes.get(recipe).getResult(),112,21);
	}
	@Override
	public List<PositionedStack> getOtherStacks(int recipe)
	{
		return new ArrayList<PositionedStack>();
	}
	@Override
	public List<PositionedStack> getIngredientStacks(int recipe)
	{
		List<PositionedStack> need = new ArrayList<PositionedStack>();
		need.add(new PositionedStack(recipes.get(recipe).getInput(0), 56,21));
		return need;
	}

	@Override
	public int numRecipes()
	{
		return recipes.size();
	}
	@Override
	public void drawExtras(int recipe)
	{
		int ticks = 100;
		int ticks2 = 500;
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("ultratech:textures/misc/energy.png"));
		this.drawProgressBar(9, 4, 0, 0, 25, 50, 1f-(cycleticks % ticks2 / (float)ticks2), 3);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("nei:textures/gui/purifier.png"));
		this.drawProgressBar(80, 20, 176, 14, 24, 16, 1f-(cycleticks % ticks / (float)ticks), 4);
	}

}
