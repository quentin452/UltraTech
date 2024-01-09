package common.cout970.UltraTech.blocks.common;

import java.util.List;
import java.util.Random;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import common.cout970.UltraTech.TileEntities.electric.TileEntityClimateStation;
import common.cout970.UltraTech.TileEntities.electric.TileEntityMiner;
import common.cout970.UltraTech.TileEntities.electric.TileEntityMolecularAssembly;
import common.cout970.UltraTech.TileEntities.electric.tiers.TileEntityTesseract;
import common.cout970.UltraTech.TileEntities.utility.TileEntityHologramEmiter;
import common.cout970.UltraTech.client.textures.Block_Textures;
import common.cout970.UltraTech.managers.ItemManager;
import common.cout970.UltraTech.managers.UT_Tabs;
import common.cout970.UltraTech.managers.UltraTech;
import common.cout970.UltraTech.util.power.BlockConductor;
import common.cout970.UltraTech.util.power.Machine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlocksTier3 extends BlockConductor{

	public IIcon icons[];
	public int numBlocks = 4;
	
	public BlocksTier3(Material par2Material) {
		super(par2Material);
		setCreativeTab(UT_Tabs.techTab);
		setStepSound(soundTypeMetal);
		setResistance(50);
		setHardness(2.5f);
		setBlockName("UT_Tier3Block");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister IR){
		icons = new IIcon[9];
		icons[0] = IR.registerIcon(Block_Textures.CHASIS_T3);
		icons[1] = IR.registerIcon(Block_Textures.MINER);
		icons[2] = IR.registerIcon(Block_Textures.HOLOGRAM_EMITER);
		icons[3] = IR.registerIcon(Block_Textures.ASSEMBLY);
		icons[4] = IR.registerIcon(Block_Textures.CLIMATE_STATION+"1");
		icons[5] = IR.registerIcon(Block_Textures.CLIMATE_STATION+"2");
		icons[6] = IR.registerIcon(Block_Textures.CLIMATE_STATION+"3");
		icons[7] = IR.registerIcon(Block_Textures.TESSERACT);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if(metadata == 0)return new TileEntityMiner();
		if(metadata == 1)return new TileEntityHologramEmiter();
		if(metadata == 2)return new TileEntityMolecularAssembly();
		if(metadata == 3)return new TileEntityClimateStation();
		return null;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		switch(meta){
		case 0:{
			if(side != 1 && side != 0)return icons[1];
			return icons[0];
		}
		case 1:{
			if(side == 1)return icons[2];
			return icons[0];
		}
		case 2:{
			if(side != 1 && side != 0)return icons[3];
			return icons[0];
		}
		case 3:{
			if(side == 1 || side == 0)return icons[6];
			if(side == 3)return icons[5];
			if(side == 2)return icons[5];
			return icons[4];
		}
		case 4:{
			return icons[7];
		}
		default:return icons[0];
		}
	}

	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {

		if(entityplayer.isSneaking()){
			return true;
		}else{
			TileEntity tile = world.getTileEntity(i, j, k);
			if(tile != null){ 
				if(tile instanceof TileEntityMiner){
					if(entityplayer.getCurrentEquippedItem() != null){
						if(entityplayer.getCurrentEquippedItem().getItem() instanceof IToolWrench){
							((TileEntityMiner) tile).ChangeMode();
							if(!world.isRemote)
							entityplayer.addChatComponentMessage(new ChatComponentText("Changed Miner mode to: "+((TileEntityMiner) tile).mode));
							return true;
						}
					}
				}
				entityplayer.openGui(UltraTech.instance, 13, world, i, j, k);
			}
		}
		return true;
	}

	public void onNeighborBlockChange(World w, int x, int y, int z, Block side){
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof Machine){
			if(((Machine)te).getNetwork() != null)((Machine)te).getNetwork().refresh();
		}
		if(te instanceof TileEntityMiner){
			((TileEntityMiner)te).searchInventories();
		}
		if(te instanceof TileEntityClimateStation){
			((TileEntityClimateStation) te).restoneUpdate(w.isBlockIndirectlyGettingPowered(x, y, z));
		}
		if(te instanceof TileEntityTesseract){
			TileEntityTesseract.tes.clear();
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems)
    {
		for (int ix = 0; ix < numBlocks; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

	@Override
	public int getDamageValue(World par1World, int par2, int par3, int par4) {
		return par1World.getBlockMetadata(par2, par3, par4);
	}

	@Override
	public int damageDropped (int metadata) {
		return metadata;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6){
		TileEntityTesseract.tes.clear();
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void dropItems(World world, int x, int y, int z){
		super.dropItems(world, x, y, z);
		Random rand = new Random();
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		if(tileEntity instanceof TileEntityMiner){
			TileEntityMiner me = (TileEntityMiner) tileEntity;
			if(me.eject){
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityItem = new EntityItem(world,
						x + rx, y + ry, z + rz,
						new ItemStack(ItemManager.ItemName.get("AutoEjectUpgrade"), 1, 0));
				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
			}
			if(me.hasSpeedUpgrades){
				for(int d = me.speedUpgrades;d > 0;d--){
					float rx = rand.nextFloat() * 0.8F + 0.1F;
					float ry = rand.nextFloat() * 0.8F + 0.1F;
					float rz = rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(ItemManager.ItemName.get("MiningUpgrade"), 1, 0));
					float factor = 0.05F;
					entityItem.motionX = rand.nextGaussian() * factor;
					entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
					entityItem.motionZ = rand.nextGaussian() * factor;
					world.spawnEntityInWorld(entityItem);
				}
			}
			if(me.hasRangeUpgrades){
				for(int d = me.rangeUpgrades;d > 0;d--){
					float rx = rand.nextFloat() * 0.8F + 0.1F;
					float ry = rand.nextFloat() * 0.8F + 0.1F;
					float rz = rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(ItemManager.ItemName.get("RangeUpgrade"), 1, 0));
					float factor = 0.05F;
					entityItem.motionX = rand.nextGaussian() * factor;
					entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
					entityItem.motionZ = rand.nextGaussian() * factor;
					world.spawnEntityInWorld(entityItem);
				}
			}
			if(me.hasFortuneUpgrades){
				for(int d = me.fortuneUpgrades;d > 0;d--){
					float rx = rand.nextFloat() * 0.8F + 0.1F;
					float ry = rand.nextFloat() * 0.8F + 0.1F;
					float rz = rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(ItemManager.ItemName.get("FortuneUpgrade"), 1, 0));
					float factor = 0.05F;
					entityItem.motionX = rand.nextGaussian() * factor;
					entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
					entityItem.motionZ = rand.nextGaussian() * factor;
					world.spawnEntityInWorld(entityItem);
				}
			}
			if(me.hasSilkUpgrade){
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityItem = new EntityItem(world,
						x + rx, y + ry, z + rz,
						new ItemStack(ItemManager.ItemName.get("SilkTouchUpgrade"), 1, 0));
				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
			}
		}
	}

}
