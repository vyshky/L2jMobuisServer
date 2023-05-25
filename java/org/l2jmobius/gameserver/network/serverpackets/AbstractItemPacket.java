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

import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.enums.ItemListType;
import org.l2jmobius.gameserver.model.ItemInfo;
import org.l2jmobius.gameserver.model.TradeItem;
import org.l2jmobius.gameserver.model.buylist.Product;
import org.l2jmobius.gameserver.model.item.WarehouseItem;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.PlayerInventory;

/**
 * @author UnAfraid
 */
public abstract class AbstractItemPacket extends AbstractMaskPacket<ItemListType>
{
	private static final byte[] MASKS =
	{
		0x00
	};
	
	@Override
	protected byte[] getMasks()
	{
		return MASKS;
	}
	
	protected void writeItem(TradeItem item)
	{
		writeItem(new ItemInfo(item));
	}
	
	protected void writeItem(WarehouseItem item)
	{
		writeItem(new ItemInfo(item));
	}
	
	protected void writeItem(Item item)
	{
		writeItem(new ItemInfo(item));
	}
	
	protected void writeItem(Product item)
	{
		writeItem(new ItemInfo(item));
	}
	
	protected void writeItem(ItemInfo item)
	{
		final int mask = calculateMask(item);
		// cddcQcchQccddc
		writeByte(mask);
		writeInt(item.getObjectId()); // ObjectId
		writeInt(item.getItem().getDisplayId()); // ItemId
		writeByte(item.getItem().isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocation()); // T1
		writeLong(item.getCount()); // Quantity
		writeByte(item.getItem().getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
		writeByte(item.getCustomType1()); // Filler (always 0)
		writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
		writeLong(item.getItem().getBodyPart()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
		writeByte(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
		writeByte(item.getCustomType2()); // Pet name exists or not shown in control item
		writeInt(item.getMana());
		writeInt(item.getTime());
		writeByte(item.isAvailable()); // GOD Item enabled = 1 disabled (red) = 0
		if (containsMask(mask, ItemListType.AUGMENT_BONUS))
		{
			writeItemAugment(item);
		}
		if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE))
		{
			writeItemElemental(item);
		}
		if (containsMask(mask, ItemListType.ENCHANT_EFFECT))
		{
			writeItemEnchantEffect(item);
		}
		if (containsMask(mask, ItemListType.VISUAL_ID))
		{
			writeInt(item.getVisualId()); // Item remodel visual ID
		}
	}
	
	protected static int calculateMask(ItemInfo item)
	{
		int mask = 0;
		if (item.getAugmentation() != null)
		{
			mask |= ItemListType.AUGMENT_BONUS.getMask();
		}
		if ((item.getAttackElementType() >= 0) || (item.getAttributeDefence(AttributeType.FIRE) > 0) || (item.getAttributeDefence(AttributeType.WATER) > 0) || (item.getAttributeDefence(AttributeType.WIND) > 0) || (item.getAttributeDefence(AttributeType.EARTH) > 0) || (item.getAttributeDefence(AttributeType.HOLY) > 0) || (item.getAttributeDefence(AttributeType.DARK) > 0))
		{
			mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
		}
		if (item.getEnchantOptions() != null)
		{
			for (int id : item.getEnchantOptions())
			{
				if (id > 0)
				{
					mask |= ItemListType.ENCHANT_EFFECT.getMask();
					break;
				}
			}
		}
		if (item.getVisualId() > 0)
		{
			mask |= ItemListType.VISUAL_ID.getMask();
		}
		return mask;
	}
	
	protected void writeItemAugment(ItemInfo item)
	{
		if ((item != null) && (item.getAugmentation() != null))
		{
			writeInt(item.getAugmentation().getOption1Id());
			writeInt(item.getAugmentation().getOption2Id());
		}
		else
		{
			writeInt(0);
			writeInt(0);
		}
	}
	
	protected void writeItemElementalAndEnchant(ItemInfo item)
	{
		writeItemElemental(item);
		writeItemEnchantEffect(item);
	}
	
	protected void writeItemElemental(ItemInfo item)
	{
		if (item != null)
		{
			writeShort(item.getAttackElementType());
			writeShort(item.getAttackElementPower());
			writeShort(item.getAttributeDefence(AttributeType.FIRE));
			writeShort(item.getAttributeDefence(AttributeType.WATER));
			writeShort(item.getAttributeDefence(AttributeType.WIND));
			writeShort(item.getAttributeDefence(AttributeType.EARTH));
			writeShort(item.getAttributeDefence(AttributeType.HOLY));
			writeShort(item.getAttributeDefence(AttributeType.DARK));
		}
		else
		{
			writeShort(0);
			writeShort(0);
			writeShort(0);
			writeShort(0);
			writeShort(0);
			writeShort(0);
			writeShort(0);
			writeShort(0);
		}
	}
	
	protected void writeItemEnchantEffect(ItemInfo item)
	{
		// Enchant Effects
		for (int op : item.getEnchantOptions())
		{
			writeInt(op);
		}
	}
	
	protected void writeInventoryBlock(PlayerInventory inventory)
	{
		if (inventory.hasInventoryBlock())
		{
			writeShort(inventory.getBlockItems().size());
			writeByte(inventory.getBlockMode().getClientId());
			for (int id : inventory.getBlockItems())
			{
				writeInt(id);
			}
		}
		else
		{
			writeShort(0);
		}
	}
}
