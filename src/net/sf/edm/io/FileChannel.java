/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author usien
 */
public class FileChannel {

    private java.nio.channels.FileChannel channel;
    private ByteBuffer[] buffer;
    private long[] indice;
    private int bufferSize;
    private boolean pendingData = false;
    private boolean[] indiceSet;

    public FileChannel(String file, int bufferSize) throws FileNotFoundException {
        this.bufferSize = bufferSize;
        channel = new RandomAccessFile(file, "rw").getChannel();
    }

    public void setBuffersCount(int count) {
        buffer = new ByteBuffer[count];
        indice = new long[count];
        indiceSet = new boolean[count];
        Arrays.fill(indiceSet, false);

        for (int x = 0; x < buffer.length; x++) {
            buffer[x] = ByteBuffer.allocate(bufferSize);
        }
    }

    public void setIndice(int id, long value) {
        indice[id] = value;
        indiceSet[id] = true;
    }

    public boolean isIndiceSet(int id) {
        return indiceSet[id];
    }

    public void write(int id, ByteBuffer data) throws IOException {
        data.flip();

        try {
            buffer[id].put(data);
        } catch (BufferOverflowException ex) {
            flush();
            write(id, data);
        }

        pendingData = true;
        int limit = 0;

        for (ByteBuffer locBuffer : buffer) {
            limit += locBuffer.limit();
        }

        if (limit >= bufferSize) {
            flush();
        }
    }

    public void flush() throws IOException {
        for (int x = 0; x < buffer.length; x++) {
            channel.position(indice[x]);
            buffer[x].flip();
            channel.write(buffer[x]);
            System.out.println(buffer[x].limit() + " bytes written at " + indice[x] + "(" + (buffer[x].limit() + indice[x] + ")"));
            indice[x] = channel.position();
            buffer[x].clear();
        }

        //channel.force(false);
        pendingData = false;
    }

    public void close() throws IOException {
        if (pendingData) {
            flush();
        }
        channel.close();
    }
}
