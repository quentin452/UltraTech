package common.cout970.UltraTech.blocks.common;

import ultratech.api.util.UT_Utils;
import common.cout970.UltraTech.TileEntities.utility.TileEntityCrafter;
import common.cout970.UltraTech.client.textures.Block_Textures;
import common.cout970.UltraTech.managers.UT_Tabs;
import common.cout970.UltraTech.managers.UltraTech;
import common.cout970.UltraTech.util.power.Machine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCrafter extends BlockContainer{

	private IIcon blockIcon1;

	public BlockCrafter(Material m) {
		super(m);
		setCreativeTab(UT_Tabs.techTab);
		setStepSound(soundTypeMetal);
		setResistance(50);
		setHardness(2.0f);
		setBlockName("CrafterBlock");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCrafter();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister IR){
		blockIcon = IR.registerIcon(Block_Textures.CHASIS_T1);
		blockIcon1 = IR.registerIcon(Block_Textures.CRAFTER);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if(side == 1)return blockIcon1;
		return blockIcon;
	}
	
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		if(entityplayer.isSneaking())return true;
		TileEntity tile = world.getTileEntity(i, j, k);
		if(tile instanceof TileEntityCrafter){
			((TileEntityCrafter) tile).update();
			entityplayer.openGui(UltraTech.instance, 16, world, i, j, k);
			return true;
		}
		return true;
	}

	public void onNeighborBlockChange(World w, int x, int y, int z, Block side){
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof TileEntityCrafter){
			((TileEntityCrafter) te).onNeigChange(((TileEntityCrafter) te).getSignal());
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6){
		TileEntity t = world.getTileEntity(x, y, z);
		if(t instanceof IInventory){	
			UT_Utils.dropItems(world, x, y, z);
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
}
