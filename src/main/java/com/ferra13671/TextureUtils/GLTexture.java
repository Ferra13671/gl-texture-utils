package com.ferra13671.TextureUtils;

import com.ferra13671.TextureUtils.Controller.DefaultGlController;
import com.ferra13671.TextureUtils.Utils.StreamUtils;
import org.lwjgl.opengl.GL11;
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
 * @LastUpdate 1.5.1
 */

public class GLTexture implements GlTex {
    protected int texId;

    protected int width;
    protected int height;

    protected GLTexture() {
        GLTextureSystem.ALL_TEXTURES.add(this);
    }

    private GLTexture _fromPath(String path, PathMode pathMode, ColorMode colorMode, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        try {
            InputStream stream;
            if (pathMode == PathMode.INSIDEJAR)
                stream = GLTexture.class.getClassLoader().getResourceAsStream(path);
            else
                stream = Files.newInputStream(Paths.get(path));

            _fromInputStream(stream, colorMode, textureFiltering, textureWrapping);
        } catch (IOException e) {
            _fromBufferedImage(genNotFoundBufferedImage(), textureFiltering, textureWrapping);
        }
        return this;
    }

    private GLTexture _fromInputStream(InputStream stream, ColorMode colorMode, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        try {
            glController.run(() -> {
                texId = glController.genTexId();
                try {
                    genImage(stream, colorMode, textureFiltering, textureWrapping);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate a GL texture");
                }
            });
        } catch (Exception e) {
            _fromBufferedImage(genNotFoundBufferedImage(), textureFiltering, textureWrapping);
        }
        return this;
    }

    private GLTexture _fromBufferedImage(BufferedImage bufferedImage, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        glController.run(() -> {
            texId = glController.genTexId();

            int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());

            ByteBuffer byteBuffer = MemoryUtil.memAlloc((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

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
            glController.texParameter(GL11.GL_TEXTURE_2D, 33085, 0);
            glController.texParameter(GL11.GL_TEXTURE_2D, 33082, 0);
            glController.texParameter(GL11.GL_TEXTURE_2D, 33083, 0);
            glController.texParameter(GL11.GL_TEXTURE_2D, 34049, 0.0F);
            applyFiltering(textureFiltering);
            applyWrapping(textureWrapping);

            glController.texImage2D(GL11.GL_TEXTURE_2D, 0, DefaultGlController.GL_RGBA, width, height, 0, DefaultGlController.GL_RGBA, 5121, null);
            glController.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
            glController.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            glController.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            glController.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
            glController.texSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, DefaultGlController.GL_RGBA, 5121, MemoryUtil.memAddress(byteBuffer));

            glController.bindTexture(0);

            MemoryUtil.memFree(byteBuffer);
        });
        return this;
    }

    private void genImage(InputStream stream, ColorMode colorMode, TextureFiltering textureFiltering, TextureWrapping textureWrapping) throws IOException {
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
                glController.texParameter(GL11.GL_TEXTURE_2D, 33085, 0);
                glController.texParameter(GL11.GL_TEXTURE_2D, 33082, 0);
                glController.texParameter(GL11.GL_TEXTURE_2D, 33083, 0);
                glController.texParameter(GL11.GL_TEXTURE_2D, 34049, 0.0F);
                applyFiltering(textureFiltering);
                applyWrapping(textureWrapping);

                glController.texImage2D(GL11.GL_TEXTURE_2D, 0, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, width, height, 0, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, 5121, null);
                glController.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
                glController.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
                glController.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
                glController.pixelStore(GL11.GL_UNPACK_ALIGNMENT, colorMode == ColorMode.RGBA ? 4 : 3);
                glController.texSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, colorMode == ColorMode.RGBA ? DefaultGlController.GL_RGBA : DefaultGlController.GL_RGB, 5121, MemoryUtil.memAddress(byteBuffer));

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

    private void applyFiltering(TextureFiltering textureFiltering) {
        glController.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureFiltering.id);
        glController.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureFiltering.id);
    }

    private void applyWrapping(TextureWrapping textureWrapping) {
        glController.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrapping.id);
        glController.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrapping.id);
    }

    /**
     * Removes the texture from video memory and returns null.
     * Use this for times when the texture is no longer needed to free up some video memory.
     */
    @Override
    public void delete() {
        glController.deleteTexture(texId);
        GLTextureSystem.ALL_TEXTURES.remove(this);
    }

    @Override
    public void bind() {
        glController.bindTexture(getTexId());
    }

    @Override
    public void unBind() {
        glController.bindTexture(0);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getTexId() {
        return this.texId;
    }

    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Loads an image into the texture along the specified path, color mode and in the specified mode.
     *
     * @param path   Paths to the image file.
     * @param pathMode   Select how the image path will be treated: inside the jar file or outside.
     * @param colorMode    Image color mode.
     * @param textureFiltering    Texture filter type.
     * @param textureWrapping    Texture wrap type.
     */
    public static GLTexture fromPath(String path, PathMode pathMode, ColorMode colorMode, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        return new GLTexture()._fromPath(path, pathMode, colorMode, textureFiltering, textureWrapping);
    }

    /**
     * Loads an image into the texture using the specified BufferedImage.
     *
     * @param bufferedImage   BufferedImage that will be loaded into the texture.
     * @param textureFiltering    Texture filter type.
     * @param textureWrapping    Texture wrap type.
     */
    public static GLTexture fromBufferedImage(BufferedImage bufferedImage, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        return new GLTexture()._fromBufferedImage(bufferedImage, textureFiltering, textureWrapping);
    }

    /**
     * Loads an image into the texture using the specified URL link.
     *
     * @param url   URL link to the image that will be loaded into the texture.
     * @param textureFiltering    Texture filter type.
     * @param textureWrapping    Texture wrap type.
     */
    public static GLTexture fromURL(URL url, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        try {
            return new GLTexture()._fromBufferedImage(ImageIO.read(url), textureFiltering, textureWrapping);
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
     * @param textureFiltering    Texture filter type.
     * @param textureWrapping    Texture wrap type.
     */
    public static GLTexture fromInputStream(InputStream stream, ColorMode colorMode, TextureFiltering textureFiltering, TextureWrapping textureWrapping) {
        return new GLTexture()._fromInputStream(stream, colorMode, textureFiltering, textureWrapping);
    }

    public enum ColorMode {
        RGB,
        RGBA
    }
}
