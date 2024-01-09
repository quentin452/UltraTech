package common.cout970.UltraTech.wiki;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ultratech.api.util.UT_Utils;
import common.cout970.UltraTech.wiki.pages.DecoPage;
import common.cout970.UltraTech.wiki.pages.EnergyCapacityPage;
import common.cout970.UltraTech.wiki.pages.EnergyMainPage;
import common.cout970.UltraTech.wiki.pages.IndexPage;
import common.cout970.UltraTech.wiki.pages.MainPage;
import common.cout970.UltraTech.wiki.pages.MultiblockMainPage;
import common.cout970.UltraTech.wiki.pages.PageMultiblockRefinery;

public class TabletGui extends GuiContainer{

	public ResourceLocation texture = new ResourceLocation("ultratech:textures/gui/tablet.png");
	public IPage curr = new MainPage(this);
	public int page = 1;
	public int maxpages = 1;
	
	public TabletGui(Container par1Container) {
		super(par1Container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
		this.mc.renderEngine.bindTexture(texture);
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

		if(curr != null){
			curr.renderPage(this.mc.renderEngine,xStart,yStart);
		}
	}

	protected void mouseClicked(int x, int y, int b)
	{
		super.mouseClicked(x, y, b);
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		if(UT_Utils.isIn(x-xStart, y-yStart, 155, 5, 18, 18)){
			curr = new MainPage(this);
		}else if(UT_Utils.isIn(x-xStart, y-yStart, 155, 137, 18, 18)){
			if(page < maxpages)page++;
		}else if(UT_Utils.isIn(x-xStart, y-yStart, 155, 115, 18, 18)){
			if(page > 1 )page--;
		}else{
			if(curr != null){
				curr.mouseClick(x-xStart, y-yStart);
			}
		}
	}

	public void loadPage(String s) {
		maxpages = 1;
		if(s == "index")curr = new IndexPage(this);
		if(s == "e-main")curr = new EnergyMainPage(this);
		if(s == "e-cap")curr = new EnergyCapacityPage(this);
		if(s == "deco")curr = new DecoPage(this);
		if(s == "multi")curr = new MultiblockMainPage(this);
		if(s == "multi-refinery")curr = new PageMultiblockRefinery(this);
	}
}
