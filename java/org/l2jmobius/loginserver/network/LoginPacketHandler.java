/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.loginserver.network;

import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketHandlerInterface;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.loginserver.network.clientpackets.LoginClientPacket;

/**
 * @author Mobius
 */
public class LoginPacketHandler implements PacketHandlerInterface<LoginClient>
{
	private static final Logger LOGGER = Logger.getLogger(LoginPacketHandler.class.getName());
	
	@Override
	public void handle(LoginClient client, ReadablePacket packet)
	{
		// Read packet id.
		final int packetId;
		try
		{
			packetId = packet.readByte();
		}
		catch (Exception e)
		{
			LOGGER.warning("LoginPacketHandler: Problem receiving packet id from " + client);
			LOGGER.warning(CommonUtil.getStackTrace(e));
			client.disconnect();
			return;
		}
		
		// Check if packet id is within valid range.
		if ((packetId < 0) || (packetId >= LoginClientPackets.PACKET_ARRAY.length))
		{
			return;
		}
		
		// Find packet enum.
		final LoginClientPackets packetEnum = LoginClientPackets.PACKET_ARRAY[packetId];
		if (packetEnum == null)
		{
			return;
		}
		
		// Check connection state.
		if (!packetEnum.getConnectionStates().contains(client.getConnectionState()))
		{
			return;
		}
		
		// Create new LoginClientPacket.
		final LoginClientPacket newPacket = packetEnum.newPacket();
		if (newPacket == null)
		{
			return;
		}
		
		// Continue on another thread.
		if (Config.THREADS_FOR_CLIENT_PACKETS)
		{
			ThreadPool.execute(new ExecuteTask(client, packet, newPacket, packetId));
		}
		else // Wait for execution.
		{
			try
			{
				newPacket.read(packet);
				newPacket.run(client);
			}
			catch (Exception e)
			{
				LOGGER.warning("LoginPacketHandler: Problem with " + client + " [Packet: 0x" + Integer.toHexString(packetId).toUpperCase() + "]");
				LOGGER.warning(CommonUtil.getStackTrace(e));
			}
		}
	}
	
	private class ExecuteTask implements Runnable
	{
		private final LoginClient _client;
		private final ReadablePacket _packet;
		private final LoginClientPacket _newPacket;
		private final int _packetId;
		
		public ExecuteTask(LoginClient client, ReadablePacket packet, LoginClientPacket newPacket, int packetId)
		{
			_client = client;
			_packet = packet;
			_newPacket = newPacket;
			_packetId = packetId;
		}
		
		@Override
		public void run()
		{
			try
			{
				_newPacket.read(_packet);
				_newPacket.run(_client);
			}
			catch (Exception e)
			{
				LOGGER.warning("LoginPacketHandler->ExecuteTask: Problem with " + _client + " [Packet: 0x" + Integer.toHexString(_packetId).toUpperCase() + "]");
				LOGGER.warning(CommonUtil.getStackTrace(e));
			}
		}
	}
}
