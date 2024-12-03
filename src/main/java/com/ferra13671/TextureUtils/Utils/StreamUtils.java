package com.ferra13671.TextureUtils.Utils;

import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

/**
 * @author Ferra13671
 * @LastUpdate 1.0
 */

public class StreamUtils {

    public static ByteBuffer readStream(InputStream stream) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(stream);
        if (readableByteChannel instanceof SeekableByteChannel) {
            SeekableByteChannel channel = (SeekableByteChannel) readableByteChannel;
            return readChannel(readableByteChannel, (int) channel.size() + 1);
        } else {
            return readChannel(readableByteChannel, 8192);
        }
    }

    public static ByteBuffer readChannel(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(bufSize);

        try {
            while(channel.read(byteBuffer) != -1) {
                if (!byteBuffer.hasRemaining()) {
                    byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
                }
            }

            return byteBuffer;
        } catch (IOException var4) {
            MemoryUtil.memFree(byteBuffer);
            throw var4;
        }
    }
}
