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

import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.model.RecipeList;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;

public class RecipeItemMakeInfo extends ServerPacket
{
	private final int _id;
	private final Player _player;
	private final boolean _success;
	
	public RecipeItemMakeInfo(int id, Player player, boolean success)
	{
		_id = id;
		_player = player;
		_success = success;
	}
	
	public RecipeItemMakeInfo(int id, Player player)
	{
		_id = id;
		_player = player;
		_success = true;
	}
	
	@Override
	public void write()
	{
		final RecipeList recipe = RecipeData.getInstance().getRecipeList(_id);
		if (recipe == null)
		{
			PacketLogger.info("Character: " + _player + ": Requested unexisting recipe with id = " + _id);
			return;
		}
		
		ServerPackets.RECIPE_ITEM_MAKE_INFO.writeId(this);
		writeInt(_id);
		writeInt(!recipe.isDwarvenRecipe()); // 0 = Dwarven - 1 = Common
		writeInt((int) _player.getCurrentMp());
		writeInt(_player.getMaxMp());
		writeInt(_success); // item creation success/failed
		writeByte(0);
		writeLong(0);
	}
}
