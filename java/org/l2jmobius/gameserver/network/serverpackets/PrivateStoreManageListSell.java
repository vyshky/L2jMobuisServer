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

import org.l2jmobius.gameserver.model.TradeItem;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PrivateStoreManageListSell extends AbstractItemPacket
{
	private final int _objId;
	private final long _playerAdena;
	private final boolean _packageSale;
	private final Collection<TradeItem> _itemList;
	private final Collection<TradeItem> _sellList;
	
	public PrivateStoreManageListSell(Player player, boolean isPackageSale)
	{
		_objId = player.getObjectId();
		_playerAdena = player.getAdena();
		player.getSellList().updateItems();
		_packageSale = isPackageSale;
		_itemList = player.getInventory().getAvailableItems(player.getSellList());
		_sellList = player.getSellList().getItems();
	}
	
	@Override
	public void write()
	{
		ServerPackets.PRIVATE_STORE_MANAGE_LIST.writeId(this);
		writeInt(_objId);
		writeInt(_packageSale); // Package sell
		writeLong(_playerAdena);
		writeInt(_itemList.size()); // for potential sells
		for (TradeItem item : _itemList)
		{
			writeItem(item);
			writeLong(item.getItem().getReferencePrice() * 2);
		}
		writeInt(_sellList.size()); // count for any items already added for sell
		for (TradeItem item : _sellList)
		{
			writeItem(item);
			writeLong(item.getPrice());
			writeLong(item.getItem().getReferencePrice() * 2);
		}
	}
}
