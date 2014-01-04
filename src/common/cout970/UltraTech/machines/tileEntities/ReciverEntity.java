package common.cout970.UltraTech.machines.tileEntities;


import net.minecraft.tileentity.TileEntity;

public class ReciverEntity extends Machine{
		
	public Machine[] machines;
	public SenderEntity from;
	
	public ReciverEntity(){
		super();
		this.EnergyMax = 1000;
	}

	@Override
	public void updateEntity(){
		if(machines == null){
			machines = new Machine[6];
			check();
		}
		if(EnergyMax-Energy > 64)
		this.gainEnergy(getEnergyFromSender());
		refill();
	}
	
	public int getEnergyFromSender(){
		if(from != null){
			int e = from.getEnergy();
			if(e >0){
				if(e <= 64){
					from.loseEnergy(e);
					return e;
				}else{
					from.loseEnergy(64);
					return 64;
				}
			}
		}
		return 0;
	}


	public void refill(){
		for(Machine m :machines){
			if(m != null && !(m instanceof ReciverEntity)){
				if(m.EnergyMax-m.Energy > 64 && Energy >= 64){
					m.gainEnergy(64);
					loseEnergy(64);
				}
			}
		}
	}

	public void check(){
		TileEntity[] t = new TileEntity[6];
		t[0] = this.worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
		t[1] = this.worldObj.getBlockTileEntity(xCoord, yCoord+1, zCoord);
		t[2] = this.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord+1);
		t[3] = this.worldObj.getBlockTileEntity(xCoord+1, yCoord, zCoord);
		t[4] = this.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord-1);
		t[5] = this.worldObj.getBlockTileEntity(xCoord-1, yCoord, zCoord);

		int i= 0;
		for(TileEntity y : t){
			if(y instanceof Machine){
				machines[i] = (Machine) y;
			}else{
				machines[i] = null;
			}
			i++;
		}
	}

	public void onNeighChange() {
		check();
	}

	public void setFrom(int[] i) {

		TileEntity t = worldObj.getBlockTileEntity(i[0], i[1], i[2]);
		if(t != null){
			from = (SenderEntity) t;
		}
	}
}