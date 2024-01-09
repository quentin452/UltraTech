package common.cout970.UltraTech.blocks.models;

import buildcraft.api.tools.IToolWrench;
import common.cout970.UltraTech.TileEntities.fluid.TileEntityBoiler;
import common.cout970.UltraTech.TileEntities.intermod.TileEntityDynamo;
import common.cout970.UltraTech.TileEntities.intermod.TileEntityEngine;
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
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDynamo extends BlockConductor{

	public BlockDynamo(Material p_i45394_1_) {
		super(p_i45394_1_);
		setCreativeTab(UT_Tabs.techTab);
		setHardness(2.5f);
		setStepSound(soundTypeMetal);
		setResistance(20);
		setBlockName("Dynamo");
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int par6, float par7, float par8, float par9)
	{
		if(p.isSneaking())return true;
		TileEntityDynamo e = (TileEntityDynamo) w.getTileEntity(x, y, z);
		if(e != null){
			if(p.getCurrentEquippedItem() != null && p.getCurrentEquippedItem().getItem() instanceof IToolWrench){
				e.switchOrientation();
				return true;
			}
			p.openGui(UltraTech.instance, 13, w, x, y, z);
		}
		return true;
	}

	public void onNeighborBlockChange(World w, int x, int y, int z, Block side){
		IUpdatedEntity m = (IUpdatedEntity) w.getTileEntity(x, y, z);
		m.onNeigUpdate();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world,int b) {
		return new TileEntityDynamo();
	}
	
	public void onBlockAdded(World w, int x, int y, int z) {
		super.onBlockAdded(w, x, y, z);
		TileEntityDynamo d = (TileEntityDynamo) w.getTileEntity(x, y, z);
		d.switchOrientation();
	}

	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Block_Textures.CHASIS_T2);
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
		return ClientProxy.dynamoRenderPass;
	}
	

}
