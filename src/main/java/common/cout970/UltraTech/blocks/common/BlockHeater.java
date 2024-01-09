package common.cout970.UltraTech.blocks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import common.cout970.UltraTech.TileEntities.electric.tiers.TileEntityHeater;
import common.cout970.UltraTech.client.textures.Block_Textures;
import common.cout970.UltraTech.managers.UT_Tabs;
import common.cout970.UltraTech.managers.UltraTech;
import common.cout970.UltraTech.misc.IUpdatedEntity;
import common.cout970.UltraTech.util.LogHelper;
import common.cout970.UltraTech.util.power.BlockConductor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHeater extends BlockConductor{

	private IIcon blockIcon1;

	public BlockHeater(Material m) {
		super(m);
		setCreativeTab(UT_Tabs.techTab);
		setHardness(2f);
		setStepSound(soundTypeMetal);
		setBlockName("Heater");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityHeater();
	}
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e instanceof EntityPlayer){
			TileEntity t = w.getTileEntity(x, y, z);
			if(t instanceof TileEntityHeater){
				if(((TileEntityHeater) t).Heat > 90){
					e.attackEntityFrom(DamageSource.inFire, 1.0F);
				}
			}
		}
	}
	
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Block_Textures.HEATER);
		this.blockIcon1 = iconRegister.registerIcon(Block_Textures.HEATER+"_on");
	}

	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
		if(meta == 1)return blockIcon1;
        return this.blockIcon;
    }
	
	public void onNeighborBlockChange(World w, int x, int y, int z, Block b) {
		IUpdatedEntity i = (IUpdatedEntity) w.getTileEntity(x, y, z);
		i.onNeigUpdate();
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int a, float b, float c, float d){
		if(!p.isSneaking())p.openGui(UltraTech.instance, 1, world, x, y, z);
		return true;
	}

}
