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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.model.stats.BaseStat;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * This server packet sends the player's henna information using the Game Master's UI.
 * @author KenM, Zoey76
 */
public class GMHennaInfo extends ServerPacket
{
	private final Player _player;
	private final List<Henna> _hennas = new ArrayList<>();
	
	public GMHennaInfo(Player player)
	{
		_player = player;
		for (Henna henna : _player.getHennaList())
		{
			if (henna != null)
			{
				_hennas.add(henna);
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.GMHENNA_INFO.writeId(this);
		writeShort(_player.getHennaValue(BaseStat.INT)); // equip INT
		writeShort(_player.getHennaValue(BaseStat.STR)); // equip STR
		writeShort(_player.getHennaValue(BaseStat.CON)); // equip CON
		writeShort(_player.getHennaValue(BaseStat.MEN)); // equip MEN
		writeShort(_player.getHennaValue(BaseStat.DEX)); // equip DEX
		writeShort(_player.getHennaValue(BaseStat.WIT)); // equip WIT
		writeShort(0); // equip LUC
		writeShort(0); // equip CHA
		writeInt(3); // Slots
		writeInt(_hennas.size()); // Size
		for (Henna henna : _hennas)
		{
			writeInt(henna.getDyeId());
			writeInt(1);
		}
		writeInt(0);
		writeInt(0);
		writeInt(0);
	}
}
