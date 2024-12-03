package com.ferra13671.TextureUtils;

import com.ferra13671.TextureUtils.Controller.DefaultGlController;
import com.ferra13671.TextureUtils.Utils.StreamUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.ferra13671.TextureUtils.GLTextureSystem.glController;
import static org.lwjgl.stb.STBImage.nstbi_image_free;

/**
 * @author Ferra13671
 * @LastUpdate 1.0
 */

public class GLTexture {
    private int texId;

    private int width;
    private int height;

    /**
     * Loads an image into the texture along the specified path and in the specified mode.
     *
     * @param path   Paths to the image file.
     * @param pathMode   Select how the image path will be treated: inside the jar file or outside.
     */
    public GLTexture(String path, PathMode pathMode, ColorMode colorMode) {
        glController.run(() -> {
            texId = glController.genTexId();

            try {
                InputStream stream;
                if (pathMode == PathMode.INSIDEJAR)
                    stream = GLTexture.class.getClassLoader().getResourceAsStream(path);
                else
                    stream = Files.newInputStream(Paths.get(path));

                genImage(stream, colorMode);  //I don't know if it will work consistently forever, but, it's working so far :/
            } catch (IOException | IllegalArgumentException e) {

                //BufferedImage img = genNotFoundImage();
                //this.width = img.getWidth();                              Nah, lol.
                //this.height = img.getHeight();

                //generateImage(img, colorMode);
            }
        });
    }

    public void genImage(InputStream stream, ColorMode colorMode) throws IOException {
        ByteBuffer buffer = null;
        try {
            buffer = StreamUtils.readStream(stream);
            buffer.rewind();

            MemoryStack memoryStack = MemoryStack.stackPush();

            try {

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

    public enum ColorMode {
        RGB,
        RGBA
    }
}
