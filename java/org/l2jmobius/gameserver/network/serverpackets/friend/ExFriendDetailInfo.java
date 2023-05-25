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
package org.l2jmobius.gameserver.network.serverpackets.friend;

import java.util.Calendar;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo extends ServerPacket
{
	private final int _objectId;
	private final Player _friend;
	private final String _name;
	private final int _lastAccess;
	
	public ExFriendDetailInfo(Player player, String name)
	{
		_objectId = player.getObjectId();
		_name = name;
		_friend = World.getInstance().getPlayer(_name);
		_lastAccess = _friend.isBlocked(player) ? 0 : _friend.isOnline() ? (int) System.currentTimeMillis() : (int) (System.currentTimeMillis() - _friend.getLastAccess()) / 1000;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_FRIEND_DETAIL_INFO.writeId(this);
		writeInt(_objectId);
		if (_friend == null)
		{
			writeString(_name);
			writeInt(0);
			writeInt(0);
			writeShort(0);
			writeShort(0);
			writeInt(0);
			writeInt(0);
			writeString("");
			writeInt(0);
			writeInt(0);
			writeString("");
			writeInt(1);
			writeString(""); // memo
		}
		else
		{
			writeString(_friend.getName());
			writeInt(_friend.isOnlineInt());
			writeInt(_friend.getObjectId());
			writeShort(_friend.getLevel());
			writeShort(_friend.getClassId().getId());
			writeInt(_friend.getClanId());
			writeInt(_friend.getClanCrestId());
			writeString(_friend.getClan() != null ? _friend.getClan().getName() : "");
			writeInt(_friend.getAllyId());
			writeInt(_friend.getAllyCrestId());
			writeString(_friend.getClan() != null ? _friend.getClan().getAllyName() : "");
			final Calendar createDate = _friend.getCreateDate();
			writeByte(createDate.get(Calendar.MONTH) + 1);
			writeByte(createDate.get(Calendar.DAY_OF_MONTH));
			writeInt(_lastAccess);
			writeString(""); // memo
		}
	}
}
