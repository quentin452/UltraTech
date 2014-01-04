package common.cout970.UltraTech.machines.blocks;

import common.cout970.UltraTech.core.UltraTech;
import common.cout970.UltraTech.machines.tileEntities.ReciverEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class Reciver extends BlockContainer{


	public Reciver(int par1, Material par2Material) {
		super(par1, par2Material);
		setCreativeTab(UltraTech.techTab);
		setHardness(1.5f);
		setStepSound(soundMetalFootstep);
		setResistance(30);
		setUnlocalizedName("Reciver");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new ReciverEntity();
	}

	public void registerIcons(IconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon("ultratech:reciver");
	}
	
	@Override
	public Icon getIcon(int side,int a)
    {
		return this.blockIcon;
    }
	public void onNeighborBlockChange(World w, int x, int y, int z, int side){
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te != null && !w.isRemote){
			if(te instanceof ReciverEntity){
				ReciverEntity r = (ReciverEntity)te;
				r.onNeighChange();
			}
		}
	}
}
