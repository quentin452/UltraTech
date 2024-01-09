package common.cout970.UltraTech.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.fuels.IronEngineFuel.Fuel;
import ultratech.api.power.interfaces.IPower;
import ultratech.api.util.UT_Utils;
import common.cout970.UltraTech.TileEntities.electric.tiers.TileEntityFluidGeneratorT1;
import common.cout970.UltraTech.TileEntities.electric.tiers.TileEntityFluidGeneratorT2;
import common.cout970.UltraTech.util.power.PowerExchange;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidGenerator extends GuiContainer{

	public TileEntityFluidGeneratorT1 entity;

	public GuiFluidGenerator(Container par1Container, TileEntityFluidGeneratorT1 tile) {
		super(par1Container);
		entity = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.renderEngine.bindTexture(new ResourceLocation("ultratech:textures/gui/fluidgen.png"));
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

		//fluid
		FluidStack input = entity.getTankInfo(ForgeDirection.UP)[0].fluid;
		boolean w = input == null;
		if(!w){
			GuiBoiler.bindTexture(input);
			IIcon ic = input.getFluid().getStillIcon();
			int a = input.amount*40/entity.getTankInfo(ForgeDirection.UP)[0].capacity;
			this.drawTexturedModelRectFromIcon(xStart+139, yStart+61-a, ic, 18, a);
		}

		//overlay
		this.mc.renderEngine.bindTexture(new ResourceLocation("ultratech:textures/gui/fluidgen.png"));
		if(!w)this.drawTexturedModalRect(xStart+138, yStart+21, 224, 0, 20, 40);
		//energy
		this.mc.renderEngine.bindTexture(new ResourceLocation("ultratech:textures/misc/energy.png"));
		int p = (int) (entity.getCharge()*50/entity.getCapacity());
		this.drawTexturedModalRect(xStart+14, yStart+15+(50-p), 0, 0, 25, p);
		if(!w){
			String name = ""+input.getFluid().getName();
			this.drawCenteredString(fontRendererObj, name, xStart+90, yStart+26, UT_Utils.RGBtoInt(255, 255, 255));
			String amount = "Amount: "+input.amount;
			this.drawCenteredString(fontRendererObj, amount, xStart+90, yStart+37, UT_Utils.RGBtoInt(255, 255, 255));
			Fuel fu = IronEngineFuel.getFuelForFluid(input.getFluid());
			if(fu != null){
				int m = 2;
				if(entity instanceof TileEntityFluidGeneratorT2)m = 4;
				String prod = "Production: "+PowerExchange.FTtoQP(fu.powerPerCycle)*m;
				this.drawCenteredString(fontRendererObj, prod, xStart+90, yStart+48, UT_Utils.RGBtoInt(255, 255, 255));
			}
		}
		
		this.drawCenteredString(fontRendererObj, "Fluid Generator", xStart+85, yStart+6, UT_Utils.RGBtoInt(255, 255, 255));
		//text
		if(UT_Utils.isIn(i, j, xStart+14, yStart+15, 25, 50)){
        	List<String> energy = new ArrayList<String>();
        	energy.add("Energy: "+UT_Utils.removeDecimals(entity.getCharge())+IPower.POWER_NAME);
        	this.drawHoveringText(energy, i, j, fontRendererObj);
        	RenderHelper.enableGUIStandardItemLighting();
        }
	}
	
	
}
