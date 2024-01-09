package common.cout970.UltraTech.util.fluids;


import common.cout970.UltraTech.util.LogHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class TankUT implements IFluidTank{

	private FluidStack fluid;
	private int capacity = 0;


	public TankUT(int cap ,World w,int x, int y, int z){
		capacity = cap;
	}

	public TankUT(int cap, TileEntity entity) {
		capacity = cap;
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getFluidAmount() {
		if(fluid == null)return 0;
		return fluid.amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (resource == null)return 0;
		if (!doFill){
			if (fluid == null){
				return Math.min(capacity, resource.amount);
			}
			if (!fluid.isFluidEqual(resource)){
				return 0;
			}
			return Math.min(capacity - fluid.amount, resource.amount);
		}

		if (fluid == null){
			fluid = new FluidStack(resource, Math.min(capacity, resource.amount));

			return fluid.amount;
		}
		if (!fluid.isFluidEqual(resource)){
			return 0;
		}

		int filled = capacity - fluid.amount;
		if (resource.amount < filled)
		{
			fluid.amount += resource.amount;
			filled = resource.amount;
		}else{
			fluid.amount = capacity;
		}
		return filled;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (fluid == null)return null;
		int drained = maxDrain;

		if (fluid.amount < drained){
			drained = fluid.amount;
		}
		FluidStack stack = new FluidStack(fluid, drained);
		if (doDrain){
			fluid.amount -= drained;
			if (fluid.amount <= 0){
				fluid = null;
			}
		}
		return stack;
	}

	public void readFromNBT(NBTTagCompound nbt, String liq) {
		if(nbt.getInteger("Fluid_"+liq)==-1){
			fluid = null;
			return;
		}
		fluid = new FluidStack(nbt.getInteger("Fluid_"+liq), nbt.getInteger("Amount_"+liq));
	}

	public void writeToNBT(NBTTagCompound nbt,String liq) {
		if(fluid != null){
			nbt.setInteger("Fluid_"+liq, fluid.getFluidID());
			nbt.setInteger("Amount_"+liq, fluid.amount);
		}else{
			nbt.setInteger("Fluid_"+liq, -1);
		}
	}

	public void setFluid(FluidStack fluidStack) {
		fluid = fluidStack;
	}

	public void setFluidAmount(int value) {
		if(fluid != null)fluid.amount = value;
	}

	public void setCapacity(int integer) {
		capacity = integer;
	}

}
