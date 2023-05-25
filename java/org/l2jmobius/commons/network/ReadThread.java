package org.l2jmobius.commons.network;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author Pantelis Andrianakis
 * @since September 7th 2020
 * @param <E> extends NetClient
 */
public class ReadThread<E extends NetClient> implements Runnable
{
	private final ByteBuffer _sizeBuffer = ByteBuffer.allocate(2); // Reusable size buffer.
	private final ByteBuffer _pendingSizeBuffer = ByteBuffer.allocate(1); // Reusable pending size buffer.
	private final Set<E> _pool;
	
	public ReadThread(Set<E> pool)
	{
		_pool = pool;
	}
	
	@Override
	public void run()
	{
		long executionStart;
		long currentTime;
		while (true)
		{
			executionStart = System.currentTimeMillis();
			
			// No need to iterate when pool is empty.
			if (!_pool.isEmpty())
			{
				// Iterate client pool.
				ITERATE: for (E client : _pool)
				{
					try
					{
						final SocketChannel channel = client.getChannel();
						if (channel == null) // Unexpected disconnection?
						{
							// Null SocketChannel: client
							onDisconnection(client);
							continue ITERATE;
						}
						
						// Continue read if there is a pending ByteBuffer.
						final ByteBuffer pendingByteBuffer = client.getPendingByteBuffer();
						if (pendingByteBuffer != null)
						{
							// Allocate an additional ByteBuffer based on pending packet size.
							final int pendingPacketSize = client.getPendingPacketSize();
							final ByteBuffer additionalData = ByteBuffer.allocate(pendingPacketSize - pendingByteBuffer.position());
							switch (channel.read(additionalData))
							{
								// Disconnected.
								case -1:
								{
									onDisconnection(client);
									continue ITERATE;
								}
								// Nothing read.
								case 0:
								{
									continue ITERATE;
								}
								// Data was read.
								default:
								{
									// Merge additional read data.
									pendingByteBuffer.put(pendingByteBuffer.position(), additionalData, 0, additionalData.position());
									pendingByteBuffer.position(pendingByteBuffer.position() + additionalData.position());
									
									// Read was complete.
									if (pendingByteBuffer.position() >= pendingPacketSize)
									{
										// Add packet data to client.
										client.addPacketData(pendingByteBuffer.array());
										client.setPendingByteBuffer(null);
									}
								}
							}
							continue ITERATE;
						}
						
						// Read incoming packet size (short).
						_sizeBuffer.clear();
						switch (channel.read(_sizeBuffer))
						{
							// Disconnected.
							case -1:
							{
								onDisconnection(client);
								continue ITERATE;
							}
							// Nothing read.
							case 0:
							{
								continue ITERATE;
							}
							// Need to read two bytes to calculate size.
							case 1:
							{
								int attempt = 0; // Keep it under 10 attempts (100ms).
								COMPLETE_SIZE_READ: while ((attempt++ < 10) && (_sizeBuffer.position() < 2))
								{
									// Wait for pending data.
									try
									{
										Thread.sleep(10);
									}
									catch (Exception ignored)
									{
									}
									
									// Try to read the missing extra byte.
									_pendingSizeBuffer.clear();
									switch (channel.read(_pendingSizeBuffer))
									{
										// Disconnected.
										case -1:
										{
											onDisconnection(client);
											continue ITERATE;
										}
										// Nothing read.
										case 0:
										{
											continue COMPLETE_SIZE_READ;
										}
										// Merge additional read byte.
										default:
										{
											_sizeBuffer.put(1, _pendingSizeBuffer, 0, 1);
											_sizeBuffer.position(2);
										}
									}
								}
								
								// Read failed.
								if (_sizeBuffer.position() < 2)
								{
									onDisconnection(client);
									continue ITERATE;
								}
								
								// Fallthrough.
							}
							// Read actual packet bytes.
							default:
							{
								// Allocate a new ByteBuffer based on packet size read.
								final int packetSize = calculatePacketSize();
								final ByteBuffer packetByteBuffer = ByteBuffer.allocate(packetSize);
								switch (channel.read(packetByteBuffer))
								{
									// Disconnected.
									case -1:
									{
										onDisconnection(client);
										continue ITERATE;
									}
									// Nothing read.
									case 0:
									{
										continue ITERATE;
									}
									// Send data read to the client packet queue.
									default:
									{
										// Read was not complete.
										if (packetByteBuffer.position() < packetSize)
										{
											client.setPendingByteBuffer(packetByteBuffer);
											client.setPendingPacketSize(packetSize);
										}
										else // Add packet data to client.
										{
											client.addPacketData(packetByteBuffer.array());
										}
									}
								}
							}
						}
					}
					catch (SocketTimeoutException e)
					{
						onDisconnection(client);
					}
					catch (Exception e) // Unexpected disconnection?
					{
						onDisconnection(client);
					}
				}
			}
			
			// Prevent high CPU caused by repeatedly looping.
			currentTime = System.currentTimeMillis();
			if ((currentTime - executionStart) < 1)
			{
				try
				{
					Thread.sleep(1);
				}
				catch (Exception ignored)
				{
				}
			}
		}
	}
	
	private int calculatePacketSize()
	{
		_sizeBuffer.rewind();
		return ((_sizeBuffer.get() & 0xff) | ((_sizeBuffer.get() << 8) & 0xffff)) - 2;
	}
	
	private void onDisconnection(E client)
	{
		_pool.remove(client);
		client.onDisconnection();
	}
}
