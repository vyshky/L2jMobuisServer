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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.serverpackets.AskJoinPledge;

/**
 * @author Mobius
 */
public class RequestClanAskJoinByName implements ClientPacket
{
	private String _playerName;
	private int _pledgeType;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_playerName = packet.readString();
		_pledgeType = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		final Player invitedPlayer = World.getInstance().getPlayer(_playerName);
		if (!player.getClan().checkClanJoinCondition(player, invitedPlayer, _pledgeType))
		{
			return;
		}
		if (!player.getRequest().setRequest(invitedPlayer, this))
		{
			return;
		}
		
		invitedPlayer.sendPacket(new AskJoinPledge(player, _pledgeType, player.getClan().getName()));
	}
	
	public int getPledgeType()
	{
		return _pledgeType;
	}
}