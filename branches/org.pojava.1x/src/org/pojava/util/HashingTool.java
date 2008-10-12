package org.pojava.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashes are numbers, byte arrays or strings that non-uniquely identify data by
 * applying some calculation against a set of data in order to derive a
 * relatively small signature. Hashes are useful for lookups, data distribution,
 * and data integrity.
 * 
 * @author John Pile
 * 
 */
public class HashingTool {

	public static byte[] md5Hash(final byte[] hashMe) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			// throw new SwallowedException("Programmer unable to spell 'MD5'.");
		}
		md5.update(hashMe);
		return md5.digest();
	}

	/**
	 * Returns an md5 hash as text.
	 * 
	 * @param hashMe
	 *            String to hash.
	 * @return md5 hash of string.
	 */
	public static String md5Hash(final String hashMe) {
		StringBuffer sb = new StringBuffer();
		final int byteMask = 0xFF;
		final int twoDigit = 0x10;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(hashMe.getBytes());
			/* A pure md5 hash is a binary array of 128 bits */
			byte[] array = md5.digest();
			/* Format the byte array into a human-readable string of 32 hex chars */
			for (int i = 0; i < array.length; ++i) {
				int b = array[i] & byteMask;
				if (b < twoDigit) {
					sb.append('0');
				}
				sb.append(Integer.toHexString(b));
			}
		} catch (NoSuchAlgorithmException e) {
			// Programmer misspelled MD5?
			e.printStackTrace();
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * An implementation of Jenkin's One-at-a-Time Hash which
	 * returns a well-distributed 32-bit hash of any string.
	 * @author John Pile 
	 */
	public static int oatHash(final String hashMe) {
		return oatHash(hashMe.getBytes());
	}

	/**
	 *  An implementation of Jenkin's One-at-a-Time Hash Returns a
	 *  well-distributed 32-bit hash of any byte array.
	 *  @author John Pile
	 */
	public static int oatHash(byte[] hashMe) {
		int hash = 0;
		for (int i = 0; i < hashMe.length; i++) {
			hash += hashMe[i];
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}

}
