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

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ValidateLocationInVehicle extends ServerPacket
{
	private final int _objectId;
	private final int _boatObjId;
	private final int _heading;
	private final Location _pos;
	
	public ValidateLocationInVehicle(Player player)
	{
		_objectId = player.getObjectId();
		_boatObjId = player.getBoat().getObjectId();
		_heading = player.getHeading();
		_pos = player.getInVehiclePosition();
	}
	
	@Override
	public void write()
	{
		ServerPackets.VALIDATE_LOCATION_IN_VEHICLE.writeId(this);
		writeInt(_objectId);
		writeInt(_boatObjId);
		writeInt(_pos.getX());
		writeInt(_pos.getY());
		writeInt(_pos.getZ());
		writeInt(_heading);
	}
}
