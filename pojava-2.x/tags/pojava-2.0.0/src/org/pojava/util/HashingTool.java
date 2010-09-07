package org.pojava.util;
/*
Copyright 2008-09 John Pile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Iterator;
import java.util.Set;

import org.pojava.exception.InconceivableException;

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

	/**
	 * Returns an md5 hash as text.
	 * 
	 * @param hashMe
	 *            String to hash.
	 * @return md5 hash of string.
	 */
	public static String md5Hash(final String hashMe) {
		return EncodingTool.hexEncode(hash(hashMe.getBytes(),
				HashingAlgorithm.MD5));
	}

	/**
	 * List of supported algorithms used for Exception message.
	 * @return Comma separated list of supported hashing algorithms.
	 */
	private static String supportedHashingAlgorithms() {
		Set<String> algorithms = Security.getAlgorithms("MessageDigest");
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> it = algorithms.iterator(); it.hasNext();) {
			sb.append(it.next().toString());
			sb.append(", ");
		}
		sb.setLength(Math.max(0, sb.length() - 2));
		return sb.toString();
	}

	/**
	 * Hash based on a relatively safe list of supported hashing algorithms.
	 * 
	 * @param hashMe bytes from which to calculate a hash
	 * @param alg algorithm used to calculate the hash
	 * @return byte array containing binary version of hash
	 */
	public static byte[] hash(final byte[] hashMe, HashingAlgorithm alg) {
		try {
			return hash(hashMe, alg.toString());
		} catch (NoSuchAlgorithmException ex) {
			throw new InconceivableException("Unsupported algorithm ["
					+ alg.toString() + "].  Must be one of "
					+ supportedHashingAlgorithms(), ex);
		}
	}

	/**
	 * Hash based on any algorithm named in
	 * Security.getAlgorithms("MessageDigest")
	 * 
	 * @param hashMe
	 * @param algorithm
	 * @return binary hash of a byte array
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] hash(final byte[] hashMe, String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest digest = null;
		digest = MessageDigest.getInstance(algorithm);
		digest.update(hashMe);
		return digest.digest();
	}

	/**
	 * An implementation of Jenkin's One-at-a-Time Hash which returns a
	 * well-distributed 32-bit hash of any string.
	 */
	public static int oatHash(final String hashMe) {
		return oatHash(hashMe.getBytes());
	}

	/**
	 * An implementation of Jenkin's One-at-a-Time Hash
	 * 
	 * @return A well-distributed 32-bit hash of any byte array.
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
