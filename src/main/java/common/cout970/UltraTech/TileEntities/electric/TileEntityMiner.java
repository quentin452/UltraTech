package common.cout970.UltraTech.TileEntities.electric;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.oredict.OreDictionary;
import ultratech.api.util.UT_Utils;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

import com.google.common.collect.Sets;

import common.cout970.UltraTech.containers.ContainerMiner;
import common.cout970.UltraTech.managers.InformationManager;
import common.cout970.UltraTech.managers.MachineData;
import common.cout970.UltraTech.managers.UltraTech;
import common.cout970.UltraTech.util.LogHelper;
import common.cout970.UltraTech.util.MachineWithInventory;

public class TileEntityMiner extends MachineWithInventory implements IInventory{

	private ItemStack waiting;
	private int progres = 0;
	public int maxProgres = 6;
	boolean hasEnergy = false ;
	public int current = 0;
	private ArrayList<int[]> mining;
	public boolean hasMine = false;
	public int height = 4;
	public int widht = 4;
	public boolean blocked = false;
	public int minedLastSec = 0;
	public int count;
	
	public Mode mode = Mode.Horizontal;
	public List<IInventory> ex_Inv;
	//upgrades
	public boolean eject = false;
	public boolean hasSpeedUpgrades;
	public boolean hasRangeUpgrades;
	public boolean hasFortuneUpgrades;
	public int speedUpgrades=0;
	public int rangeUpgrades=0;
	public int fortuneUpgrades=0;
	private boolean mineFinish;
	private int mineSize;
	public boolean hasSilkUpgrade;
	private int cooldown;
	private boolean isFirstTime = false;
	private Ticket chunkTicket;


	public TileEntityMiner(){
		super(52,"Miner",MachineData.Miner);
	}

	public void loadChunk(){
		isFirstTime = true;
		if (chunkTicket == null) {
			chunkTicket = ForgeChunkManager.requestTicket(UltraTech.instance, worldObj, Type.NORMAL);
		}
		chunkTicket.getModData().setInteger("quarryX", xCoord);
		chunkTicket.getModData().setInteger("quarryY", yCoord);
		chunkTicket.getModData().setInteger("quarryZ", zCoord);
		ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if(this.worldObj.isRemote)return;
		if(!isFirstTime)loadChunk();
		if(ex_Inv == null){
			searchInventories();
		}
		
		if(eject && !isEmpty()){
			for(int g =0;g<2;g++)expulse();
		}
		
		if(!blocked){
			if(!hasMine){
				CreateMining();
				hasMine = true;
			}
			if(!hasEnergy){
				hasEnergy = this.getCharge() >= MachineData.Miner.use || InformationManager.debug;
			}
			boolean changes = false;
			
			if(hasEnergy && current < mining.size()){
				progres++;
				if(progres >= maxProgres){
					progres = 0;
					changes = true;
					hasEnergy = false;
					BreakNextBlock();
				}
			}
			if(InformationManager.debug){
				for(int i = 0;i<25;i++)BreakNextBlock();
			}

			if (changes)
			{
				markDirty();
			}
		}else{
			blocked = !addItemStack(waiting);
		}
		
		if(worldObj.getTotalWorldTime()%100 == 0){
			minedLastSec = count;
			count = 0;
		}
	}
	
	private boolean isEmpty() {
		for(int d = getSizeInventory()-1; d >= 0; d--){
			if(this.getStackInSlot(d) != null)return false;
		}
		return true;
	}


	private void BreakNextBlock() {
		for(int r = 0 ; r < 10;r++){
			if(mining.size() > current){
				int x = mining.get(current)[0];
				int y = mining.get(current)[1];
				int z = mining.get(current)[2];
				if(canBreak(worldObj.getBlock(x, y, z))){
					removeCharge(MachineData.Miner.use);
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					Block id = worldObj.getBlock(x, y, z);
					int meta = worldObj.getBlockMetadata(x, y, z);
					
					if(!hasSilkUpgrade){
						items = id.getDrops(worldObj, x, y, z, meta, fortuneUpgrades);
					}else{
						if(id.renderAsNormalBlock() && !id.hasTileEntity( meta)){//id.canSilkHarvest(worldObj, Minecraft.getMinecraft().thePlayer , x, y, z, meta)
							items.add(new ItemStack(id,1,meta));//Block.blocksList[id].damageDropped(Block.blocksList[id].getDamageValue(worldObj, x, y, z))));
						}else{
							items = id.getDrops(worldObj, x, y, z, meta, fortuneUpgrades);
						}
					}
					for(int n = 0; n < items.size();n++){
						blocked = !addItemStack(items.get(n));
					}
					worldObj.setBlockToAir(x, y, z);
					count++;
					break;
				}
			}else{
				if(!mineFinish){
					mineFinish = true;
					hasMine = false;
					current = 0;
				}
			}
			current++;
		}
	}

