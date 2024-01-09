package common.cout970.UltraTech.blocks.models;

import common.cout970.UltraTech.TileEntities.fluid.TileEntityBoiler;
import common.cout970.UltraTech.client.textures.Block_Textures;
import common.cout970.UltraTech.managers.UT_Tabs;
import common.cout970.UltraTech.managers.UltraTech;
import common.cout970.UltraTech.misc.IUpdatedEntity;
import common.cout970.UltraTech.proxy.ClientProxy;
import common.cout970.UltraTech.util.power.BlockConductor;
import common.cout970.UltraTech.util.power.Machine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBoiler extends BlockConductor{

	public BlockBoiler(Material par2Material) {
		super(par2Material);
		setCreativeTab(UT_Tabs.techTab);
		setHardness(2.5f);
		setStepSound(soundTypeMetal);
		setResistance(20);
		setBlockName("Boiler");
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int par6, float par7, float par8, float par9)
	{
		if(p.isSneaking())return true;
		TileEntity e = w.getTileEntity(x, y, z);
		if(e != null){
			p.openGui(UltraTech.instance, 13, w, x, y, z);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world,int b) {
		return new TileEntityBoiler();
	}
	
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ){
		IUpdatedEntity i = (IUpdatedEntity) world.getTileEntity(x, y, z);
		i.onNeigUpdate();
	}

	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Block_Textures.PARTICLE_COPPER);
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}

	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return ClientProxy.boilerRenderPass;
	}

}
