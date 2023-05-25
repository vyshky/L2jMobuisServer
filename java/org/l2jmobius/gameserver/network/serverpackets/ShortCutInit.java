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

import org.l2jmobius.gameserver.model.Shortcut;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ShortCutInit extends ServerPacket
{
	private Collection<Shortcut> _shortCuts;
	
	public ShortCutInit(Player player)
	{
		if (player == null)
		{
			return;
		}
		_shortCuts = player.getAllShortCuts();
	}
	
	@Override
	public void write()
	{
		ServerPackets.SHORT_CUT_INIT.writeId(this);
		writeInt(_shortCuts.size());
		for (Shortcut sc : _shortCuts)
		{
			writeInt(sc.getType().ordinal());
			writeInt(sc.getSlot() + (sc.getPage() * 12));
			switch (sc.getType())
			{
				case ITEM:
				{
					writeInt(sc.getId());
					writeInt(1); // Enabled or not
					writeInt(sc.getSharedReuseGroup());
					writeInt(0);
					writeInt(0);
					writeLong(0); // Augment id
					writeInt(0); // Visual id
					break;
				}
				case SKILL:
				{
					writeInt(sc.getId());
					writeShort(sc.getLevel());
					writeShort(sc.getSubLevel());
					writeInt(sc.getSharedReuseGroup());
					writeByte(0); // C5
					writeInt(1); // C6
					break;
				}
				case ACTION:
				case MACRO:
				case RECIPE:
				case BOOKMARK:
				{
					writeInt(sc.getId());
					writeInt(1); // C6
				}
			}
		}
	}
}
