package com.ferra13671.TextureUtils.builder;

import java.util.List;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public class GLGifInfo {
    private final List<GLTextureInfo> textures;
    private final int delay;

    public GLGifInfo(List<GLTextureInfo> textures, int delay) {
        this.textures = textures;
        this.delay = delay;
    }

    public List<GLTextureInfo> getTextures() {
        return textures;
    }

    public int getDelay() {
        return delay;
    }
}
