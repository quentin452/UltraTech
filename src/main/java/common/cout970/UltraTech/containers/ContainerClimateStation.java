package common.cout970.UltraTech.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerClimateStation extends ContainerUT{

	public ContainerClimateStation(InventoryPlayer inventoryPlayer, TileEntity t) {
		super(inventoryPlayer, t);
		bindPlayerInventory(inventoryPlayer);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
		return null;
	}
}
