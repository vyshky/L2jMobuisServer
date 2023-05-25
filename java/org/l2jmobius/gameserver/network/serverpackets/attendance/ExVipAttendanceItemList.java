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
package org.l2jmobius.gameserver.network.serverpackets.attendance;

import org.l2jmobius.gameserver.data.xml.AttendanceRewardData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.AttendanceInfoHolder;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList extends ServerPacket
{
	boolean _available;
	int _index;
	
	public ExVipAttendanceItemList(Player player)
	{
		final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
		_available = attendanceInfo.isRewardAvailable();
		_index = attendanceInfo.getRewardIndex();
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_VIP_ATTENDANCE_ITEM_LIST.writeId(this);
		writeByte(_available ? _index + 1 : _index); // index to receive?
		writeByte(_index); // last received index?
		writeInt(0);
		writeInt(0);
		writeByte(1);
		writeByte(_available); // player can receive reward today?
		writeByte(250);
		writeByte(AttendanceRewardData.getInstance().getRewardsCount()); // reward size
		int rewardCounter = 0;
		for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards())
		{
			rewardCounter++;
			writeInt(reward.getId());
			writeLong(reward.getCount());
			writeByte(1); // is unknown?
			writeByte((rewardCounter % 7) == 0); // is last in row?
		}
		writeByte(0);
		writeInt(0);
	}
}
