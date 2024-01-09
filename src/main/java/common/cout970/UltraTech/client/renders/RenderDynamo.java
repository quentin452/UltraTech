package common.cout970.UltraTech.client.renders;

import org.lwjgl.opengl.GL11;

import common.cout970.UltraTech.TileEntities.intermod.TileEntityDynamo;
import common.cout970.UltraTech.TileEntities.intermod.TileEntityEngine;
import common.cout970.UltraTech.client.models.ModelDynamo;
import common.cout970.UltraTech.client.textures.ModelResources;
import common.cout970.UltraTech.util.render.RenderUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderDynamo extends TileEntitySpecialRenderer{

private ModelDynamo model;
	
	public RenderDynamo(){
		model = new ModelDynamo();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
			double z, float scale) {

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
//		float f = 0.9f;
//		GL11.glColor4f(f, f, f, 1);
		TileEntityDynamo d = (TileEntityDynamo) te;
		if(d.isWorking())RenderUtil.bindTexture(ModelResources.DYNAMO_ON);
		else RenderUtil.bindTexture(ModelResources.DYNAMO);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		setRotation(d);
		this.model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
	
	private void setRotation(TileEntityDynamo e) {
		//rotation
		switch(e.facing){
		case NORTH:{
			GL11.glRotatef(180, 0, 1, 0);
		}
		case SOUTH:{
			GL11.glRotatef(-90, 1, 0, 0);
			GL11.glTranslated(0, -1, 1);
			break;}
		case EAST:{
			GL11.glRotatef(-90, 0, 0, 1);
			GL11.glTranslated(-1, -1, 0);
			break;}
		case WEST:{
			GL11.glRotatef(90, 0, 0, 1);
			GL11.glTranslated(1, -1, 0);
			break;}
		case DOWN:{
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glTranslated(0, -2, 0);
			break;}
		case UP:{break;}
		default:{}
		}
	}
}
