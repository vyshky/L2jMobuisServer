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

import java.util.Collection;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.sql.CharNameTable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.Clan.SubPledge;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PledgeShowMemberListAll extends ServerPacket
{
	private final Clan _clan;
	private final SubPledge _pledge;
	private final String _name;
	private final String _leaderName;
	private final Collection<ClanMember> _members;
	private final int _pledgeId;
	private final boolean _isSubPledge;
	
	private PledgeShowMemberListAll(Clan clan, SubPledge pledge, boolean isSubPledge)
	{
		_clan = clan;
		_pledge = pledge;
		_pledgeId = _pledge == null ? 0 : _pledge.getId();
		_leaderName = pledge == null ? clan.getLeaderName() : CharNameTable.getInstance().getNameById(pledge.getLeaderId());
		_name = pledge == null ? clan.getName() : pledge.getName();
		_members = _clan.getMembers();
		_isSubPledge = isSubPledge;
	}
	
	public static void sendAllTo(Player player)
	{
		final Clan clan = player.getClan();
		if (clan != null)
		{
			for (SubPledge subPledge : clan.getAllSubPledges())
			{
				player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
			}
			player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(this);
		writeInt(!_isSubPledge);
		writeInt(_clan.getId());
		writeInt(Config.SERVER_ID);
		writeInt(_pledgeId);
		writeString(_name);
		writeString(_leaderName);
		writeInt(_clan.getCrestId()); // crest id .. is used again
		writeInt(_clan.getLevel());
		writeInt(_clan.getCastleId());
		writeInt(0);
		writeInt(_clan.getHideoutId());
		writeInt(_clan.getFortId());
		writeInt(_clan.getRank());
		writeInt(_clan.getReputationScore());
		writeInt(0); // 0
		writeInt(0); // 0
		writeInt(_clan.getAllyId());
		writeString(_clan.getAllyName());
		writeInt(_clan.getAllyCrestId());
		writeInt(_clan.isAtWar()); // new c3
		writeInt(0); // Territory castle ID
		writeInt(_clan.getSubPledgeMembersCount(_pledgeId));
		for (ClanMember m : _members)
		{
			if (m.getPledgeType() != _pledgeId)
			{
				continue;
			}
			writeString(m.getName());
			writeInt(m.getLevel());
			writeInt(m.getClassId());
			final Player player = m.getPlayer();
			if (player != null)
			{
				writeInt(player.getAppearance().isFemale()); // no visible effect
				writeInt(player.getRace().ordinal()); // writeInt(1);
			}
			else
			{
				writeInt(1); // no visible effect
				writeInt(1); // writeInt(1);
			}
			writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
			writeInt(m.getSponsor() != 0);
			writeByte(m.getOnlineStatus());
		}
	}
}
