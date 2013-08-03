package morph.client.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import morph.client.morph.MorphInfoClient;
import morph.common.Morph;
import morph.common.core.ObfHelper;
import morph.common.morph.MorphHandler;
import morph.common.morph.MorphState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandlerClient
	implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try
		{
			int id = stream.readByte();
			switch(id)
			{
				case 0:
				{
					String name = stream.readUTF();
					
					boolean morphing = stream.readBoolean();
					int morphProg = stream.readInt();

					NBTTagCompound prevTag = new NBTTagCompound();
					NBTTagCompound nextTag = new NBTTagCompound();
					if(stream.readBoolean())
					{
						prevTag = Morph.readNBTTagCompound(stream);
					}
					if(stream.readBoolean())
					{
						nextTag = Morph.readNBTTagCompound(stream);
					}
					
					MorphState prevState = new MorphState(mc.theWorld, mc.thePlayer.username, "", null, true);
					MorphState nextState = new MorphState(mc.theWorld, mc.thePlayer.username, "", null, true);
					
					prevState.readTag(mc.theWorld, prevTag);
					nextState.readTag(mc.theWorld, nextTag);
					
					//TODO check for mc.theplayer morphstate
//					prevState = MorphHandler.addOrGetMorphState(Morph.proxy.tickHandlerClient.getPlayerMorphs(event.entityPlayer.worldObj, event.entityPlayer.username), prevState);
//					nextState = MorphHandler.addOrGetMorphState(Morph.proxy.tickHandlerClient.getPlayerMorphs(event.entityPlayer.worldObj, event.entityPlayer.username), nextState);
					
					if(prevState.entInstance != null)
					{
						ObfHelper.forceSetSize(prevState.entInstance, 0.0F, 0.0F);
						if(prevState.entInstance != mc.thePlayer)
						{
							prevState.entInstance.noClip = true;
						}
					}
					
					if(nextState.entInstance != null)
					{
						ObfHelper.forceSetSize(nextState.entInstance, 0.0F, 0.0F);
						if(nextState.entInstance != mc.thePlayer)
						{
							nextState.entInstance.noClip = true;
						}
					}
					
//					System.out.println(prevEnt);
//					System.out.println(nextEnt);
					
					MorphInfoClient info = new MorphInfoClient(name, prevState, nextState);
					info.setMorphing(morphing);
					info.morphProgress = morphProg;
					Morph.proxy.tickHandlerClient.playerMorphInfo.put(name, info);

					break;
				}
			}
		}
		catch(IOException e)
		{
		}
	}
}
