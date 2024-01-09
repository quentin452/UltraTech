package common.cout970.UltraTech.TileEntities.fluid;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ultratech.api.recipes.Boiler_Recipes;

import common.cout970.UltraTech.misc.IUpdatedEntity;
import common.cout970.UltraTech.network.SyncTile;
import common.cout970.UltraTech.util.fluids.TankUT;
import common.cout970.UltraTech.util.power.PowerExchange;

public class TileEntityBoiler extends SyncTile implements IFluidHandler,IUpdatedEntity{

	public TankUT result;
	public TankUT storage;
	public float heat;

	public void updateEntity(){
		super.updateEntity();
		if(result == null)result = new TankUT(16000, worldObj, xCoord, yCoord, zCoord);
		if(storage == null)storage = new TankUT(8000, worldObj, xCoord, yCoord, zCoord);
		if(worldObj.isRemote)return;

		if(storage != null){
			float exces = heat-99;
			int toBoil = (int) exces;
			if(toBoil >= 1){
				int canBoil = Math.min(storage.getFluidAmount(), toBoil);
				int canAccept = Math.min(result.getCapacity()-result.getFluidAmount(), canBoil*10);
				canBoil = Math.min(canBoil, canAccept/10);
				if(canBoil > 0){
					Fluid s = Boiler_Recipes.getResult(storage.getFluid());
					if(s != null){
						storage.drain(canBoil, true);
						FluidStack t = new FluidStack(s,canBoil*10);
						result.fill(t, true);
						heat -= PowerExchange.HeatPerFluid(canBoil)*10;
					}
				}
			}
		}
	}

	//Fluids

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource == null || storage == null)return 0;
		if(Boiler_Recipes.hasRecipe(resource)){
			return storage.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource == null || result == null)return null;
		if(result.getFluid() != null && Boiler_Recipes.isEqual(result.getFluid().getFluid(),resource.getFluid())){
			return result.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(maxDrain == 0 || result == null)return null;
		return result.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(Boiler_Recipes.hasRecipe(new FluidStack(fluid, 0)))return true;
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(result.getFluid() != null && Boiler_Recipes.isEqual(fluid,result.getFluid().getFluid()))return true;
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(result == null || storage == null)return new FluidTankInfo[]{};
		return new FluidTankInfo[]{result.getInfo(),storage.getInfo()};
	}

	//Save & Load

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		if(storage == null)storage = new TankUT(8000, worldObj, xCoord, yCoord, zCoord);
		if(result == null)result = new TankUT(16000, worldObj, xCoord, yCoord, zCoord);
		storage.readFromNBT(nbtTagCompound, "storage");
		result.readFromNBT(nbtTagCompound, "result");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		if(storage == null)storage = new TankUT(8000, worldObj, xCoord, yCoord, zCoord);
		if(result == null)result = new TankUT(16000, worldObj, xCoord, yCoord, zCoord);
		storage.writeToNBT(nbtTagCompound, "storage");
		result.writeToNBT(nbtTagCompound, "result");
	}

	//Synchronization

	public void sendGUINetworkData(Container cont, ICrafting c) {
		super.sendGUINetworkData(cont, c);
		if(storage.getFluid() != null)c.sendProgressBarUpdate(cont, 5, storage.getFluid().getFluidID());
		if(result.getFluid() != null)c.sendProgressBarUpdate(cont, 6, result.getFluid().getFluidID());
		c.sendProgressBarUpdate(cont, 2, storage.getFluidAmount());
		c.sendProgressBarUpdate(cont, 3, result.getFluidAmount());
		c.sendProgressBarUpdate(cont, 4, (int) heat);
	}

	public void getGUINetworkData(int id, int value) {
		super.getGUINetworkData(id, value);
		if(id == 2)storage.setFluidAmount(value);
		if(id == 3)result.setFluidAmount(value);
		if(id == 4)heat = value;
		if(id == 5)storage.setFluid(new FluidStack(value, 1));
		if(id == 6)result.setFluid(new FluidStack(value, 1));
	}

	@Override
	public void onNeigUpdate() {

	}
}
