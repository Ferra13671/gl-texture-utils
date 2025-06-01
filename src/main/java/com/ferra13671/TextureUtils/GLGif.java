package com.ferra13671.TextureUtils;

import com.ferra13671.TextureUtils.GifUtils.GifData;
import com.ferra13671.TextureUtils.GifUtils.GifUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ferra13671
 * @LastUpdate 1.5
 */

public class GLGif implements GlTex {
    private int updateDelayMillis;
    private long lastTime;
    private final List<GLTexture> textures = new ArrayList<>();
    private Iterator<GLTexture> iterator;
    private GLTexture currentTexture;


    protected void _fromInputStream(InputStream inputStream, DecompileMode decompileMode) throws IOException {
        lastTime = System.currentTimeMillis();
        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
        GifData gifData = decompileMode == DecompileMode.DELTAS ? GifUtils.decompileDeltas(iis) : GifUtils.decompileFull(iis);
        updateDelayMillis = gifData.updateDelay;

        for (BufferedImage bufferedImage : gifData.images)
            textures.add(GLTexture.fromBufferedImage(bufferedImage, TextureFiltering.DEFAULT, TextureWrapping.DEFAULT));
        iterator = textures.iterator();
    }

    public void update() {
        if (System.currentTimeMillis() - lastTime >= updateDelayMillis) {
            if (!iterator.hasNext()) iterator = textures.iterator();
            currentTexture = iterator.next();
            lastTime = System.currentTimeMillis();
        }
    }

    @Override
    public void delete() {
        for (GLTexture glTexture : textures) {
            glTexture.delete();
        }
        textures.clear();
    }

    @Override
    public void bind() {
        currentTexture.bind();
    }

    @Override
    public void unBind() {
        currentTexture.unBind();
    }

    @Override
    public int getTexId() {
        return currentTexture.getTexId();
    }

    @Override
    public int getWidth() {
        return currentTexture.getWidth();
    }

    @Override
    public int getHeight() {
        return currentTexture.getHeight();
    }

    public int getUpdateDelayMillis() {
        return updateDelayMillis;
    }

    public void setUpdateDelayMillis(int updateDelayMillis) {
        this.updateDelayMillis = updateDelayMillis;
    }

    public static GLGif fromFile(File file, DecompileMode decompileMode) {
        try {
            GLGif glGif = new GLGif();
            glGif._fromInputStream(Files.newInputStream(file.toPath()), decompileMode);
            return glGif;
        } catch (IOException e) {
            return null;
        }
    }

    public static GLGif fromInputStream(InputStream inputStream, DecompileMode decompileMode) {
        try {
            GLGif glGif = new GLGif();
            glGif._fromInputStream(inputStream, decompileMode);
            return glGif;
        } catch (IOException e) {
            return null;
        }
    }

    public enum DecompileMode {
        DELTAS,
        FULL
    }
}
