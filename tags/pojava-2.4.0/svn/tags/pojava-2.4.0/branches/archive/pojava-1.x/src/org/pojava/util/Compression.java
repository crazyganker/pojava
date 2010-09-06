package org.pojava.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compression {

	private final Deflater compressor = new Deflater();

	private final Inflater decompressor = new Inflater();

	public Compression() {
		// Default constructor
		compressor.setLevel(Deflater.BEST_SPEED);
	}

	/**
	 * Compress using the ubiquitous "zip" compression.
	 * 
	 * @param input
	 *            Usually myString.getBytes()
	 * @return Compressed version of byte array.
	 * @author John Pile
	 */
	public byte[] compress(byte[] input) {
		compressor.reset();
		if (input.length==0) return input;
		compressor.setInput(input);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		return bos.toByteArray();
	}

	/**
	 * Decompress data that has already been compressed.
	 * 
	 * @param compressedData
	 *            byte array of zipped data
	 * @return original data contained in a byte array.
	 * @author John Pile
	 */
	public byte[] decompress(byte[] compressedData) throws DataFormatException {
		decompressor.reset();
		decompressor.setInput(compressedData);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				compressedData.length);
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			int count = decompressor.inflate(buf);
			if (count==0) {
				break;
			}
			bos.write(buf, 0, count);
		}
		return bos.toByteArray();
	}

}
