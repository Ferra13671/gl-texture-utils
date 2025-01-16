package com.ferra13671.TextureUtils;

import com.ferra13671.TextureUtils.Controller.DefaultGlController;
import com.ferra13671.TextureUtils.Utils.StreamUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static com.ferra13671.TextureUtils.GLTextureSystem.glController;
import static org.lwjgl.stb.STBImage.nstbi_image_free;

/**
 * @author Ferra13671
 * @LastUpdate 1.1
 */

public class GLTexture {
    private int texId;

    private int width;
    private int height;

    private GLTexture _fromPath(String path, PathMode pathMode, ColorMode colorMode) {
        try {
            InputStream stream;
            if (pathMode == PathMode.INSIDEJAR)
                stream = GLTexture.class.getClassLoader().getResourceAsStream(path);
            else
                stream = Files.newInputStream(Paths.get(path));

            _fromInputStream(stream, colorMode);
        } catch (IOException e) {
            _fromBufferedImage(genNotFoundBufferedImage());
        }
        return this;
    }

    private GLTexture _fromInputStream(InputStream stream, ColorMode colorMode) {
        try {
            glController.run(() -> {
                texId = glController.genTexId();
                try {
                    genImage(stream, colorMode);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate a GL texture");
                }
            });
        } catch (Exception e) {
            _fromBufferedImage(genNotFoundBufferedImage());
        }
        return this;
    }

    private GLTexture _fromBufferedImage(BufferedImage bufferedImage) {
        glController.run(() -> {
            texId = glController.genTexId();

            int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    int pixel = pixels[y * bufferedImage.getWidth() + x];
                    byteBuffer.put((byte) ((pixel >> 16) & 0xFF));
                    byteBuffer.put((byte) ((pixel >> 8) & 0xFF));
                    byteBuffer.put((byte) (pixel & 0xFF));
                    byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            byteBuffer.flip();

            this.width = bufferedImage.getWidth();
            this.height = bufferedImage.getHeight();

            glController.bindTexture(texId);
            glController.texParameter(3553, 33085, 0);
            glController.texParameter(3553, 33082, 0);
            glController.texParameter(3553, 33083, 0);
            glController.texParameter(3553, 34049, 0.0F);

            glController.texImage2D(3553, 0, DefaultGlController.GL_RGBA, width, height, 0, DefaultGlController.GL_RGBA, 5121, null);
            glController.pixelStore(3314, 0);
            glController.pixelStore(3316, 0);
            glController.pixelStore(3315, 0);
            glController.pixelStore(3317, 4);
            glController.texSubImage2D(3553, 0, 0, 0, width, height, DefaultGlController.GL_RGBA, 5121, MemoryUtil.memAddress(byteBuffer));

            glController.bindTexture(0);

            MemoryUtil.memFree(byteBuffer);
        });
        return this;
    }

    private void genImage(InputStream stream, ColorMode colorMode) throws IOException {
        ByteBuffer buffer = StreamUtils.readStream(stream);
        buffer.rewind();
        tryGenerate(buffer, stream, memoryStack -> {
            IntBuffer xBuffer = memoryStack.mallocInt(1);
            IntBuffer yBuffer = memoryStack.mallocInt(1);
            IntBuffer channelsBuffer = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer = STBImage.stbi_load_from_memory(buffer, xBuffer, yBuffer, channelsBuffer, colorMode == ColorMode.RGBA ? 4 : 3);

            if (byteBuffer != null) {

                this.width = xBuffer.get(0);
                this.height = yBuffer.get(0);

                glController.bindTexture(texId);
                glController.texParameter(3553, 33085, 0);
                glController.texParameter(3553, 33082, 0);
                glController.texParameter(3553, 33083, 0);
                glController.texParameter(3553, 34049, 0.0F);

                glController.texImage2D(3553, 0, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, width, height, 0, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, 5121, null);
                glController.pixelStore(3314, 0);
                glController.pixelStore(3316, 0);
                glController.pixelStore(3315, 0);
                glController.pixelStore(3317, colorMode == ColorMode.RGBA ? 4 : 3);
                glController.texSubImage2D(3553, 0, 0, 0, width, height, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, 5121, MemoryUtil.memAddress(byteBuffer));

                glController.bindTexture(0);

                nstbi_image_free(MemoryUtil.memAddress(byteBuffer));
            }
        });
    }

    private BufferedImage genNotFoundBufferedImage() {
        BufferedImage nf = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D graphics = (Graphics2D) nf.getGraphics();

        graphics.setBackground(Color.DARK_GRAY);
        graphics.clearRect(0, 0, 64, 64);

        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, 32, 32);
        graphics.fillRect(32, 32, 32, 32);

        BufferedImage nf2 = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);

        graphics = (Graphics2D) nf2.getGraphics();

        graphics.drawImage(nf, 0, 0, null);
        graphics.drawImage(nf, 64, 0, null);
        graphics.drawImage(nf, 64, 64, null);
        graphics.drawImage(nf, 0, 64, null);

        nf.flush();

        return nf2;
    }

    private void tryGenerate(ByteBuffer buffer, InputStream stream, Consumer<MemoryStack> consumer) throws IOException {
        try {
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                consumer.accept(memoryStack);
            } catch (Throwable var9) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if (memoryStack != null) {
                memoryStack.close();
            }
        } finally {
            MemoryUtil.memFree(buffer);
            if (stream != null)
                stream.close();
        }
    }

    /**
     * Removes the texture from video memory and returns null.
     * Use this for times when the texture is no longer needed to free up some video memory.
     *
     * @return   null.
     */
    public GLTexture deleteTexture() {
        glController.deleteTexture(texId);
        return null;
    }

    public void bind() {
        glController.bindTexture(getTexId());
    }

    public void unBind() {
        glController.bindTexture(0);
    }

    public int getHeight() {
        return height;
    }

    public int getTexId() {
        return this.texId;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Loads an image into the texture along the specified path, color mode and in the specified mode.
     *
     * @param path   Paths to the image file.
     * @param pathMode   Select how the image path will be treated: inside the jar file or outside.
     * @param colorMode    Image color mode.
     */
    public static GLTexture fromPath(String path, PathMode pathMode, ColorMode colorMode) {
        return new GLTexture()._fromPath(path, pathMode, colorMode);
    }

    /**
     * Loads an image into the texture using the specified BufferedImage.
     *
     * @param bufferedImage   BufferedImage that will be loaded into the texture.
     */
    public static GLTexture fromBufferedImage(BufferedImage bufferedImage) {
        return new GLTexture()._fromBufferedImage(bufferedImage);
    }

    /**
     * Loads an image into the texture using the specified URL link.
     *
     * @param url   URL link to the image that will be loaded into the texture.
     */
    public static GLTexture fromURL(URL url) {
        try {
            return new GLTexture()._fromBufferedImage(ImageIO.read(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads an image into the texture along the specified InputStream and in the specified mode.
     *
     * @param stream   Image InputStream.
     * @param colorMode    Image color mode.
     */
    public static GLTexture fromInputStream(InputStream stream, ColorMode colorMode) {
        return new GLTexture()._fromInputStream(stream, colorMode);
    }

    public enum ColorMode {
        RGB,
        RGBA
    }
}
