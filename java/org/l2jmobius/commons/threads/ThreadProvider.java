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
package org.l2jmobius.commons.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mobius
 */
public class ThreadProvider implements ThreadFactory
{
	private final AtomicInteger _id = new AtomicInteger();
	private final String _prefix;
	
	public ThreadProvider(String prefix)
	{
		_prefix = prefix + " ";
	}
	
	@Override
	public Thread newThread(Runnable runnable)
	{
		final Thread thread = new Thread(runnable, _prefix + _id.incrementAndGet());
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.setDaemon(false);
		return thread;
	}
}
