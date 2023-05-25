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

import java.util.Set;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExUserInfoAbnormalVisualEffect extends ServerPacket
{
	private final Player _player;
	
	public ExUserInfoAbnormalVisualEffect(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT.writeId(this);
		writeInt(_player.getObjectId());
		writeInt(_player.getTransformationId());
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _player.getEffectList().getCurrentAbnormalVisualEffects();
		final boolean isInvisible = _player.isInvisible();
		writeInt(abnormalVisualEffects.size() + (isInvisible ? 1 : 0));
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			writeShort(abnormalVisualEffect.getClientId());
		}
		if (isInvisible)
		{
			writeShort(AbnormalVisualEffect.STEALTH.getClientId());
		}
	}
}
