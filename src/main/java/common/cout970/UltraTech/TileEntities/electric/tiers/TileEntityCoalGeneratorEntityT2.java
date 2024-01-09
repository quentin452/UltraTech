package common.cout970.UltraTech.TileEntities.electric.tiers;


import ultratech.api.power.interfaces.ISpeeded;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import common.cout970.UltraTech.misc.IUpdatedEntity;
import common.cout970.UltraTech.util.power.PowerExchange;

public class TileEntityCoalGeneratorEntityT2 extends TileEntityCoalGeneratorEntityT1 implements ISpeeded,IUpdatedEntity{

	private int upgrades = 0;

	public TileEntityCoalGeneratorEntityT2(){
		super();
		maxHeat = 600 + 100*upgrades;
	}

	@Override
	public void updateEntity(){
		super.updateEntity();
		if(worldObj.isRemote)return;

		if(Progres > 0){
			if(!updated){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
				updated = true;
			}
			double extract = production();
			if(Progres - extract*2 < 0){
				addCharge(PowerExchange.FTtoQP(Progres));
				Progres = 0;
			}else{
				Progres -= extract*2;
				addCharge(extract);
			}
			if(heat < maxHeat)heat+=1.2-heat/maxHeat;
		}else{
			if(heat > 25)heat-=0.1+heat/maxHeat;
		}

		if(Progres <= 0){
			if(inventory[0] != null && shouldWork()){
				int fuel = TileEntityFurnace.getItemBurnTime(inventory[0]);
				if(fuel > 0 && (getCharge()+PowerExchange.FTtoQP(fuel) <= getCapacity() || (PowerExchange.FTtoQP(fuel) > getCapacity()&& getCharge() < getCapacity()))){
					Progres = fuel;
					maxProgres = fuel;
					if(inventory[0] != null){
						inventory[0].stackSize--;
						if(inventory[0].stackSize <= 0){
							inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
						}
					}
					markDirty();
				}
			}
			if(Progres <= 0 && updated){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
				updated = false;
			}
		}			
	}

	//Save & Load
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		upgrades = nbt.getInteger("Up");
		maxHeat = 600 + 100*upgrades;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("Up", upgrades);
	}

	@Override
	public boolean upgrade() {
		if(upgrades < 4){
			upgrades++;
			maxHeat = 600 + 100*upgrades;
			return true;
		}
		return false;
	}

	@Override
	public int getUpgrades() {
		return upgrades;
	}

	@Override
	public void onNeigUpdate() {}
}