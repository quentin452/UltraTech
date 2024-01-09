package common.cout970.UltraTech.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

import common.cout970.UltraTech.misc.IUpdatedEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageMultiPartUpdate implements IMessage, IMessageHandler<MessageMultiPartUpdate, IMessage>{

	public int x, y, z;
	
	 public MessageMultiPartUpdate(){}
	 
	 public MessageMultiPartUpdate(TMultiPart t){
		 x = t.x();
		 y = t.y();
		 z = t.z();
	 }

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer PB = new PacketBuffer(buf);
		x = PB.readInt();
		y = PB.readShort();
		z = PB.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer PB = new PacketBuffer(buf);
        PB.writeInt(x);
        PB.writeShort(y);
        PB.writeInt(z);
	}

	@Override
	public IMessage onMessage(MessageMultiPartUpdate message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		if(tileEntity instanceof TileMultipart){
			TileMultipart tm = (TileMultipart) tileEntity;
			for(TMultiPart t : tm.jPartList()){
				if(t instanceof IUpdatedEntity){
					((IUpdatedEntity) t).onNeigUpdate();
				}
			}
		}
		return null;
	}
}