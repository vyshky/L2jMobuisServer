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

import org.l2jmobius.gameserver.model.Shortcut;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ShortCutRegister extends ServerPacket
{
	private final Shortcut _shortcut;
	
	/**
	 * Register new skill shortcut
	 * @param shortcut
	 */
	public ShortCutRegister(Shortcut shortcut)
	{
		_shortcut = shortcut;
	}
	
	@Override
	public void write()
	{
		ServerPackets.SHORT_CUT_REGISTER.writeId(this);
		writeInt(_shortcut.getType().ordinal());
		writeInt(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
		switch (_shortcut.getType())
		{
			case ITEM:
			{
				writeInt(_shortcut.getId());
				writeInt(_shortcut.getCharacterType());
				writeInt(_shortcut.getSharedReuseGroup());
				writeInt(0); // unknown
				writeInt(0); // unknown
				writeInt(0); // item augment id
				writeInt(0); // TODO: Find me, item visual id ?
				break;
			}
			case SKILL:
			{
				writeInt(_shortcut.getId());
				writeShort(_shortcut.getLevel());
				writeShort(_shortcut.getSubLevel());
				writeInt(_shortcut.getSharedReuseGroup());
				writeByte(0); // C5
				writeInt(_shortcut.getCharacterType());
				writeInt(0); // TODO: Find me
				writeInt(0); // TODO: Find me
				break;
			}
			case ACTION:
			case MACRO:
			case RECIPE:
			case BOOKMARK:
			{
				writeInt(_shortcut.getId());
				writeInt(_shortcut.getCharacterType());
			}
		}
	}
}
