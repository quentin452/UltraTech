package common.cout970.UltraTech.client.gui;


import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ultratech.api.power.interfaces.IPower;
import ultratech.api.util.UT_Utils;
import common.cout970.UltraTech.TileEntities.electric.TileEntityBatteryTier1;
import common.cout970.UltraTech.TileEntities.electric.TileEntityBatteryTier2;
import common.cout970.UltraTech.TileEntities.electric.TileEntityBatteryTier3;
import common.cout970.UltraTech.TileEntities.electric.TileEntityBatteryTier4;
import common.cout970.UltraTech.network.Net_Utils;
import common.cout970.UltraTech.network.SyncTile;
import common.cout970.UltraTech.network.messages.MessageMachineMode;
import common.cout970.UltraTech.util.power.Machine;

public class GuiBattery extends GuiMachineBase{

	public GuiBattery(Container par1Container,InventoryPlayer ip ,Machine entity) {
		super(par1Container, entity);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.renderEngine.bindTexture(new ResourceLocation("ultratech:textures/gui/ids.png"));
		xStart = (width - xSize) / 2;
		yStart = (height - ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
		int v = 0;
		Machine tile = (Machine) entity;
		switch(tile.cond.configIO){
		case Nothing: 	v=2; break;
		case Output:	v=1; break;
		case Storage:	v=0; break;
		default: v=0;}
		drawTexturedModalRect(xStart+138, yStart+29, 176, 19*v, 19, 19);
		if(entity != null){
			this.mc.renderEngine.bindTexture(new ResourceLocation("ultratech:textures/misc/energy.png"));
			int p = (int) (((double)tile.getCharge())*50/(tile.getCapacity()));
			this.drawTexturedModalRect(xStart+14, yStart+15+(50-p), 0, 0, 25, p);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		Machine tile = (Machine) entity;
		String e = (int)tile.getCharge()+""+IPower.POWER_NAME;
		String m = (int)tile.getCapacity()+""+IPower.POWER_NAME;
		
		this.drawString(fontRendererObj, "Max Storage", 50, 18, UT_Utils.RGBtoInt(255, 255, 255));
		this.drawString(fontRendererObj, m, 50, 30, UT_Utils.RGBtoInt(255, 255, 255));
		this.drawString(fontRendererObj, "Current", 50, 42, UT_Utils.RGBtoInt(255, 255, 255));
		this.drawString(fontRendererObj, e, 50, 54, UT_Utils.RGBtoInt(255, 255, 255));
		
		String f = "Battery Block";
		int c = UT_Utils.RGBtoInt(0, 0, 0);
		if(entity instanceof TileEntityBatteryTier1)c = UT_Utils.RGBtoInt(0, 120, 0);
		if(entity instanceof TileEntityBatteryTier2)c = UT_Utils.RGBtoInt(255, 0, 0);
		if(entity instanceof TileEntityBatteryTier3)c = UT_Utils.RGBtoInt(0, 0, 255);
		if(entity instanceof TileEntityBatteryTier4)c = UT_Utils.RGBtoInt(115, 0, 230);
		this.fontRendererObj.drawString(f, this.xSize - 130, 5, c);

		if(isIn(x, y, xStart+138, yStart+29, 19, 19)){
			String s = tile.cond.configIO+"";
			drawText(x, y, s);
		}
	}

	protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		Machine tile = (Machine) entity;
		xStart = (width - xSize) / 2;
		yStart = (height - ySize) / 2;
		if(isIn(par1, par2, xStart+138, yStart+29, 19, 19)){
			int m = tile.cond.configIO.ordinal();//023
			if(m == 0)m = 2;
			else if(m == 2)m = 3;
			else if(m == 3)m = 0;
			Net_Utils.INSTANCE.sendToServer(new MessageMachineMode((SyncTile) entity, m));
		}
	}
}
