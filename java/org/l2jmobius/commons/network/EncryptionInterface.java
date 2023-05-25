package org.l2jmobius.commons.network;

/**
 * @author Pantelis Andrianakis
 * @since October 4th 2022
 */
public interface EncryptionInterface
{
	default void encrypt(byte[] data, int offset, int size)
	{
	}
	
	default void decrypt(byte[] data, int offset, int size)
	{
	}
}
