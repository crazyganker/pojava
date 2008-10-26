package org.pojava.util;

/**
 * A class for reversible encodings. Typical encodings are designed to represent
 * binary data in a more portable or printable format. Currently supported
 * encodings include Base64, Hexadecimal.
 * 
 * @author John Pile
 */
public class EncodingTool {

	// Mapping table for converting a 4-bit nybble to hex characters.
	private static final char[] hexmap = "0123456789abcdef".toCharArray();

	private static final char[] e64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();
	private static final byte[] d64 = initD64();

	// Index the e64 offsets from their values
	private static byte[] initD64() {
		byte[] decoderMap = new byte[128];
		for (int i = 0; i < 64; i++) {
			decoderMap[e64[i]] = (byte) i;
		}
		return decoderMap;
	}

	/**
	 * Encode binary data into a Base-64 array of printable characters.
	 * 
	 * @param src
	 * @return Base-64 encoded string
	 */
	public static char[] base64Encode(byte[] src) {
		int unpadded = (src.length * 8 + 5) / 6;
		int padding = 4 - (unpadded % 4);
		int d = 0;
		int e = 3;
		long buffer = 0;
		if (padding == 4)
			padding = 0;
		char[] encoded = new char[unpadded + padding];

		while (d < src.length) {
			// Push into buffer in 8-bit chunks
			for (int i = 0; i < 3; i++) {
				buffer <<= 8;
				buffer |= (d < src.length ? src[d++] : 0);
			}
			// Pop from buffer in 6-bit chunks
			for (int i = 0; i < 4; i++) {
				encoded[e--] = e64[(int) (buffer & 0x3f)];
				buffer >>>= 6;
			}
			e += 8;
		}
		while (padding > 0) {
			encoded[unpadded + --padding] = '=';
		}
		return encoded;
	}

	/**
	 * Decode a String from Base64 format. It strips whitespace before decoding.
	 * 
	 * @param s
	 *            a Base64 String to be decoded.
	 * @return An array containing the decoded data.
	 * @throws IllegalArgumentException
	 *             if the input is not valid Base64 encoded data.
	 */
	public static byte[] base64Decode(String s) {
		if (s == null) {
			return new byte[0];
		}
		return base64Decode(StringTool.stripWhitespace(s).toCharArray());
	}

	/**
	 * Decode a Base64 message back to its original byte array.
	 * 
	 * @param encoded
	 *            a character array containing the Base64 encoded data.
	 * @return A byte array containing the decoded data.
	 */
	public static byte[] base64Decode(char[] encoded) {
		int length = encoded.length * 6 / 8;
		if (encoded.length > 0 && encoded[encoded.length - 1] == '=') {
			length--;
			if (encoded.length > 1 && encoded[encoded.length - 2] == '=') {
				length--;
			}
			if (encoded.length > 2 && encoded[encoded.length - 3] == '=') {
				length--;
			}
		}
		byte[] decoded = new byte[length];
		long buffer = 0x00000000;
		int e = 0;
		int d = 0;
		while (e < encoded.length) {
			// Enqueue 24 bits into a buffer
			buffer = d64[encoded[e++]] << 18;
			buffer |= (d64[encoded[e++]] << 12);
			buffer |= (d64[encoded[e++]] << 6);
			buffer |= (d64[encoded[e++]]);
			// Dequeue 24 bits off of the buffer
			decoded[d++] = (byte) ((buffer & 0x00FF0000) >>> 16);
			if (d < length) {
				decoded[d++] = (byte) ((buffer & 0x0000FF00) >>> 8);
			}
			if (d < length) {
				decoded[d++] = (byte) (buffer & 0x000000FF);
			}
		}
		return decoded;
	}

	/**
	 * Interpret a hex character as a nybble.
	 * 
	 * @param c
	 *            hexadecimal character to encode
	 * @return integer between 0 and 15.
	 */
	private static int hex2int(char c) {
		int nybble = 0;
		if (c >= '0' && c <= '9') {
			nybble = c - '0';
		} else if (c >= 'a' && c <= 'f') {
			nybble = 10 + c - 'a';
		} else if (c >= 'A' && c <= 'F') {
			nybble = 10 + c - 'A';
		} else {
			throw new IllegalArgumentException(
					"Hex value MUST be in range [0-9a-fA-F]");
		}
		return nybble;
	}

	/**
	 * Convert a hex-encoded string back to a byte array. This String version
	 * strips whitespace before decoding.
	 * 
	 * @param hex
	 * @return decoded array of bytes
	 */
	public static byte[] hexDecode(String hex) {
		if (hex == null) {
			return new byte[0];
		}
		return hexDecode(StringTool.stripWhitespace(hex).toCharArray());
	}

	/**
	 * Convert a hex-encoded character array back to a byte array.
	 * 
	 * @param hexChars
	 *            array of hex-encoded characters
	 * @return original byte array
	 */
	public static byte[] hexDecode(char[] hexChars) {
		int byteCt = hexChars.length / 2;
		if (2 * byteCt != hexChars.length) {
			throw new IllegalArgumentException(
					"Hex value MUST be two digits per byte.");
		}
		byte[] bytes = new byte[byteCt];
		for (int i = 0; i < byteCt; i++) {
			bytes[i] = (byte) (hex2int(hexChars[2 * i]) << 4 | hex2int(hexChars[2 * i + 1]));
		}
		return bytes;
	}

	/**
	 * Output a hex-encoded representation of a byte array
	 * 
	 * @param bytes
	 *            of binary data to encode
	 * @return Hex encoded representation of binary data
	 */
	public static String hexEncode(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexmap[0x0F & (bytes[i] >>> 4)]);
			sb.append(hexmap[0x0F & bytes[i]]);
		}
		return sb.toString();
	}
}
