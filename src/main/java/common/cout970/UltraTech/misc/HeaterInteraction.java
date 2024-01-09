package common.cout970.UltraTech.misc;

import ultratech.api.util.UT_Utils;
import common.cout970.UltraTech.TileEntities.electric.tiers.TileEntityHeater;
import common.cout970.UltraTech.TileEntities.fluid.TileEntityBoiler;
import common.cout970.UltraTech.util.LogHelper;
import common.cout970.UltraTech.util.power.PowerExchange;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;

public class HeaterInteraction {

	public TileEntityHeater src;
	public ForgeDirection side;
	public Object target;
	public Interaction type;
	public float own;
	
	public HeaterInteraction(TileEntityHeater h,ForgeDirection s,Interaction t){
		src = h;
		side = s;
		type = t;
		if(type == Interaction.Block || type == Interaction.Fluid){
			target = src.getWorldObj().getBlock(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ);
		}
	}
	
	public float apply(float heat){
		if(type == Interaction.Block){
			if(heat >= 1300){
				if(target instanceof Block){
					if(target == Blocks.cobblestone){
						src.getWorldObj().setBlock(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, Blocks.lava);
						src.getWorldObj().notifyBlockOfNeighborChange(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, Blocks.lava);
						return 1000;
					}else if(target == Blocks.sand){
						src.getWorldObj().setBlock(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, Blocks.glass);
					}else if(target == Blocks.netherrack){
						src.getWorldObj().setBlock(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, Blocks.lava);
						src.getWorldObj().notifyBlockOfNeighborChange(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, Blocks.lava);
						return 900;
					}
				}
			}
			return 0;
		}else if(type == Interaction.Fluid){
			if(heat > 100){
				if(target instanceof Block){
					if(target == Blocks.water){
						src.getWorldObj().setBlock(src.xCoord+side.offsetX, src.yCoord+side.offsetY, src.zCoord+side.offsetZ, FluidRegistry.getFluid("steam").getBlock());
						return 10f;
					}
				}
			}
			return 0f;
		}else if(type == Interaction.Boiler){
			if(target == null)target = UT_Utils.getRelative(src, side);
			if(target instanceof TileEntityBoiler){
				TileEntityBoiler b = (TileEntityBoiler) target;
				float change = change(heat, b.heat);
				change = Math.min(change, heat-25);
				b.heat += change;
				return change;
			}
			return 0;
		}else if(type == Interaction.Furnace){
			if(target == null)target = UT_Utils.getRelative(src, side);
			if(target instanceof TileEntityFurnace){
				TileEntityFurnace tf = (TileEntityFurnace) target;
				if(tf.currentItemBurnTime <= heat)tf.currentItemBurnTime = (int) heat;
				if(tf.furnaceBurnTime < heat){
					int g = (int) (heat-99);
					g = Math.min(g, tf.currentItemBurnTime-tf.furnaceBurnTime);
					tf.furnaceBurnTime += g;
					tf.updateEntity();
					tf.updateEntity();
					tf.updateEntity();
					return PowerExchange.FTtoHeat(g);
				}
			}
		}
		return 0f;
	}
	
	public static float change(float a, float b){
		float dif = a-b;
		float change = dif/2;
		return change;
	}

	public static Interaction isInteresting(TileEntityHeater h, ForgeDirection d) {
		TileEntity t = UT_Utils.getRelative(h, d);
		if(t instanceof TileEntityBoiler)return Interaction.Boiler;
		if(t instanceof TileEntityFurnace)return Interaction.Furnace;
		Block b = h.getWorldObj().getBlock(h.xCoord+d.offsetX, h.yCoord+d.offsetY, h.zCoord+d.offsetZ);
		if(b == null)return Interaction.Nothing;
		if(b == Blocks.cobblestone)return Interaction.Block;
		if(b == Blocks.netherrack)return Interaction.Block;
		if(b == Blocks.sand)return Interaction.Block;
		if(b == Blocks.water)return Interaction.Fluid;
		return Interaction.Nothing;
	}
	
	public enum Interaction{
		Nothing,Block,Fluid,Furnace,Boiler;
	}
}
