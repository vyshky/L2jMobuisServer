package org.l2jmobius.commons.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * @author Pantelis Andrianakis
 * @since September 7th 2020
 */
public class NetClient
{
	protected static final Logger LOGGER = Logger.getLogger(NetClient.class.getName());
	
	private String _ip;
	private SocketChannel _channel;
	private NetConfig _netConfig;
	private Queue<byte[]> _pendingPacketData;
	private ByteBuffer _pendingByteBuffer;
	private int _pendingPacketSize;
	
	/**
	 * Initialize the client.
	 * @param channel
	 * @param netConfig
	 */
	public void init(SocketChannel channel, NetConfig netConfig)
	{
		_channel = channel;
		_netConfig = netConfig;
		_pendingPacketData = new ConcurrentLinkedQueue<>();
		
		try
		{
			_ip = _channel.getRemoteAddress().toString();
			_ip = _ip.substring(1, _ip.lastIndexOf(':')); // Trim out /127.0.0.1:12345
		}
		catch (Exception ignored)
		{
		}
		
		// Client is ready for communication.
		onConnection();
	}
	
	/**
	 * Called when client is connected.
	 */
	public void onConnection()
	{
	}
	
	/**
	 * Called when client is disconnected.
	 */
	public void onDisconnection()
	{
	}
	
	/**
	 * Disconnect the client.
	 */
	public void disconnect()
	{
		if (_channel != null)
		{
			try
			{
				_channel.close();
				_channel = null;
			}
			catch (Exception ignored)
			{
			}
		}
		
		if (_pendingPacketData != null)
		{
			_pendingPacketData.clear();
		}
		
		if (_pendingByteBuffer != null)
		{
			_pendingByteBuffer = null;
		}
	}
	
	/**
	 * Add packet data to the queue.
	 * @param data
	 */
	public void addPacketData(byte[] data)
	{
		// Check packet flooding.
		final int size = _pendingPacketData.size();
		if (size >= _netConfig.getPacketQueueLimit())
		{
			if (_netConfig.isPacketFloodDisconnect())
			{
				disconnect();
				return;
			}
			
			if (_netConfig.isPacketFloodDrop())
			{
				if (_netConfig.isPacketFloodLogged() && ((size % _netConfig.getPacketQueueLimit()) == 0))
				{
					final StringBuilder sb = new StringBuilder();
					sb.append(this);
					sb.append(" packet queue size(");
					sb.append(size);
					sb.append(") exceeded limit(");
					sb.append(_netConfig.getPacketQueueLimit());
					sb.append(").");
					LOGGER.warning(sb.toString());
				}
				return;
			}
		}
		
		// Add to queue.
		_pendingPacketData.add(data);
	}
	
	/**
	 * @return the pending packet data.
	 */
	public Queue<byte[]> getPacketData()
	{
		return _pendingPacketData;
	}
	
	/**
	 * @return the pending read ByteBuffer.
	 */
	public ByteBuffer getPendingByteBuffer()
	{
		return _pendingByteBuffer;
	}
	
	/**
	 * Set the pending read ByteBuffer.
	 * @param pendingByteBuffer the pending read ByteBuffer.
	 */
	public void setPendingByteBuffer(ByteBuffer pendingByteBuffer)
	{
		_pendingByteBuffer = pendingByteBuffer;
	}
	
	/**
	 * @return the expected pending packet size.
	 */
	public int getPendingPacketSize()
	{
		return _pendingPacketSize;
	}
	
	/**
	 * Set the expected pending packet size.
	 * @param pendingPacketSize the expected packet size.
	 */
	public void setPendingPacketSize(int pendingPacketSize)
	{
		_pendingPacketSize = pendingPacketSize;
	}
	
	/**
	 * @return the Encryption of this client.
	 */
	public EncryptionInterface getEncryption()
	{
		return null;
	}
	
	/**
	 * @return the SocketChannel of this client.
	 */
	public SocketChannel getChannel()
	{
		return _channel;
	}
	
	/**
	 * @return the network configurations of this client.
	 */
	public NetConfig getNetConfig()
	{
		return _netConfig;
	}
	
	/**
	 * @return the IP address of this client.
	 */
	public String getIp()
	{
		return _ip;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" - IP: ");
		sb.append(_ip);
		sb.append("]");
		return sb.toString();
	}
}
