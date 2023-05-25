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

import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.enums.SiegeClanType;
import org.l2jmobius.gameserver.model.SiegeClan;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<br>
 * <br>
 * c = 0xcb<br>
 * d = CastleID<br>
 * d = unknown (0)<br>
 * d = unknown (1)<br>
 * d = unknown (0)<br>
 * d = Number of Defending Clans?<br>
 * d = Number of Defending Clans<br>
 * { //repeats<br>
 * d = ClanID<br>
 * S = ClanName<br>
 * S = ClanLeaderName<br>
 * d = ClanCrestID<br>
 * d = signed time (seconds)<br>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<br>
 * d = AllyID<br>
 * S = AllyName<br>
 * S = AllyLeaderName<br>
 * d = AllyCrestID<br>
 * @author KenM
 */
public class SiegeDefenderList extends ServerPacket
{
	private final Castle _castle;
	
	public SiegeDefenderList(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	public void write()
	{
		ServerPackets.CASTLE_SIEGE_DEFENDER_LIST.writeId(this);
		writeInt(_castle.getResidenceId());
		writeInt(0); // Unknown
		writeInt(1); // Unknown
		writeInt(0); // Unknown
		final int size = _castle.getSiege().getDefenderWaitingClans().size() + _castle.getSiege().getDefenderClans().size() + (_castle.getOwner() != null ? 1 : 0);
		writeInt(size);
		writeInt(size);
		// Add owners
		final Clan ownerClan = _castle.getOwner();
		if (ownerClan != null)
		{
			writeInt(ownerClan.getId());
			writeString(ownerClan.getName());
			writeString(ownerClan.getLeaderName());
			writeInt(ownerClan.getCrestId());
			writeInt(0); // signed time (seconds) (not storated by L2J)
			writeInt(SiegeClanType.OWNER.ordinal());
			writeInt(ownerClan.getAllyId());
			writeString(ownerClan.getAllyName());
			writeString(""); // AllyLeaderName
			writeInt(ownerClan.getAllyCrestId());
		}
		// List of confirmed defenders
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderClans())
		{
			final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if ((defendingClan == null) || (defendingClan == _castle.getOwner()))
			{
				continue;
			}
			writeInt(defendingClan.getId());
			writeString(defendingClan.getName());
			writeString(defendingClan.getLeaderName());
			writeInt(defendingClan.getCrestId());
			writeInt(0); // signed time (seconds) (not storated by L2J)
			writeInt(SiegeClanType.DEFENDER.ordinal());
			writeInt(defendingClan.getAllyId());
			writeString(defendingClan.getAllyName());
			writeString(""); // AllyLeaderName
			writeInt(defendingClan.getAllyCrestId());
		}
		// List of not confirmed defenders
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderWaitingClans())
		{
			final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if (defendingClan == null)
			{
				continue;
			}
			writeInt(defendingClan.getId());
			writeString(defendingClan.getName());
			writeString(defendingClan.getLeaderName());
			writeInt(defendingClan.getCrestId());
			writeInt(0); // signed time (seconds) (not storated by L2J)
			writeInt(SiegeClanType.DEFENDER_PENDING.ordinal());
			writeInt(defendingClan.getAllyId());
			writeString(defendingClan.getAllyName());
			writeString(""); // AllyLeaderName
			writeInt(defendingClan.getAllyCrestId());
		}
	}
}
