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

public class RecipeShopItemInfo extends ServerPacket
{
	private final Player _player;
	private final int _recipeId;
	
	public RecipeShopItemInfo(Player player, int recipeId)
	{
		_player = player;
		_recipeId = recipeId;
	}
	
	@Override
	public void write()
	{
		ServerPackets.RECIPE_SHOP_ITEM_INFO.writeId(this);
		writeInt(_player.getObjectId());
		writeInt(_recipeId);
		writeInt((int) _player.getCurrentMp());
		writeInt(_player.getMaxMp());
		writeInt(0xffffffff);
		writeLong(0);
		writeByte(0); // Trigger offering window if 1
		writeLong(0);
	}
}
