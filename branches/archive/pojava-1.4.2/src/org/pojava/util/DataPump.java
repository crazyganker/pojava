package org.pojava.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * The DataPump class was developed primarily for supporting Runtime.getRuntime().exec(...),
 * where you may need to gather from stdout and stderr simultaneously. Handling results using an
 * additional thread or two could prevent an app from locking up while waiting for you to read
 * from one of those two pipes. If you exec a single-threaded app that completely fills one 4k
 * pipe while you're waiting for data on the other pipe, it can block and may never get around
 * to either writing to or closing the pipe you're reading-- a classic deadlock preventable by
 * threading.
 * 
 * @author John Pile
 */
public class DataPump extends Thread {
    /**
     * Source of data stream
     */
    private InputStream in;

    /**
     * Destination of data stream (often intermediate)
     */
    private OutputStream out;

    /**
     * Holding pond
     */
    private StringBuffer textBuffer;

    /**
     * Connect pump from an Input Stream to an Output Stream
     * 
     * @param dataSource
     *            Data Source
     * @param dataDest
     *            Data Destination
     */
    public DataPump(final InputStream dataSource, final OutputStream dataDest) {
        this.in = dataSource;
        this.out = dataDest;
    }

    /**
     * Connect pump from an Input Stream to a String Buffer
     * 
     * @param dataSource
     *            Data Source
     * @param buffer
     *            Data Buffer (holding pond)
     */
    public DataPump(final InputStream dataSource, final StringBuffer buffer) {
        this.in = dataSource;
        this.textBuffer = buffer;
    }

    /**
     * Connectors are in place. Begin moving data.
     */
    public final void run() {
        try {
            if (out != null) {
                DataPump.pump(in, out);
            } else if (textBuffer != null) {
                DataPump.pump(in, textBuffer);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            // Just eat the exception. You're in a secondary stream.
        }
    }

    /**
     * This pump pulls data from one stream, pushing it to another.
     * 
     * @param in
     *            Data Source
     * @param out
     *            Data Destination
     * @throws IOException
     *             if read/write fails
     */
    public static void pump(final InputStream in, final OutputStream out) throws IOException {
        BufferedInputStream buffer = new BufferedInputStream(in);
        final int bucketSize = 1024;
        int drawn;
        byte[] bucket = new byte[bucketSize];
        try {
            while ((drawn = buffer.read(bucket)) >= 0) {
                out.write(bucket, 0, drawn);
            }
        } finally {
            if (buffer != null) {
                buffer.close();
            }
        }
    }

    /**
     * This pump extracts text from an input stream into a StringBuffer.
     * 
     * @param in
     *            Data Source
     * @param buffer
     *            Holding Buffer
     * @throws IOException
     *             if read/write fails
     */
    public static void pump(final InputStream in, final StringBuffer buffer) throws IOException {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
        final int bucketSize = 1024;
        int drawn;
        char[] bucket = new char[bucketSize];
        try {
            while ((drawn = bReader.read(bucket)) >= 0) {
                buffer.append(bucket, 0, drawn);
            }
        } finally {
            if (bReader != null) {
                bReader.close();
            }
        }
    }

}