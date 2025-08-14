package com.ferra13671.TextureUtils.loader;

import com.ferra13671.TextureUtils.*;
import com.ferra13671.TextureUtils.Utils.Utils;
import com.ferra13671.TextureUtils.builder.GLTextureInfo;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import com.ferra13671.TextureUtils.texture.ColorMode;
import com.ferra13671.TextureUtils.texture.TextureFiltering;
import com.ferra13671.TextureUtils.texture.TextureWrapping;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public final class TextureLoaders {
    public static Function<InputStream, TextureLoader> INPUT_STREAM = inputStream -> new TextureLoader() {
        @Override
        public GLTextureInfo load(ColorMode colorMode, TextureFiltering filtering, TextureWrapping wrapping) throws Exception {
            ByteBuffer buffer = Utils.readStream(inputStream);
            buffer.rewind();

            AtomicReference<GLTextureInfo> glTextureInfo = new AtomicReference<>(null);
            Utils.tryGenerate(buffer, inputStream, memoryStack -> {
                IntBuffer xBuffer = memoryStack.mallocInt(1);
                IntBuffer yBuffer = memoryStack.mallocInt(1);
                IntBuffer channelsBuffer = memoryStack.mallocInt(1);
                ByteBuffer byteBuffer = STBImage.stbi_load_from_memory(buffer, xBuffer, yBuffer, channelsBuffer, colorMode == ColorMode.RGBA ? 4 : 3);

                if (byteBuffer != null)
                    glTextureInfo.set(new GLTextureInfo(byteBuffer, xBuffer.get(0), yBuffer.get(0), colorMode, filtering, wrapping, isUsingStb()));
            });
            return glTextureInfo.get();
        }

        @Override
        public boolean isUsingStb() {
            return true;
        }
    };
    public static BiFunction<String, PathMode, TextureLoader> PATH = (path, pathMode) -> INPUT_STREAM.apply(pathMode.streamCreateFunction.apply(path));
    public static Function<URL, TextureLoader> URL = url -> {
        try {
            return INPUT_STREAM.apply(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
    public static Function<BufferedImage, TextureLoader> BUFFERED_IMAGE = bufferedImage -> new TextureLoader() {
        @Override
        public GLTextureInfo load(ColorMode colorMode, TextureFiltering filtering, TextureWrapping wrapping) {
            GLTextureInfo glTextureInfo;

            int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());

            ByteBuffer byteBuffer = MemoryUtil.memAlloc((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    int pixel = pixels[y * bufferedImage.getWidth() + x];
                    byteBuffer.put(Utils.getRed(pixel));
                    byteBuffer.put(Utils.getGreen(pixel));
                    byteBuffer.put(Utils.getBlue(pixel));
                    byteBuffer.put(Utils.getAlpha(pixel));
                }
            }
            byteBuffer.flip();

            glTextureInfo = new GLTextureInfo(byteBuffer, bufferedImage.getWidth(), bufferedImage.getHeight(), colorMode, filtering, wrapping, isUsingStb());

            return glTextureInfo;
        }

        @Override
        public boolean isUsingStb() {
            return false;
        }
    };
}
