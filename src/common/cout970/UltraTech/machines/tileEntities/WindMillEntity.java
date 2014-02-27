package common.cout970.UltraTech.machines.tileEntities;

import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WindMillEntity extends Machine{

	public float fan;
	public float speed = 0;
	public float wind = 1;
	public int timer;
	public boolean obs = false;

	public WindMillEntity(){
		super();
		this.tipe = MachineTipe.Output;
	}
	
	public void updateEntity()
	{
		float time = 0;
		float bioma = 0.5f;
		time = worldObj.getSunBrightness(1);
		String b = worldObj.getBiomeGenForCoords(xCoord, zCoord).biomeName;
		if(b == "Desert")bioma = 0.8f;
		if(b == "Ocean")bioma = 1f;
		if(b == "Jungle")bioma = 0.2f;
		if(b == "Forest")bioma = 0.3f;
		if(b == "ExtremeHills")bioma = 1f;
		wind = (this.yCoord/128)+(time+bioma)/2;
		if(timer++ > 100){
			timer = 0;
			if(isObstruido()){obs = true;}else{obs = false;}
		}
		if(obs)wind = 0;
		if(speed > wind){
			speed -= 0.01;
		}else if(speed < wind){
			speed += 0.005f;
		}
		gainEnergy((int)(speed*10));
		super.updateEntity();
	}

	private boolean isObstruido() {
		for(int x=-1;x<2;x++){
			for(int y=-1;y<2;y++){
				if(!worldObj.isAirBlock(xCoord+x, yCoord+4+y, zCoord+1)){
					return true;
				}
			}
		}
		for(int h=0;h<20;h++){
			if(!worldObj.isAirBlock(xCoord, yCoord+4, zCoord+1+h)){
				return true;
			}
		}
		return false;
	}
	
	
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
            bb = AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2+5, zCoord + 2);
        
        return bb;
    }

}
