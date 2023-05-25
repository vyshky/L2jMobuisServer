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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

public class AskJoinPledge extends ServerPacket
{
	private final Player _requestor;
	private final int _pledgeType;
	private final String _pledgeName;
	
	public AskJoinPledge(Player requestor, int pledgeType, String pledgeName)
	{
		_requestor = requestor;
		_pledgeType = pledgeType;
		_pledgeName = pledgeName;
	}
	
	@Override
	public void write()
	{
		ServerPackets.ASK_JOIN_PLEDGE.writeId(this);
		writeInt(_requestor.getObjectId());
		writeString(_requestor.getName());
		writeString(_pledgeName);
		if (_pledgeType != 0)
		{
			writeInt(_pledgeType);
		}
	}
}
