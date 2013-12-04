/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 *
 * @author usien
 */
public class ByteChannelCodec {

    private ByteChannel channel;
    private static Charset charset = Charset.defaultCharset();

    public ByteChannelCodec(SocketChannel channel) {
        this.channel = channel;
    }

    public int write(String data) throws IOException {
        return channel.write(charset.encode(data));
    }

    public String readLine() throws IOException {
        ByteBuffer input = ByteBuffer.allocate(1);
        CharBuffer output;
        String line = "";
        char outChar;

        while (channel.read(input) != -1) {
            input.flip();
            output = charset.decode(input);
            outChar = output.get();

            if (outChar != '\n') {
                line += outChar;
            } else {
                break;
            }

            input.clear();
        }

        return line;
    }
}