	public boolean canBreak(Block block){
		if(block == Blocks.air)return false;
		if(block instanceof BlockLiquid)return false;
		if(block instanceof BlockFluidBase)return false;
		if(Block.isEqualTo(block,Blocks.mob_spawner))return false;
		if(block == Blocks.portal)return false;
		if(block == Blocks.end_portal)return false;
		if(block == Blocks.end_portal_frame)return false;
		if(block.getBlockHardness(getWorldObj(), xCoord, yCoord, zCoord) == -1)return false;
		return true;
	}


	public void CreateMining() {
		mining = new ArrayList<int[]>();
		if(mode == Mode.Horizontal){
			for(int j = this.yCoord-1; j >= 1;j--){
				for(int i = -height;i <= height; i++){
					for(int k = -widht;k <= widht; k++){
						if(canBreak(worldObj.getBlock(this.xCoord + i,j , this.zCoord + k))){
							mining.add(new int[]{this.xCoord + i,j , this.zCoord + k});
						}
					}
				}
			}
		}else if(mode == Mode.VerticalY){
			for(int i = -height;i <= height; i++){
				for(int k = -widht;k <= widht; k++){
					for(int j = this.yCoord-1; j >= 1;j--){
						if(canBreak(worldObj.getBlock(this.xCoord + i,j , this.zCoord + k))){
							mining.add(new int[]{this.xCoord + i,j , this.zCoord + k});
						}
					}
				}
			}
		}else{
			for(int i = -height;i <= height; i++){
				for(int j = this.yCoord-1; j >= 1;j--){
					for(int k = -widht;k <= widht; k++){
						if(canBreak(worldObj.getBlock(this.xCoord + i,j , this.zCoord + k))){
							mining.add(new int[]{this.xCoord + i,j , this.zCoord + k});
						}
					}
				}
			}
		}
		mineSize = mining.size();
	}

	public void expulse(){
		ItemStack b;
		if((b = this.getStack()) != null){
			for(IInventory a : ex_Inv){
				for(int x = 0; x < a.getSizeInventory() ;x++){
					if(a.isItemValidForSlot(x, b) && a.getStackInSlot(x) == null){
						a.setInventorySlotContents(x, b);
						return;
					}else{
						if(OreDictionary.itemMatches(a.getStackInSlot(x), b, true)){
							if((b.stackSize + a.getStackInSlot(x).stackSize) <= a.getInventoryStackLimit() && (b.stackSize + a.getStackInSlot(x).stackSize) <= b.getItem().getItemStackLimit(b)){
								b.stackSize += a.getStackInSlot(x).stackSize;
								a.setInventorySlotContents(x, b);
								return;
							}
						}
					}
				}
			}
			if(cooldown <= 0){
				if(b != null){
					for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS){
						TileEntity t = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
						if(t instanceof IPipeTile){
							IPipeTile a = (IPipeTile) t;
							if(a.getPipeType() == PipeType.ITEM){
								a.injectItem(b, true, d.getOpposite());
								cooldown = 1;
								return;
							}
						}
					}
				}
			}else{
				cooldown--;
			}
			this.addItemStack(b);
		}
	}

	private ItemStack getStack() {
		for(int d = getSizeInventory()-1; d >= 0; d--){
			if(inventory[d] != null){
				ItemStack d1 = inventory[d];
				inventory[d] = null;
				return d1;
			}
		}
		return null;
	}

	public void searchInventories(){
		ex_Inv = new ArrayList<IInventory>();
		for(TileEntity y : UT_Utils.getTiles(this)){
			if(y instanceof IInventory)ex_Inv.add((IInventory) y);
		}
	}
	
	public enum Mode{
		VerticalY,Horizontal,VerticalX
	}
	
	public boolean addItemStack(ItemStack i){
		for(int s = 0;s < this.getSizeInventory();s++){	
			if (this.inventory[s] == null)
			{
				this.inventory[s] = i.copy();
				return true;
			}
			else if (this.inventory[s].isItemEqual(i))
			{
				if(inventory[s].stackSize + i.stackSize <= getInventoryStackLimit()){
				inventory[s].stackSize += i.stackSize;
				return true;
				}
			}
		}
		waiting = i;
		return false;
	}
	
	public void ChangeMode(){
		hasMine = false;
		current = 0;
		switch(mode){
		case Horizontal:{
			mode = Mode.VerticalY;
			break;
		}
		case VerticalY:{
			mode = Mode.VerticalX;
			break;
		}
		case VerticalX:{
			mode = Mode.Horizontal;
			break;
		}
		}
	}
	
	public int MinigSize(){
		if(mineSize <= 0)return 0;
		return mineSize;
	}

	//Save & Load
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {

		super.readFromNBT(nbtTagCompound);
		height = nbtTagCompound.getInteger("tam");
		widht = height;
		maxProgres = nbtTagCompound.getInteger("maxProgres");
		eject = nbtTagCompound.getBoolean("eject");
		switch(nbtTagCompound.getInteger("mode")){
		case 0:{
			mode = Mode.Horizontal;
			break;
		}
		case 1:{
			mode = Mode.VerticalY;
			break;
		}
		case 2:{
			mode = Mode.VerticalX;
			break;
		}
		}
		rangeUpgrades = nbtTagCompound.getInteger("rangeUpgrades");
		speedUpgrades = nbtTagCompound.getInteger("speedUpgrades");
		fortuneUpgrades = nbtTagCompound.getInteger("fortuneUpgrades");
		
		if(speedUpgrades > 0)hasSpeedUpgrades = true;
		if(rangeUpgrades > 0)hasRangeUpgrades = true;
		if(fortuneUpgrades > 0)hasFortuneUpgrades = true;
		hasSilkUpgrade = nbtTagCompound.getBoolean("silk");
		maxProgres = (6 - speedUpgrades);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {

		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("silk", hasSilkUpgrade);
		nbtTagCompound.setInteger("tam", height);
		nbtTagCompound.setInteger("maxProgres", maxProgres);
		nbtTagCompound.setBoolean("eject", eject);
		switch(mode){
		case Horizontal:{
			nbtTagCompound.setInteger("mode", 0);
			break;
		}
		case VerticalY:{
			nbtTagCompound.setInteger("mode", 1);
			break;
		}
		case VerticalX:{
			nbtTagCompound.setInteger("mode", 2);
			break;
		}		
		}
		
		nbtTagCompound.setInteger("rangeUpgrades", rangeUpgrades);
		nbtTagCompound.setInteger("speedUpgrades", speedUpgrades);
		nbtTagCompound.setInteger("fortuneUpgrades", fortuneUpgrades);
	}

	
	//Synchronization

	public void sendGUINetworkData(ContainerMiner containerMiner,
			ICrafting iCrafting) {
		super.sendGUINetworkData(containerMiner, iCrafting);
		iCrafting.sendProgressBarUpdate(containerMiner, 2, widht);
		iCrafting.sendProgressBarUpdate(containerMiner, 3, maxProgres);
		iCrafting.sendProgressBarUpdate(containerMiner, 4, mineSize);
		iCrafting.sendProgressBarUpdate(containerMiner, 5, minedLastSec);
		iCrafting.sendProgressBarUpdate(containerMiner, 6, fortuneUpgrades);
		iCrafting.sendProgressBarUpdate(containerMiner, 7, rangeUpgrades);
		iCrafting.sendProgressBarUpdate(containerMiner, 8, speedUpgrades);
		iCrafting.sendProgressBarUpdate(containerMiner, 9, hasSilkUpgrade ? 1 : 0);
		iCrafting.sendProgressBarUpdate(containerMiner, 10, this.eject ? 1 : 0);
	}

	public void getGUINetworkData(int id, int value) {
		super.getGUINetworkData(id, value);
		if(id == 2){
			widht = value;
			height = value;
		}
		if(id == 3)maxProgres = value;
		if(id == 4)mineSize = value;
		if(id == 5)minedLastSec = value;
		if(id == 6)fortuneUpgrades = value;
		if(id == 7)rangeUpgrades = value;
		if(id == 8)speedUpgrades = value;
		if(id == 9)hasSilkUpgrade = value == 1;
		if(id == 10)eject = value == 1;
	}

	public void forceChunkLoading(Ticket ticket) {
		if (chunkTicket == null) {
			chunkTicket = ticket;
		}
		Set<ChunkCoordIntPair> chunks = Sets.newHashSet();
		ChunkCoordIntPair quarryChunk = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
		chunks.add(quarryChunk);
		ForgeChunkManager.forceChunk(ticket, quarryChunk);

		for (int chunkX = -height/2 >> 4; chunkX <= height/2 >> 4; chunkX++) {
			for (int chunkZ = -height/2 >> 4; chunkZ <= height/2 >> 4; chunkZ++) {
				ChunkCoordIntPair chunk = new ChunkCoordIntPair(chunkX, chunkZ);
				ForgeChunkManager.forceChunk(ticket, chunk);
				chunks.add(chunk);
			}
		}
		LogHelper.info("Miner at "+xCoord+" "+yCoord+" "+zCoord+" will keep "+chunks.size()+" chunks loaded");
	}
}
